package vn.nextcore.device.service.user;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nextcore.device.dto.req.DepartmentRequest;
import vn.nextcore.device.dto.req.UserRequest;
import vn.nextcore.device.dto.resp.DepartmentResponse;
import vn.nextcore.device.dto.resp.RoleResponse;
import vn.nextcore.device.dto.resp.UserResponse;
import vn.nextcore.device.entity.Department;
import vn.nextcore.device.entity.Group;
import vn.nextcore.device.entity.Role;
import vn.nextcore.device.entity.User;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.DepartmentRepository;
import vn.nextcore.device.repository.RoleRepository;
import vn.nextcore.device.repository.UserRepository;
import vn.nextcore.device.security.jwt.JwtUtil;
import vn.nextcore.device.service.storageFiles.IStorageService;
import vn.nextcore.device.util.ParseUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements IUserService {
    @Autowired
    private JwtUtil jwtUtil;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Autowired
    private IStorageService storageService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
            if (user.getDateOfBirth() != null) {
                userResponse.setDateOfBirth(dateFormat.format(user.getDateOfBirth()));
            }
            userResponse.setRole(new RoleResponse(user.getRole().getId(), user.getRole().getName()));
            return userResponse;
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public UserResponse createUser(HttpServletRequest request, UserRequest userRequest) {
        User newUser = new User();
        try {
            User user = jwtUtil.extraUserFromRequest(request);
            if (user.getRole().getId() != 3) {
                throw new HandlerException(ErrorCodeEnum.ER403.getCode(), ErrorCodeEnum.ER403.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.FORBIDDEN);
            }
            newUser.setUserName(userRequest.getUserName());
            newUser.setEmail(userRequest.getEmail());
            newUser.setPhoneNumber(userRequest.getPhoneNumber());
            newUser.setGender(userRequest.getGender());
            newUser.setDateOfBirth(dateFormat.parse(userRequest.getDateOfBirth()));
            if (userRequest.getAddress() != null) newUser.setAddress(userRequest.getAddress());
            if (userRequest.getAvatar() != null) {
                String fileName = storageService.saveFile(userRequest.getAvatar());
                newUser.setAvatarUrl(fileName);
            }

            Role role = roleRepository.findRoleById(userRequest.getRoleId());
            if (role == null) {
                throw new HandlerException(ErrorCodeEnum.ER127.getCode(), ErrorCodeEnum.ER127.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            newUser.setRole(role);

            Department department = departmentRepository.findDepartmentById(userRequest.getDepartmentId());
            if (department == null) {
                throw new HandlerException(ErrorCodeEnum.ER138.getCode(), ErrorCodeEnum.ER138.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            newUser.setDepartment(department);

            newUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));


            userRepository.save(newUser);

            return new UserResponse(newUser.getId());
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), handlerException.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public UserResponse updateUser(String id, UserRequest userRequest) {
        try {
            User user = userRepository.findUserById(Long.parseLong(id));
            user.setUserName(userRequest.getUserName());
            user.setPhoneNumber(userRequest.getPhoneNumber());
            user.setGender(userRequest.getGender());
            user.setDateOfBirth(dateFormat.parse(userRequest.getDateOfBirth()));
            user.setAddress(userRequest.getAddress());
            user.setEmail(userRequest.getEmail());
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            Department department = departmentRepository.findDepartmentById(userRequest.getDepartmentId());
            if (department == null) {
                throw new HandlerException(ErrorCodeEnum.ER138.getCode(), ErrorCodeEnum.ER138.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            user.setDepartment(department);

            Role role = roleRepository.findRoleById(userRequest.getRoleId());
            if (role == null) {
                throw new HandlerException(ErrorCodeEnum.ER127.getCode(), ErrorCodeEnum.ER127.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            user.setRole(role);

            if (userRequest.getAvatar() != null) {
                String fileName = storageService.saveFile(userRequest.getAvatar());
                user.setAvatarUrl(fileName);
            }

            userRepository.save(user);
            return new UserResponse(user.getId());
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), handlerException.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<UserResponse> userResponseList = new ArrayList<>();
        try {
            List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            if (users != null && !users.isEmpty()) {
                for (User user : users) {
                    UserResponse userResponse = ParseUtils.convertUserToUserResponse(user);
                    userResponseList.add(userResponse);
                }
            }
            return userResponseList;
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<RoleResponse> getRoles() {
        List<RoleResponse> roleResponses = new ArrayList<>();
        try {
            List<Role> roles = roleRepository.findAll();
            if (roles != null && !roles.isEmpty()) {
                for (Role role : roles) {
                    roleResponses.add(new RoleResponse(role.getId(), role.getName()));
                }
            }

            return roleResponses;
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<DepartmentResponse> getDepartments() {
        List<DepartmentResponse> departmentResponses = new ArrayList<>();
        try {
            List<Department> departments = departmentRepository.findAll();
            if (departments != null && !departments.isEmpty()) {
                for (Department department : departments) {
                    departmentResponses.add(new DepartmentResponse(department.getId(), department.getName()));
                }
            }

            return departmentResponses;
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        Department newDepartment = new Department();
        try {
            newDepartment.setName(request.getName());
            departmentRepository.save(newDepartment);

            return new DepartmentResponse(newDepartment.getId());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
