package vn.nextcore.device.validation.handler_anotations;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.http.HttpStatus;
import vn.nextcore.device.dto.req.DeviceRequest;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.validation.anotations.CheckFieldsCreateDeviceValid;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.lang.reflect.Field;

public class AllowedFieldsValidator implements ConstraintValidator<CheckFieldsCreateDeviceValid, HttpServletRequest> {
    private Set<String> allowedFields;

    @Override
    public void initialize(CheckFieldsCreateDeviceValid constraintAnnotation) {
        allowedFields = Set.of(DeviceRequest.class.getDeclaredFields())
                .stream()
                .map(Field::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(HttpServletRequest request, ConstraintValidatorContext context) {
        allowedFields.add("specifications");
        allowedFields.add("files");
        try {
            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                String partName = part.getName();
                if (!allowedFields.contains(partName)) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(partName)
                            .addConstraintViolation();
                    return false;
                }
            }
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return true;
    }
}
