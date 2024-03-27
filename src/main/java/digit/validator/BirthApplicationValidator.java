package digit.validator;

import digit.repository.BirthRegistrationRepository;
import digit.web.models.BirthApplicationSearchCriteria;
import digit.web.models.BirthRegistrationApplication;
import digit.web.models.BirthRegistrationRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static digit.config.ServiceConstants.NO_ENTITY_FOUND_ERROR;
import static digit.config.ServiceConstants.REPOSITORY_ERROR;

@Component
public class BirthApplicationValidator {

    @Autowired
    private BirthRegistrationRepository repository;

    public void validateBirthApplication(BirthRegistrationRequest birthRegistrationRequest) {
        birthRegistrationRequest.getBirthRegistrationApplications().forEach(application -> {
            String errorCode = "EG_BT_APP_ERR";
            String errorMessage = "%s is mandatory for creating birth registration applications";
            if (ObjectUtils.isEmpty(application.getTenantId()))
                throw new CustomException(errorCode, String.format(errorMessage, "tenantId" ));
            if (ObjectUtils.isEmpty(application.getFather()))
                throw new CustomException(errorCode, String.format(errorMessage, "father" ));
            if (ObjectUtils.isEmpty(application.getMother()))
                throw new CustomException(errorCode, String.format(errorMessage, "mother" ));
            if (ObjectUtils.isEmpty(application.getBabyFirstName()))
                throw new CustomException(errorCode, String.format(errorMessage, "babyFirstName" ));
            if (ObjectUtils.isEmpty(application.getDoctorName()))
                throw new CustomException(errorCode, String.format(errorMessage, "doctorName" ));
            if (ObjectUtils.isEmpty(application.getHospitalName()))
                throw new CustomException(errorCode, String.format(errorMessage, "hospitalName" ));
            if (ObjectUtils.isEmpty(application.getPlaceOfBirth()))
                throw new CustomException(errorCode, String.format(errorMessage, "placeOfBirth" ));
        });
    }

    public BirthRegistrationApplication validateApplicationExistence(BirthRegistrationApplication birthRegistrationApplication) {
        List<BirthRegistrationApplication> birthRegistrationApplicationList =repository.getApplications(BirthApplicationSearchCriteria.builder().applicationNumber(birthRegistrationApplication.getApplicationNumber()).build());
        if(birthRegistrationApplicationList.isEmpty()) {
            throw new CustomException(REPOSITORY_ERROR, NO_ENTITY_FOUND_ERROR);
        }
        return birthRegistrationApplicationList.get(0);
    }
}