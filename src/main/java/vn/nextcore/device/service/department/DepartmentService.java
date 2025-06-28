package vn.nextcore.device.service.department;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nextcore.device.dto.req.DepartmentRequest;
import vn.nextcore.device.dto.resp.DepartmentResponse;
import vn.nextcore.device.dto.resp.ProviderResponse;
import vn.nextcore.device.entity.Department;
import vn.nextcore.device.entity.Group;
import vn.nextcore.device.entity.Provider;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.DepartmentRepository;
import vn.nextcore.device.util.ParseUtils;
import vn.nextcore.device.validation.HandlerValidateParams;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DepartmentService implements IDepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public List<DepartmentResponse> listDepartment() {
        try {
            List<DepartmentResponse> result = new ArrayList<>();
            List<Department> departments = departmentRepository.findAll();
            for (Department depart : departments) {
                DepartmentResponse departmentResponse = ParseUtils.convertDepartmentToDepartmentResponse(depart);
                result.add(departmentResponse);
            }
            return result;
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.DEPARTMENT_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.DEPARTMENT_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public DepartmentResponse getDetailsDepartment(String id) {
        try {
            HandlerValidateParams.validateInt(id, ErrorCodeEnum.ER149);
            Department department = departmentRepository.findDepartmentById(Long.parseLong(id));
            if (department == null) {
                throw new HandlerException(ErrorCodeEnum.ER153.getCode(), ErrorCodeEnum.ER153.getMessage(), PathEnum.DEPARTMENT_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }

            return ParseUtils.convertDepartmentToDepartmentResponse(department);
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.DEPARTMENT_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.DEPARTMENT_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(String id, DepartmentRequest req) {
        try {
            HandlerValidateParams.validateInt(id, ErrorCodeEnum.ER149);
            Department department = departmentRepository.findDepartmentById(Long.parseLong(id));

            if (department == null) {
                throw new HandlerException(ErrorCodeEnum.ER153.getCode(), ErrorCodeEnum.ER153.getMessage(), PathEnum.DEPARTMENT_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            department.setName(req.getName());

            departmentRepository.save(department);

            return new DepartmentResponse(department.getId());
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.DEPARTMENT_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.DEPARTMENT_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void deleteDepartmentById(String id) {
        try {
            HandlerValidateParams.validateInt(id, ErrorCodeEnum.ER149);
            Department department = departmentRepository.findDepartmentById(Long.valueOf(id));
            if (department == null) {
                throw new HandlerException(ErrorCodeEnum.ER153.getCode(), ErrorCodeEnum.ER153.getMessage(), HttpStatus.BAD_REQUEST);
            }
            if (!department.getUsers().isEmpty()) {
                throw new HandlerException(ErrorCodeEnum.ER154.getCode(), ErrorCodeEnum.ER154.getMessage(), PathEnum.DEPARTMENT_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            departmentRepository.deleteById(Long.valueOf(id));
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.DEPARTMENT_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.DEPARTMENT_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
