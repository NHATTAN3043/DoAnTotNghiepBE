package vn.nextcore.device.service.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.nextcore.device.dto.resp.ProjectResponse;
import vn.nextcore.device.dto.resp.ProviderResponse;
import vn.nextcore.device.entity.Project;
import vn.nextcore.device.entity.Provider;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.ProjectRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService implements IProjectService{
    @Autowired
    private ProjectRepository projectRepository;
    @Override
    public List<ProjectResponse> getAllProjects() {
        List<ProjectResponse> results = new ArrayList<>();
        try {
            List<Project> projects = projectRepository.findAll();
            for (Project project : projects) {
                ProjectResponse item = new ProjectResponse();
                item.setId(String.valueOf(project.getId()));
                item.setName(project.getProjectName());

                results.add(item);
            }
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.GROUP_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
