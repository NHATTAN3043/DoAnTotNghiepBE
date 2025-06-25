package vn.nextcore.device.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.nextcore.device.dto.req.ProjectRequest;
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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public DataResponse<ProjectResponse> createProject(@RequestBody ProjectRequest projectRequest) {
        ProjectResponse result = projectService.createProject(projectRequest);
        return new DataResponse<>(result);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<ProjectResponse> getDetailProject(@PathVariable("id") String id) {
        ProjectResponse result = projectService.getDetailsProject(id);
        return new DataResponse<>(result);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<ProjectResponse> updateProject(
            @PathVariable("id") String id,
            @RequestBody ProjectRequest req) {
        ProjectResponse result = projectService.updateProject(id, req);
        return new DataResponse<>(result);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProject(
            @Parameter(description = "Id of project is number", required = true, example = "1")
            @PathVariable(name = "id") String id) {
        projectService.deleteProjectById(id);
    }
}
