package vn.nextcore.device.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.nextcore.device.dto.req.DepartmentRequest;
import vn.nextcore.device.dto.req.ProviderRequest;
import vn.nextcore.device.dto.resp.DataResponse;
import vn.nextcore.device.dto.resp.DepartmentResponse;
import vn.nextcore.device.dto.resp.ProviderResponse;
import vn.nextcore.device.service.department.IDepartmentService;

import java.util.List;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {
    @Autowired
    private IDepartmentService departmentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<List<DepartmentResponse>> getAllDepartments() {
        List<DepartmentResponse> result = departmentService.listDepartment();
        return new DataResponse<>(result);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<DepartmentResponse> getDetailDepartment(@PathVariable("id") String id) {
        DepartmentResponse result = departmentService.getDetailsDepartment(id);
        return new DataResponse<>(result);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<DepartmentResponse> updateDepartment(
            @PathVariable("id") String departmentId,
            @RequestBody DepartmentRequest req) {
        DepartmentResponse result = departmentService.updateDepartment(departmentId, req);
        return new DataResponse<>(result);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDepartment(
            @Parameter(description = "Id of department is number", required = true, example = "1")
            @PathVariable(name = "id") String groupId) {
        departmentService.deleteDepartmentById(groupId);
    }

}
