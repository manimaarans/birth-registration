package digit.enrichment;

import digit.service.UserService;
import digit.util.IdgenUtil;
import digit.util.UserUtil;
import digit.web.models.BirthApplicationAddress;
import digit.web.models.BirthRegistrationApplication;
import digit.web.models.BirthRegistrationRequest;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

@RunWith(SpringRunner.class)
public class BirthApplicationEnrichmentTest {

    @InjectMocks
    BirthApplicationEnrichment birthApplicationEnrichment;

    @Mock
    private IdgenUtil idgenUtil;

    @Mock
    private UserService userService;

    @Mock
    private UserUtil userUtils;

    @Test
    public void ShouldEnrichBirthApplicationTest() {
        BirthRegistrationRequest birthRegistrationRequest = new BirthRegistrationRequest();
        RequestInfo requestInfo = new RequestInfo();
        User user = User.builder().uuid("mockuuid").build();
        requestInfo.setUserInfo(user);
        birthRegistrationRequest.setRequestInfo(requestInfo);
        BirthRegistrationApplication birthRegistrationApplication = new BirthRegistrationApplication();
        birthRegistrationApplication.setTenantId("pg");
        BirthApplicationAddress birthApplicationAddress = BirthApplicationAddress.builder().build();
        birthRegistrationApplication.setAddress(birthApplicationAddress);

        birthRegistrationRequest.setBirthRegistrationApplications(Arrays.asList(birthRegistrationApplication));
        when(idgenUtil.getIdList(any(), any(),
                any(), any(), any())).thenReturn(Collections.singletonList("BTR-2024-03-20-000001"));

        birthApplicationEnrichment.enrichBirthApplication(birthRegistrationRequest);
        Assert.assertEquals("BTR-2024-03-20-000001", birthRegistrationRequest.getBirthRegistrationApplications().get(0).getApplicationNumber());
        Assert.assertEquals("mockuuid", birthRegistrationRequest.getBirthRegistrationApplications().get(0).getAuditDetails().getCreatedBy());
    }

}