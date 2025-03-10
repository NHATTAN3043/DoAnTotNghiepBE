package vn.nextcore.device.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserResponse {
    private Long id;

    private String userName;

    private String email;

    private String phoneNumber;

    private String gender;

    private String dateOfBirth;

    private String address;

    private String avatarUrl;

    private RoleResponse role;
}
