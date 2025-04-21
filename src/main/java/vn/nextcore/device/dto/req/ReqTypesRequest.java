package vn.nextcore.device.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ReqTypesRequest {
    @NotBlank(message = "ER125")
    @Pattern(regexp = "^[1-9]\\d*$", message = "ER125")
    private Long groupId;

    @NotBlank(message = "ER126")
    @Min(value = 1, message = "ER126")
    @Pattern(regexp = "^[1-9]\\d*$", message = "ER126")
    private int quantity;
}
