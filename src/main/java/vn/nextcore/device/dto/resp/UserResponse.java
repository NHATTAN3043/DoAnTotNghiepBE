package vn.nextcore.device.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private DepartmentResponse department;

    private List<DeviceResponse> deviceResponseList = new ArrayList<>();

    public UserResponse(Long id, String userName, String email, String avatarUrl) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    public UserResponse(Long id) {
        this.id = id;
    }
}
