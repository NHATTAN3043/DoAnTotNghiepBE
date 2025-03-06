package vn.nextcore.device.validation.handler_anotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import vn.nextcore.device.repository.UserRepository;
import vn.nextcore.device.validation.anotations.CheckUserIdExists;

public class CheckUserIdExistsValidator implements ConstraintValidator<CheckUserIdExists, String> {
    private String message;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(CheckUserIdExists constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        if (s == null || !s.matches("^[1-9]\\d*$")) {
            return true;
        }
        if (!userRepository.existsById(Long.valueOf(s))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        return true;
    }
}
