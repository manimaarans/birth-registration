package digit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.Configuration;
import digit.repository.ServiceRequestRepository;
import digit.web.models.BirthRegistrationApplication;
import digit.web.models.BirthRegistrationRequest;
import digit.web.models.BusinessService;
import digit.web.models.BusinessServiceResponse;
import digit.web.models.ProcessInstance;
import digit.web.models.ProcessInstanceRequest;
import digit.web.models.ProcessInstanceResponse;
import digit.web.models.RequestInfoWrapper;
import digit.web.models.State;
import digit.web.models.User;
import digit.web.models.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class WorkflowService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ServiceRequestRepository repository;

    @Autowired
    private Configuration config;

    public void updateWorkflowStatus(BirthRegistrationRequest birthRegistrationRequest) {
        birthRegistrationRequest.getBirthRegistrationApplications().forEach(application -> {
            ProcessInstance processInstance = getProcessInstanceForBTR(application, birthRegistrationRequest.getRequestInfo());
            ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(birthRegistrationRequest.getRequestInfo(), Collections.singletonList(processInstance));
            callWorkFlow(workflowRequest);
        });
    }

    public State callWorkFlow(ProcessInstanceRequest workflowReq) {

        ProcessInstanceResponse response = null;
        StringBuilder url = new StringBuilder(config.getWfHost().concat(config.getWfTransitionPath()));
        Object optional = repository.fetchResult(url, workflowReq);
        response = mapper.convertValue(optional, ProcessInstanceResponse.class);
        return response.getProcessInstances().get(0).getState();
    }

    private ProcessInstance getProcessInstanceForBTR(BirthRegistrationApplication application, RequestInfo requestInfo) {
        Workflow workflow = application.getWorkflow();

        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setBusinessId(application.getApplicationNumber());
        processInstance.setAction(workflow.getAction());
        processInstance.setModuleName("birth-services");
        processInstance.setTenantId(application.getTenantId());
        processInstance.setBusinessService("BTR");
        processInstance.setDocuments(workflow.getDocuments());
        processInstance.setComment(workflow.getComment());

        if(!CollectionUtils.isEmpty(workflow.getAssignees())){
            List<User> users = new ArrayList<>();

            workflow.getAssignees().forEach(uuid -> {
                User user = new User();
                user.setUuid(uuid);
                users.add(user);
            });

            processInstance.setAssignes(users);
        }

        return processInstance;

    }

    public ProcessInstance getCurrentWorkflow(RequestInfo requestInfo, String tenantId, String businessId) {

        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

        StringBuilder url = getSearchURLWithParams(tenantId, businessId, true);

        Object res = repository.fetchResult(url, requestInfoWrapper);
        ProcessInstanceResponse response = null;

        try{
            response = mapper.convertValue(res, ProcessInstanceResponse.class);
        }
        catch (Exception e){
            throw new CustomException("PARSING_ERROR","Failed to parse workflow search response");
        }

        if(response!=null && !CollectionUtils.isEmpty(response.getProcessInstances()) && response.getProcessInstances().get(0)!=null)
            return response.getProcessInstances().get(0);

        return null;
    }

    private BusinessService getBusinessService(BirthRegistrationApplication application, RequestInfo requestInfo) {
        String tenantId = application.getTenantId();
        StringBuilder url = getSearchURLWithParams(tenantId, "BTR", false);
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
        Object result = repository.fetchResult(url, requestInfoWrapper);
        BusinessServiceResponse response = null;
        try {
            response = mapper.convertValue(result, BusinessServiceResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException("PARSING ERROR", "Failed to parse response of workflow business service search");
        }

        if (CollectionUtils.isEmpty(response.getBusinessServices()))
            throw new CustomException("BUSINESSSERVICE_NOT_FOUND", "The businessService " + "BTR" + " is not found");

        return response.getBusinessServices().get(0);
    }

    private StringBuilder getSearchURLWithParams(String tenantId, String businessService, boolean isProcess) {

        StringBuilder url = new StringBuilder(config.getWfHost());
        url.append(isProcess? config.getWfProcessInstanceSearchPath() : config.getWfBusinessServiceSearchPath());
        url.append("?tenantId=");
        url.append(tenantId);
        if(isProcess) {
            url.append("&businessIds=");
        } else {
            url.append("&businessServices=");
        }
        url.append(businessService);
        return url;
    }

    public ProcessInstanceRequest getProcessInstanceForBirthRegistrationPayment(BirthRegistrationRequest updateRequest) {

        BirthRegistrationApplication application = updateRequest.getBirthRegistrationApplications().get(0);

        ProcessInstance process = ProcessInstance.builder()
                .businessService("BTR")
                .businessId(application.getApplicationNumber())
                .comment("Payment for birth registration processed")
                .moduleName("birth-services")
                .tenantId(application.getTenantId())
                .action("PAY")
                .build();

        return ProcessInstanceRequest.builder()
                .requestInfo(updateRequest.getRequestInfo())
                .processInstances(Arrays.asList(process))
                .build();

    }
}