package vn.nextcore.device.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.nextcore.device.dto.resp.DataResponse;
import vn.nextcore.device.dto.resp.UserResponse;
import vn.nextcore.device.service.user.IUserService;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @GetMapping("/getMe")
    public DataResponse<UserResponse> getMe(HttpServletRequest request) {
        UserResponse result = userService.getMe(request);
        return new DataResponse<>(result);
    }
}
