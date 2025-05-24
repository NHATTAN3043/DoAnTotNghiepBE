package vn.nextcore.device.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import vn.nextcore.device.dto.req.DepartmentRequest;
import vn.nextcore.device.dto.req.UserRequest;
import vn.nextcore.device.dto.resp.*;
import vn.nextcore.device.service.user.IUserService;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @GetMapping("/getMe")
    public DataResponse<UserResponse> getMe(HttpServletRequest request) {
        UserResponse result = userService.getMe(request);
        return new DataResponse<>(result);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<UserResponse> createUser(
            HttpServletRequest request,
            @Valid @ModelAttribute UserRequest userRequest) {
        UserResponse result = userService.createUser(request, userRequest);
        return new DataResponse<>(result);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<UserResponse> updateUser(
            @PathVariable("id") String userId,
            @Valid @ModelAttribute UserRequest userRequest) {
        UserResponse result = userService.updateUser(userId, userRequest);
        return new DataResponse<>(result);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}")
    public DataResponse<UserResponse> getUser(
            @PathVariable("id") String userId) {
        UserResponse result = userService.getUser(userId);
        return new DataResponse<>(result);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}")
    public DataResponse<UserResponse> disableUser(
            @PathVariable("id") String userId) {
        UserResponse result = userService.deleteUser(userId);
        return new DataResponse<>(result);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<List<UserResponse>> getAllUsers() {
        List<UserResponse> result = userService.getAllUsers();
        return new DataResponse<>(result);
    }

    @GetMapping("/getRole")
    public DataResponse<List<RoleResponse>> getRoles() {
        List<RoleResponse> result = userService.getRoles();
        return new DataResponse<>(result);
    }

    @GetMapping("/getDepartment")
    public DataResponse<List<DepartmentResponse>> getDepartments() {
        List<DepartmentResponse> result = userService.getDepartments();
        return new DataResponse<>(result);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/createDepartment")
    public DataResponse<DepartmentResponse> createDepartment(@RequestBody DepartmentRequest departmentRequest) {
        DepartmentResponse result = userService.createDepartment(departmentRequest);
        return new DataResponse<>(result);
    }

}
