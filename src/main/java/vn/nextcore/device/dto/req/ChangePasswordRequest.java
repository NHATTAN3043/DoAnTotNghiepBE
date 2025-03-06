package vn.nextcore.device.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "ER002")
    @Size(max = 50, min = 8, message = "ER114")
    private String password;

    @NotBlank(message = "ER113")
    @Size(max = 50, min = 8, message = "ER114")
    private String repeatPassword;
}
