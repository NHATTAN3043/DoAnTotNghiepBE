package vn.nextcore.device.dto.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserRequest {
    @NotBlank(message = "ER008")
    private String userName;

    @NotBlank(message = "ER001")
    private String email;

    private String phoneNumber;

    private String gender;

    private String dateOfBirth;

    private String address;

    private MultipartFile avatar;

    @NotBlank(message = "ER002")
    private String password;

    @NotNull(message = "ER138")
    private Long departmentId;

    @NotNull(message = "ER137")
    private Long roleId;
}
