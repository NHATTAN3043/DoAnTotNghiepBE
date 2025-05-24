package vn.nextcore.device.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vn.nextcore.device.dto.resp.DataResponse;
import vn.nextcore.device.dto.resp.ProjectResponse;
import vn.nextcore.device.service.project.IProjectService;

import java.util.List;

@RestController
@RequestMapping("/api/project")
public class ProjectController {
    @Autowired
    private IProjectService projectService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> result = projectService.getAllProjects();
        return new DataResponse<>(result);
    }
}
