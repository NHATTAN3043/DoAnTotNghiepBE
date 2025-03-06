package vn.nextcore.device.service.specification;

import vn.nextcore.device.dto.req.SpecificationRequest;
import vn.nextcore.device.dto.resp.DataResponse;
import vn.nextcore.device.dto.resp.SpecificationResponse;

import java.util.List;

public interface ISpecificationService {
    List<SpecificationResponse> getAllSpecification();
}
