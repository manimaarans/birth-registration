package digit.web.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import digit.service.BirthRegistrationService;
import digit.util.ResponseInfoFactory;
import digit.web.models.BirthApplicationSearchCriteria;
import digit.web.models.BirthRegistrationApplication;
import digit.web.models.BirthRegistrationRequest;
import digit.web.models.BirthRegistrationResponse;
import digit.web.models.RequestInfoWrapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-03-13T10:35:14.500+05:30[Asia/Calcutta]")
@Controller
@RequestMapping("")
public class BirthApiController {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private BirthRegistrationService birthRegistrationService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    public BirthApiController(ObjectMapper objectMapper, HttpServletRequest request, BirthRegistrationService birthRegistrationService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.birthRegistrationService = birthRegistrationService;
    }

    @RequestMapping(value = "/birth/registration/v1/_create", method = RequestMethod.POST)
    public ResponseEntity<BirthRegistrationResponse> birthRegistrationV1CreatePost(@Parameter(in = ParameterIn.DEFAULT, description = "Details for the new Birth Registration Application(s) + RequestInfo meta data.", required = true, schema = @Schema()) @Valid @RequestBody BirthRegistrationRequest birthRegistrationRequest) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            List<BirthRegistrationApplication> applications = birthRegistrationService.registerBtRequest(birthRegistrationRequest);
            ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(birthRegistrationRequest.getRequestInfo(), true);
            BirthRegistrationResponse response = BirthRegistrationResponse.builder().birthRegistrationApplications(applications).responseInfo(responseInfo).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<BirthRegistrationResponse>(HttpStatus.NOT_IMPLEMENTED);
    }

    @RequestMapping(value = "/birth/registration/v1/_search", method = RequestMethod.POST)
    public ResponseEntity<BirthRegistrationResponse> birthRegistrationV1SearchPost(@Parameter(in = ParameterIn.DEFAULT, description = "Parameter to carry Request metadata in the request body", schema = @Schema()) @Valid @RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute BirthApplicationSearchCriteria birthApplicationSearchCriteria) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            List<BirthRegistrationApplication> applications = birthRegistrationService.searchBtApplications(requestInfoWrapper.getRequestInfo(), birthApplicationSearchCriteria);
            ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
            BirthRegistrationResponse response = BirthRegistrationResponse.builder().birthRegistrationApplications(applications).responseInfo(responseInfo).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<BirthRegistrationResponse>(HttpStatus.NOT_IMPLEMENTED);
    }

    @RequestMapping(value = "/birth/registration/v1/_update", method = RequestMethod.POST)
    public ResponseEntity<BirthRegistrationResponse> birthRegistrationV1UpdatePost(@Parameter(in = ParameterIn.DEFAULT, description = "Details for the new (s) + RequestInfo meta data.", required = true, schema = @Schema()) @Valid @RequestBody BirthRegistrationRequest birthRegistrationRequest) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {

            BirthRegistrationApplication application = birthRegistrationService.updateBtApplication(birthRegistrationRequest);

            ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(birthRegistrationRequest.getRequestInfo(), true);
            BirthRegistrationResponse response = BirthRegistrationResponse.builder().birthRegistrationApplications(Collections.singletonList(application)).responseInfo(responseInfo).build();
            return new ResponseEntity<>(response, HttpStatus.OK);

        }

        return new ResponseEntity<BirthRegistrationResponse>(HttpStatus.NOT_IMPLEMENTED);
    }

}
