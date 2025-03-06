package vn.nextcore.device.validation.handler_anotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import vn.nextcore.device.repository.SpecificationRepository;
import vn.nextcore.device.validation.anotations.CheckSpecificationIdExists;

public class CheckSpecificationIdExistsValidator implements ConstraintValidator<CheckSpecificationIdExists, String> {
    private String message;

    @Autowired
    private SpecificationRepository specificationRepository;

    @Override
    public void initialize(CheckSpecificationIdExists constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        if (s == null || !s.matches("^[1-9]\\d*$")) {
            return true;
        }
        if (!specificationRepository.existsById(Long.valueOf(s))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        return true;
    }
}
