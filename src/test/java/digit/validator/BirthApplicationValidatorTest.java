package digit.validator;

import digit.repository.BirthRegistrationRepository;
import digit.web.models.Applicant;
import digit.web.models.BirthRegistrationApplication;
import digit.web.models.BirthRegistrationRequest;
import org.egov.tracer.model.CustomException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

@RunWith(SpringRunner.class)

public class BirthApplicationValidatorTest {
    @InjectMocks
    BirthApplicationValidator birthApplicationValidator;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private BirthRegistrationRepository birthRegistrationRepository;


    @Test
    public void shouldThrowExceptionWhenTenantIdIsEmpty() {
        BirthRegistrationApplication birthRegistrationApplication = createMockApplication(false, false, false,false, false, false, false);
        BirthRegistrationRequest birthRegistrationRequest = BirthRegistrationRequest.builder().birthRegistrationApplications(Collections.singletonList(birthRegistrationApplication)).build();
        thrown.expect(CustomException.class);
        thrown.expectMessage("tenantId is mandatory for creating birth registration applications");
        birthApplicationValidator.validateBirthApplication(birthRegistrationRequest);
    }
    @Test
    public void shouldThrowExceptionWhenFatherIsEmpty() {
        BirthRegistrationApplication birthRegistrationApplication = createMockApplication(true, false, false,false, false, false, false);
        BirthRegistrationRequest birthRegistrationRequest = BirthRegistrationRequest.builder().birthRegistrationApplications(Collections.singletonList(birthRegistrationApplication)).build();
        thrown.expect(CustomException.class);
        thrown.expectMessage("father is mandatory for creating birth registration applications");
        birthApplicationValidator.validateBirthApplication(birthRegistrationRequest);
    }

    @Test
    public void shouldThrowExceptionWhenMotherIsEmpty() {
        BirthRegistrationApplication birthRegistrationApplication = createMockApplication(true, true, false,false, false, false, false);
        BirthRegistrationRequest birthRegistrationRequest = BirthRegistrationRequest.builder().birthRegistrationApplications(Collections.singletonList(birthRegistrationApplication)).build();
        thrown.expect(CustomException.class);
        thrown.expectMessage("mother is mandatory for creating birth registration applications");
        birthApplicationValidator.validateBirthApplication(birthRegistrationRequest);
    }
    @Test
    public void shouldThrowExceptionWhenBabyFirstNameIsEmpty() {
        BirthRegistrationApplication birthRegistrationApplication = createMockApplication(true, true, true,false, false, false, false);
        BirthRegistrationRequest birthRegistrationRequest = BirthRegistrationRequest.builder().birthRegistrationApplications(Collections.singletonList(birthRegistrationApplication)).build();
        thrown.expect(CustomException.class);
        thrown.expectMessage("babyFirstName is mandatory for creating birth registration applications");
        birthApplicationValidator.validateBirthApplication(birthRegistrationRequest);
    }

    @Test
    public void shouldThrowExceptionWhenDoctorNameIsEmpty() {
        BirthRegistrationApplication birthRegistrationApplication = createMockApplication(true, true, true,true, false, false, false);
        BirthRegistrationRequest birthRegistrationRequest = BirthRegistrationRequest.builder().birthRegistrationApplications(Collections.singletonList(birthRegistrationApplication)).build();
        thrown.expect(CustomException.class);
        thrown.expectMessage("doctorName is mandatory for creating birth registration applications");
        birthApplicationValidator.validateBirthApplication(birthRegistrationRequest);
    }

    @Test
    public void shouldThrowExceptionWhenHospitalNameIsEmpty() {
        BirthRegistrationApplication birthRegistrationApplication = createMockApplication(true, true, true,true, true, false, false);
        BirthRegistrationRequest birthRegistrationRequest = BirthRegistrationRequest.builder().birthRegistrationApplications(Collections.singletonList(birthRegistrationApplication)).build();
        thrown.expect(CustomException.class);
        thrown.expectMessage("hospitalName is mandatory for creating birth registration applications");
        birthApplicationValidator.validateBirthApplication(birthRegistrationRequest);
    }

    @Test
    public void shouldThrowExceptionWhenPlaceOfBirthNameIsEmpty() {
        BirthRegistrationApplication birthRegistrationApplication = createMockApplication(true, true, true,true, true, true, false);
        BirthRegistrationRequest birthRegistrationRequest = BirthRegistrationRequest.builder().birthRegistrationApplications(Collections.singletonList(birthRegistrationApplication)).build();
        thrown.expect(CustomException.class);
        thrown.expectMessage("placeOfBirth is mandatory for creating birth registration applications");
        birthApplicationValidator.validateBirthApplication(birthRegistrationRequest);
    }

    @Test
    public void shouldNotThrowExceptionWhenRequiredFieldsArePresent() {
        BirthRegistrationApplication birthRegistrationApplication = createMockApplication(true, true, true,true, true, true, true);
        BirthRegistrationRequest birthRegistrationRequest = BirthRegistrationRequest.builder().birthRegistrationApplications(Collections.singletonList(birthRegistrationApplication)).build();
        Assertions.assertDoesNotThrow(() -> birthApplicationValidator.validateBirthApplication(birthRegistrationRequest));
    }

    @Test
    public  void shouldReturnApplicationWhenBirthRegistrationApplicationIsReturnedFromRepository() {
        BirthRegistrationApplication birthRegistrationApplication = createMockApplication(true, true, true,true, true, true, true);
        when(birthRegistrationRepository.getApplications(any())).thenReturn(createMockBirthRegistrationList(false));
        Assertions.assertDoesNotThrow(() -> birthApplicationValidator.validateApplicationExistence(birthRegistrationApplication));
    }

    @Test
    public  void shouldThrowExceptionWhenNoApplicationIsReturnedFromRepository() {
        BirthRegistrationApplication birthRegistrationApplication = createMockApplication(true, true, true,true, true, true, true);
        when(birthRegistrationRepository.getApplications(any())).thenReturn(createMockBirthRegistrationList(true));
        thrown.expect(CustomException.class);
        thrown.expectMessage("No entity found in database");
        birthApplicationValidator.validateApplicationExistence(birthRegistrationApplication);
    }



    private BirthRegistrationApplication createMockApplication(boolean addTenantId, boolean addFather, boolean addMother,
                                                               boolean addBabyFirstName, boolean addDoctorName, boolean addHospitalName,
                                                               boolean addPlaceOfBirth) {
        BirthRegistrationApplication birthRegistrationApplication = BirthRegistrationApplication.builder().build();
        if(addTenantId)
            birthRegistrationApplication.setTenantId("tenantId");
        if(addFather)
            birthRegistrationApplication.setFather(new Applicant());
        if(addMother)
            birthRegistrationApplication.setMother(new Applicant());
        if(addBabyFirstName)
            birthRegistrationApplication.setBabyFirstName("babyFirstName");
        if(addDoctorName)
            birthRegistrationApplication.setDoctorName("doctorName");
        if(addHospitalName)
            birthRegistrationApplication.setHospitalName("hospitalName");
        if(addPlaceOfBirth)
            birthRegistrationApplication.setPlaceOfBirth("placeOfBirth");

        return birthRegistrationApplication;
    }

    private List<BirthRegistrationApplication> createMockBirthRegistrationList(boolean isEmpty) {
        if(isEmpty) {
          return  new ArrayList<>();
        }
        return Collections.singletonList(new BirthRegistrationApplication());
    }

}