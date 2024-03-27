package digit.enrichment;

import digit.service.UserService;

import digit.util.IdgenUtil;
import digit.util.UserUtil;
import digit.web.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
@Component
@Slf4j
public class BirthApplicationEnrichment {

    @Autowired
    private IdgenUtil idgenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtil userUtils;

    public void enrichBirthApplication(BirthRegistrationRequest birthRegistrationRequest) {
        List<String> birthRegistrationIdList = idgenUtil.getIdList(birthRegistrationRequest.getRequestInfo(), birthRegistrationRequest.getBirthRegistrationApplications().get(0).getTenantId(), "btr.id", "", birthRegistrationRequest.getBirthRegistrationApplications().size());
        Integer index = 0;
        for(BirthRegistrationApplication application : birthRegistrationRequest.getBirthRegistrationApplications()){
            // Enrich audit details
            AuditDetails auditDetails = AuditDetails.builder().createdBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
            application.setAuditDetails(auditDetails);

            // Enrich UUID
            application.setId(UUID.randomUUID().toString());

//            application.getFather().setId(application.getId());
//            application.getMother().setId(application.getId());

            // Enrich registration Id
            application.getAddress().setApplicationNumber(application.getId());

            // Enrich address UUID
            application.getAddress().setId(UUID.randomUUID().toString());

            //Enrich application number from IDgen
            application.setApplicationNumber(birthRegistrationIdList.get(index++));

        }
    }

    public void enrichBirthApplicationUponUpdate(BirthRegistrationRequest birthRegistrationRequest) {
        // Enrich lastModifiedTime and lastModifiedBy in case of update
        birthRegistrationRequest.getBirthRegistrationApplications().get(0).getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
        birthRegistrationRequest.getBirthRegistrationApplications().get(0).getAuditDetails().setLastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
    }

    public void enrichFatherApplicantOnSearch(BirthRegistrationApplication application) {
        // enrich father applicant details from userservice
        UserDetailResponse fatherUserResponse = userService.searchUser(userUtils.getStateLevelTenant(application.getTenantId()),application.getFather().getId(),null);
        User fatherUser = fatherUserResponse.getUser().get(0);
        log.info(fatherUser.toString());
        Applicant fatherApplicant = Applicant.builder().aadhaarNumber(fatherUser.getAadhaarNumber())
                .accountLocked(fatherUser.getAccountLocked())
                .active(fatherUser.getActive())
                .altContactNumber(fatherUser.getAltContactNumber())
                .bloodGroup(fatherUser.getBloodGroup())
                .correspondenceAddress(fatherUser.getCorrespondenceAddress())
                .correspondenceCity(fatherUser.getCorrespondenceCity())
                .correspondencePincode(fatherUser.getCorrespondencePincode())
                .gender(fatherUser.getGender())
                .id(fatherUser.getUuid())
                .name(fatherUser.getName())
                .type(fatherUser.getType())
                .roles(fatherUser.getRoles()).build();
        application.setFather(fatherApplicant);
    }

    public void enrichMotherApplicantOnSearch(BirthRegistrationApplication application) {
        // enrich mother applicant details from userservice
        UserDetailResponse motherUserResponse = userService.searchUser(userUtils.getStateLevelTenant(application.getTenantId()),application.getFather().getId(),null);
        User motherUser = motherUserResponse.getUser().get(0);
        log.info(motherUser.toString());
        Applicant motherApplicant = Applicant.builder().aadhaarNumber(motherUser.getAadhaarNumber())
                .accountLocked(motherUser.getAccountLocked())
                .active(motherUser.getActive())
                .altContactNumber(motherUser.getAltContactNumber())
                .bloodGroup(motherUser.getBloodGroup())
                .correspondenceAddress(motherUser.getCorrespondenceAddress())
                .correspondenceCity(motherUser.getCorrespondenceCity())
                .correspondencePincode(motherUser.getCorrespondencePincode())
                .gender(motherUser.getGender())
                .id(motherUser.getUuid())
                .name(motherUser.getName())
                .type(motherUser.getType())
                .roles(motherUser.getRoles()).build();
        application.setMother(motherApplicant);
    }
}