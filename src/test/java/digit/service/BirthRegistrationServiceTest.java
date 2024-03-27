package digit.service;

import digit.enrichment.BirthApplicationEnrichment;
import digit.kafka.Producer;
import digit.repository.BirthRegistrationRepository;
import digit.validator.BirthApplicationValidator;
import digit.web.models.BirthApplicationSearchCriteria;
import digit.web.models.BirthRegistrationApplication;
import digit.web.models.BirthRegistrationRequest;
import digit.web.models.ProcessInstance;
import digit.web.models.State;
import org.egov.common.contract.request.RequestInfo;
import org.junit.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

@RunWith(SpringRunner.class)
public class BirthRegistrationServiceTest {

    @InjectMocks
    BirthRegistrationService birthRegistrationService;
    @Mock
    private BirthApplicationValidator validator;

    @Mock
    private BirthApplicationEnrichment enrichmentUtil;

    @Mock
    private UserService userService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private BirthRegistrationRepository birthRegistrationRepository;

    @Mock
    private Producer producer;

    @Test
    public void shouldRegisterBTRequest() {
        BirthRegistrationRequest birthRegistrationRequest = new BirthRegistrationRequest();
        birthRegistrationService.registerBtRequest(birthRegistrationRequest);
        verify(validator, times(1)).validateBirthApplication(any(BirthRegistrationRequest.class));
        verify(enrichmentUtil, times(1)).enrichBirthApplication(any(BirthRegistrationRequest.class));
        verify(userService, times(1)).callUserService(any(BirthRegistrationRequest.class));
        verify(workflowService, times(1)).updateWorkflowStatus(any(BirthRegistrationRequest.class));
        verify(producer, times(1)).push(contains("save-bt-application"), any(BirthRegistrationRequest.class));
    }

    @Test
    public void shouldSearchBTRApplications() {
        BirthRegistrationApplication birthRegistrationApplication = new BirthRegistrationApplication();
        when(birthRegistrationRepository.getApplications(any())).thenReturn(Collections.singletonList(birthRegistrationApplication));
        when(workflowService.getCurrentWorkflow(any(), any(), any())).thenReturn(ProcessInstance.builder().state(State.builder().state("test").build()).build());
        birthRegistrationService.searchBtApplications(new RequestInfo(), new BirthApplicationSearchCriteria());
        verify(birthRegistrationRepository, times(1)).getApplications(any(BirthApplicationSearchCriteria.class));
        verify(enrichmentUtil, times(1)).enrichFatherApplicantOnSearch(any(BirthRegistrationApplication.class));
        verify(enrichmentUtil, times(1)).enrichMotherApplicantOnSearch(any(BirthRegistrationApplication.class));
        verify(workflowService, times(1)).getCurrentWorkflow(any(RequestInfo.class), any(), any());

    }
    @Test
    public void shouldUpdateBTRApplications() {
        BirthRegistrationRequest birthRegistrationRequest = new BirthRegistrationRequest();
        BirthRegistrationApplication birthRegistrationApplication = new BirthRegistrationApplication();
        birthRegistrationRequest.setBirthRegistrationApplications(Collections.singletonList(birthRegistrationApplication));

        when(validator.validateApplicationExistence(any())).thenReturn(birthRegistrationApplication);
        birthRegistrationService.updateBtApplication(birthRegistrationRequest);
        verify(workflowService, times(1)).updateWorkflowStatus(any(BirthRegistrationRequest.class));
        verify(enrichmentUtil, times(1)).enrichBirthApplicationUponUpdate(any(BirthRegistrationRequest.class));
        verify(producer, times(1)).push(contains("update-bt-application"), any(BirthRegistrationRequest.class));

    }


}