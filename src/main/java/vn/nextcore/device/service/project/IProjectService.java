package vn.nextcore.device.service.project;

import vn.nextcore.device.dto.resp.ProjectResponse;

import java.util.List;

public interface IProjectService {
    List<ProjectResponse> getAllProjects();
}
