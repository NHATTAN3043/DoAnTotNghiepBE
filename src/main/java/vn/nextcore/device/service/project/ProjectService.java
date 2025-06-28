package vn.nextcore.device.service.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nextcore.device.dto.req.DepartmentRequest;
import vn.nextcore.device.dto.req.ProjectRequest;
import vn.nextcore.device.dto.resp.*;
import vn.nextcore.device.entity.*;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.ProjectRepository;
import vn.nextcore.device.util.ParseUtils;
import vn.nextcore.device.validation.HandlerValidateParams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService implements IProjectService{
    @Autowired
    private ProjectRepository projectRepository;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public List<ProjectResponse> getAllProjects() {
        List<ProjectResponse> results = new ArrayList<>();
        try {
            List<Project> projects = projectRepository.findAll();
            for (Project project : projects) {
                ProjectResponse item = new ProjectResponse();
                item.setId(String.valueOf(project.getId()));
                item.setName(project.getProjectName());
                List<UserProject> userProjects = project.getUserProjects();
                List<UserResponse> userResponseList = new ArrayList<>();
                if (userProjects != null) {
                    for (UserProject up : userProjects) {
                        if (up.getUser() != null) {
                            UserResponse userResponse = new UserResponse();
                            userResponse.setId(up.getUser().getId());
                            userResponse.setAvatarUrl(up.getUser().getAvatarUrl());
                            userResponse.setUserName(up.getUser().getUserName());
                            userResponse.setRole(new RoleResponse(up.getUser().getRole().getId(), up.getUser().getRole().getName()));
                            userResponse.setEmail(up.getUser().getEmail());
                            userResponse.setPhoneNumber(up.getUser().getPhoneNumber());
                            userResponse.setGender(up.getUser().getGender());
                            if (up.getUser().getDateOfBirth() != null) {
                                userResponse.setDateOfBirth(dateFormat.format(up.getUser().getDateOfBirth()));
                            }
                            userResponseList.add(userResponse);
                        }
                    }
                }
                item.setUsers(userResponseList);
                item.setQuantity(userProjects.size());
                results.add(item);
            }
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.PROJECT_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ProjectResponse createProject(ProjectRequest req) {
        try {
            Project newProject = new Project();
            newProject.setProjectName(req.getName());
            projectRepository.save(newProject);
            ProjectResponse result = new ProjectResponse();
            result.setId(newProject.getId().toString());
            return result;
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.PROJECT_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ProjectResponse getDetailsProject(String id) {
        try {
            HandlerValidateParams.validateInt(id, ErrorCodeEnum.ER149);
            Project project = projectRepository.findProjectById(Long.parseLong(id));
            if (project == null) {
                throw new HandlerException(ErrorCodeEnum.ER156.getCode(), ErrorCodeEnum.ER156.getMessage(), PathEnum.PROJECT_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }

            ProjectResponse projectResponse = new ProjectResponse();
            projectResponse.setId(project.getId().toString());
            projectResponse.setName(project.getProjectName());

            return projectResponse;
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.PROJECT_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.PROJECT_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ProjectResponse updateProject(String id, ProjectRequest req) {
        try {
            HandlerValidateParams.validateInt(id, ErrorCodeEnum.ER149);
            Project project = projectRepository.findProjectById(Long.parseLong(id));

            if (project == null) {
                throw new HandlerException(ErrorCodeEnum.ER156.getCode(), ErrorCodeEnum.ER156.getMessage(), PathEnum.PROJECT_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            project.setProjectName(req.getName());

            projectRepository.save(project);

            ProjectResponse projectResponse = new ProjectResponse();
            projectResponse.setId(project.getId().toString());
            return projectResponse;
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.PROJECT_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.PROJECT_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteProjectById(String id) {
        try {
            HandlerValidateParams.validateInt(id, ErrorCodeEnum.ER149);
            Project project = projectRepository.findProjectById(Long.parseLong(id));
            if (project == null) {
                throw new HandlerException(ErrorCodeEnum.ER156.getCode(), ErrorCodeEnum.ER156.getMessage(), PathEnum.PROJECT_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            if (project.getProjectName() == null) {
                throw new HandlerException(ErrorCodeEnum.ER157.getCode(), ErrorCodeEnum.ER157.getMessage(), PathEnum.PROJECT_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            projectRepository.deleteById(Long.valueOf(id));
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.PROJECT_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.PROJECT_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
