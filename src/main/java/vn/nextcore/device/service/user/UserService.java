package vn.nextcore.device.service.user;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.nextcore.device.dto.resp.RoleResponse;
import vn.nextcore.device.dto.resp.UserResponse;
import vn.nextcore.device.entity.User;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.security.jwt.JwtUtil;

import java.text.SimpleDateFormat;

@Service
public class UserService implements  IUserService{
    @Autowired
    private JwtUtil jwtUtil;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public UserResponse getMe(HttpServletRequest request) {
        UserResponse userResponse = new UserResponse();
        try {
            User user = jwtUtil.extraUserFromRequest(request);
            userResponse.setId(user.getId());
            userResponse.setEmail(user.getEmail());
            userResponse.setAddress(user.getAddress());
            userResponse.setGender(user.getGender());
            userResponse.setUserName(user.getUserName());
            userResponse.setAvatarUrl(user.getAvatarUrl());
            userResponse.setPhoneNumber(user.getPhoneNumber());
            userResponse.setDateOfBirth(dateFormat.format(user.getDateOfBirth()));
            userResponse.setRole(new RoleResponse(user.getRole().getId(), user.getRole().getName()));
            return userResponse;
        }catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
