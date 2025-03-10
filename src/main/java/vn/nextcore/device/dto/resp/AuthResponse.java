package vn.nextcore.device.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class AuthResponse {
    private String accessToken;

    private String refreshToken;

    private Long expireIn;

    private Long roleId;
}
