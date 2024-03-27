package digit.web.models;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import digit.web.models.Address;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;

/**
 * BirthApplicationAddress
 */
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-03-13T10:35:14.500+05:30[Asia/Calcutta]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BirthApplicationAddress {
    @JsonProperty("id")

    @Valid
    private String id = null;

    @JsonProperty("tenantId")
    @NotNull

    @Size(min = 2, max = 64)
    private String tenantId = null;

    @JsonProperty("applicationNumber")

    private String applicationNumber = null;

    @JsonProperty("applicantAddress")

    @Valid
    private Address applicantAddress = null;


}
