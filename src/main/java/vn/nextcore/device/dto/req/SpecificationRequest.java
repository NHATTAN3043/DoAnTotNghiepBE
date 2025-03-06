package vn.nextcore.device.dto.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class SpecificationRequest {
    @NotBlank(message = "ER036")
    private String name;

    @NotBlank(message = "ER037")
    private String value;
}
