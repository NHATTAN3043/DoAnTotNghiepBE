package vn.nextcore.device.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank(message = "ER001")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@nextcore\\.vn$", message = "ER003")
    @Size(max = 100, message = "ER006")
    @Schema(example = "abc123@nextcore.vn")
    private String email;

    @NotBlank(message = "ER002")
    @Size(min = 8, max = 50, message = "ER004")
    @Schema(example = "12345678")
    private String password;
}
