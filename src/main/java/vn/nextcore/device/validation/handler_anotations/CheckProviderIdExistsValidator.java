package vn.nextcore.device.validation.handler_anotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import vn.nextcore.device.repository.ProviderRepository;
import vn.nextcore.device.validation.anotations.CheckProviderIdExists;

public class CheckProviderIdExistsValidator implements ConstraintValidator<CheckProviderIdExists, String> {
    private String message;

    @Autowired
    private ProviderRepository providerRepository;

    @Override
    public void initialize(CheckProviderIdExists constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        if (s == null || !s.matches("^[1-9]\\d*$")) {
            return true;
        }
        if (!providerRepository.existsById(Long.valueOf(s))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        return true;
    }
}
