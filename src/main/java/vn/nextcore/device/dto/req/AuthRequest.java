package vn.nextcore.device.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank(message = "ER001")
//    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$\n", message = "ER003")
    @Size(max = 100, message = "ER006")
    @Schema(example = "admin@gmail.com")
    private String email;

    @NotBlank(message = "ER002")
    @Size(min = 8, max = 50, message = "ER004")
    @Schema(example = "12345678")
    private String password;
}
