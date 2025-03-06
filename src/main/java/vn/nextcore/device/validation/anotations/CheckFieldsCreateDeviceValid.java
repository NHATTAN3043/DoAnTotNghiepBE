package vn.nextcore.device.validation.anotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import vn.nextcore.device.validation.handler_anotations.AllowedFieldsValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AllowedFieldsValidator.class)
public @interface CheckFieldsCreateDeviceValid {
    String message() default "Request contain field invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
