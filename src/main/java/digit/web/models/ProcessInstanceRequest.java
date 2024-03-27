package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.egov.common.contract.request.RequestInfo;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProcessInstanceRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("ProcessInstances")
    @Valid
    @NotNull
    private List<ProcessInstance> processInstances;


    public ProcessInstanceRequest addProcessInstanceItem(ProcessInstance processInstanceItem) {
        if (this.processInstances == null) {
            this.processInstances = new ArrayList<>();
        }
        this.processInstances.add(processInstanceItem);
        return this;
    }

}