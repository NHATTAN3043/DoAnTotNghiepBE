package vn.nextcore.device.service.user;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nextcore.device.dto.req.ChangePasswordRequest;
import vn.nextcore.device.dto.req.DepartmentRequest;
import vn.nextcore.device.dto.req.UserRequest;
import vn.nextcore.device.dto.resp.DepartmentResponse;
import vn.nextcore.device.dto.resp.RoleResponse;
import vn.nextcore.device.dto.resp.UserResponse;
import vn.nextcore.device.entity.*;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.*;
import vn.nextcore.device.security.jwt.JwtUtil;
import vn.nextcore.device.service.storageFiles.IStorageService;
import vn.nextcore.device.util.ParseUtils;
import vn.nextcore.device.validation.HandlerValidateParams;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserProjectRepository userProjectRepository;

    @Override
    public UserResponse getMe(HttpServletRequest request) {
        try {
            User user = jwtUtil.extraUserFromRequest(request);
            UserResponse userResponse = ParseUtils.convertUserToUserResponse(user);
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
            if (userRequest.getPassword() == null) {
                throw new HandlerException(ErrorCodeEnum.ER002.getCode(), ErrorCodeEnum.ER002.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
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

            if (userRequest.getProjectIds() != null) {
                for (String id : userRequest.getProjectIds()) {
                    HandlerValidateParams.validatePositiveInt(id, ErrorCodeEnum.ER155);
                    Project project = projectRepository.findProjectById(Long.parseLong(id));
                    if (project == null) {
                        throw new HandlerException(ErrorCodeEnum.ER156.getCode(), ErrorCodeEnum.ER156.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.BAD_REQUEST);
                    }
                    UserProject userProject = new UserProject();
                    userProject.setUser(newUser);
                    userProject.setDateOfJoin(new Date());
                    userProject.setProject(project);
                    userProjectRepository.save(userProject);
                }
            }
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
            HandlerValidateParams.validatePositiveInt(id, ErrorCodeEnum.ER140);

            User user = userRepository.findUserByIdAndDeletedAtIsNull(Long.parseLong(id));
            user.setUserName(userRequest.getUserName());
            user.setPhoneNumber(userRequest.getPhoneNumber());
            user.setGender(userRequest.getGender());
            user.setDateOfBirth(dateFormat.parse(userRequest.getDateOfBirth()));
            user.setAddress(userRequest.getAddress());
            user.setEmail(userRequest.getEmail());
            if (userRequest.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            }
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

            if (userRequest.getProjectIds() != null) {
                Set<Long> newProjectIds = userRequest.getProjectIds().stream()
                        .map(idStr -> {
                            HandlerValidateParams.validatePositiveInt(idStr, ErrorCodeEnum.ER155);
                            return Long.parseLong(idStr);
                        })
                        .collect(Collectors.toSet());

                // Lấy projectId hiện tại của user
                Set<Long> currentProjectIds = user.getUserProjects().stream()
                        .map(up -> up.getProject().getId())
                        .collect(Collectors.toSet());

                // Những projectId cần thêm mới
                Set<Long> toAdd = new HashSet<>(newProjectIds);
                toAdd.removeAll(currentProjectIds);

                // Những projectId cần xóa
                Set<Long> toRemove = new HashSet<>(currentProjectIds);
                toRemove.removeAll(newProjectIds);

                // Thêm mới
                for (Long projectId : toAdd) {
                    Project project = projectRepository.findProjectById(projectId);
                    if (project == null) {
                        throw new HandlerException(
                                ErrorCodeEnum.ER156.getCode(),
                                ErrorCodeEnum.ER156.getMessage(),
                                PathEnum.USER_PATH.getPath(),
                                HttpStatus.BAD_REQUEST
                        );
                    }
                    UserProject userProject = new UserProject();
                    userProject.setUser(user);
                    userProject.setProject(project);
                    userProject.setDateOfJoin(new Date());
                    userProjectRepository.save(userProject);
                }

                // Xóa các UserProject không còn trong list mới
                List<UserProject> userProjectsToDelete = user.getUserProjects().stream()
                        .filter(up -> toRemove.contains(up.getProject().getId()))
                        .collect(Collectors.toList());

                for (UserProject up : userProjectsToDelete) {
                    user.getUserProjects().remove(up);
                }
            }

            return new UserResponse(user.getId());
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), handlerException.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public UserResponse getUser(String id) {
        try {
            HandlerValidateParams.validatePositiveInt(id, ErrorCodeEnum.ER140);
            User user = userRepository.findUserByIdAndDeletedAtIsNull(Long.parseLong(id));
            if (user == null) {
                throw new HandlerException(ErrorCodeEnum.ER138.getCode(), ErrorCodeEnum.ER138.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            UserResponse userResponse = ParseUtils.convertUserToUserResponse(user);
            return userResponse;
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), handlerException.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public UserResponse deleteUser(String id) {
        try {
            HandlerValidateParams.validatePositiveInt(id, ErrorCodeEnum.ER140);
            User user = userRepository.findUserByIdAndDeletedAtIsNull(Long.parseLong(id));
            if (user == null) {
                throw new HandlerException(ErrorCodeEnum.ER138.getCode(), ErrorCodeEnum.ER138.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            if (user.getDevicesUsing() != null || !user.getDevicesUsing().isEmpty()) {
                throw new HandlerException(ErrorCodeEnum.ER139.getCode(), ErrorCodeEnum.ER139.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            user.setDeletedAt(new Date());
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
            List<User> users = userRepository.findAllByDeletedAtIsNull(Sort.by(Sort.Direction.DESC, "id"));
            if (users != null && !users.isEmpty()) {
                for (User user : users) {
                    UserResponse userResponse = ParseUtils.convertUserToUserResponse(user);
                    userResponseList.add(userResponse);
                }
            }
            return userResponseList;
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), handlerException.getPath(), handlerException.getStatus());
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
    @Transactional
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

    @Override
    @Transactional
    public void changePassword(HttpServletRequest request, ChangePasswordRequest req) {
        try {
            User user = jwtUtil.extraUserFromRequest(request);
            boolean isMatch = passwordEncoder.matches(req.getPassword(), user.getPassword());
            if (!isMatch) {
                throw new HandlerException(ErrorCodeEnum.ER152.getCode(), ErrorCodeEnum.ER152.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }

            user.setPassword(passwordEncoder.encode(req.getRepeatPassword()));
            userRepository.save(user);
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), handlerException.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.USER_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
