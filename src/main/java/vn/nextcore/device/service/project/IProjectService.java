package vn.nextcore.device.service.project;

import vn.nextcore.device.dto.req.DepartmentRequest;
import vn.nextcore.device.dto.req.ProjectRequest;
import vn.nextcore.device.dto.resp.ProjectResponse;

import java.util.List;

public interface IProjectService {
    List<ProjectResponse> getAllProjects();

    ProjectResponse createProject(ProjectRequest req);

    ProjectResponse getDetailsProject(String id);

    ProjectResponse updateProject(String id, ProjectRequest req);

    void deleteProjectById(String id);
}
