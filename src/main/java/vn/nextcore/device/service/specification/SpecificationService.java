package vn.nextcore.device.service.specification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.nextcore.device.dto.resp.SpecificationResponse;
import vn.nextcore.device.entity.Specification;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.SpecificationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpecificationService implements ISpecificationService {
    @Autowired
    private SpecificationRepository specificationRepository;

    @Override
    public List<SpecificationResponse> getAllSpecification() {
        List<SpecificationResponse> result = new ArrayList<>();
        try {
            List<Specification> specifications = specificationRepository.findAll();
            for (Specification specification : specifications) {
                SpecificationResponse specificationResponse = new SpecificationResponse();
                specificationResponse.setName(specification.getName());
                specificationResponse.setValue(specification.getValue());
                result.add(specificationResponse);
            }
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.SPECIFICATION_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (result.isEmpty())
            throw new HandlerException(ErrorCodeEnum.ER036.getCode(), ErrorCodeEnum.ER036.getMessage(), PathEnum.SPECIFICATION_PATH.getPath(), HttpStatus.NOT_FOUND);

        return result;
    }
}
