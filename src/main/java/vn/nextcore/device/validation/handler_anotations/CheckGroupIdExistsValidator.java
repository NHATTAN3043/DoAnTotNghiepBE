package vn.nextcore.device.validation.handler_anotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import vn.nextcore.device.repository.GroupRepository;
import vn.nextcore.device.validation.anotations.CheckGroupIdExists;

public class CheckGroupIdExistsValidator implements ConstraintValidator<CheckGroupIdExists, String> {
    private String message;

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public void initialize(CheckGroupIdExists constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        if (s == null || !s.matches("^[1-9]\\d*$")) {
            return true;
        }
        if (!groupRepository.existsById(Long.valueOf(s))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        return true;
    }
}
