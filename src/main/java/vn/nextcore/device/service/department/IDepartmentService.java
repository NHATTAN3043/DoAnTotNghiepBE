package vn.nextcore.device.service.department;

import vn.nextcore.device.dto.req.DepartmentRequest;
import vn.nextcore.device.dto.resp.DepartmentResponse;

import java.util.List;

public interface IDepartmentService {
    List<DepartmentResponse> listDepartment();

    DepartmentResponse getDetailsDepartment(String id);

    DepartmentResponse updateDepartment(String id, DepartmentRequest req);

    void deleteDepartmentById(String id);
}
