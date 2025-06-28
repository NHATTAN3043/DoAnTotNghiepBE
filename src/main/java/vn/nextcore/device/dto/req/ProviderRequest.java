package vn.nextcore.device.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProviderRequest {
    @NotBlank(message = "ER115")
    @Size(max = 250, message = "ER119")
    private String name;

    @NotBlank(message = "ER116")
    @Pattern(regexp = "/^(03|05|07|08|09|01[2689])[0-9]{8}$/", message = "ER117")
    private String phoneNumber;

    @NotBlank(message = "ER118")
    @Size(max = 250, message = "ER120")
    private String address;
}
