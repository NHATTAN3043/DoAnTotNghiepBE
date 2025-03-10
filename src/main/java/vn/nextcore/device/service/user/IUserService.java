package vn.nextcore.device.service.user;

import jakarta.servlet.http.HttpServletRequest;
import vn.nextcore.device.dto.resp.UserResponse;

public interface IUserService {
    UserResponse getMe(HttpServletRequest request);
}
