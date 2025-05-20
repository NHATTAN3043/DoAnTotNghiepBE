package vn.nextcore.device.service.user;

import jakarta.servlet.http.HttpServletRequest;
import vn.nextcore.device.dto.req.DepartmentRequest;
import vn.nextcore.device.dto.req.UserRequest;
import vn.nextcore.device.dto.resp.DepartmentResponse;
import vn.nextcore.device.dto.resp.RoleResponse;
import vn.nextcore.device.dto.resp.UserResponse;

import java.util.List;

public interface IUserService {
    UserResponse getMe(HttpServletRequest request);

    UserResponse createUser(HttpServletRequest request, UserRequest userRequest);

    UserResponse updateUser(String id, UserRequest userRequest);

    List<UserResponse> getAllUsers();

    List<RoleResponse> getRoles();

    List<DepartmentResponse> getDepartments();

    DepartmentResponse createDepartment(DepartmentRequest request);
}
