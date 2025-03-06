package vn.nextcore.device.validation.handler_anotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.nextcore.device.validation.anotations.CheckDateValid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class CheckDateValidValidator implements ConstraintValidator<CheckDateValid, String> {
    private String message;

    @Override
    public void initialize(CheckDateValid constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        if (date == null || date.isEmpty() || !date.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            return true;
        }

        boolean isDateValid = isValid(date);
        if (!isDateValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        return true;
    }

    public static boolean isValid(final String date) {
        try {
            LocalDate.parse(date,
                    DateTimeFormatter.ofPattern("dd/MM/uuuu")
                            .withResolverStyle(ResolverStyle.STRICT)
            );
            return true;
        } catch (DateTimeParseException e) {
            return  false;
        }
    }
}
