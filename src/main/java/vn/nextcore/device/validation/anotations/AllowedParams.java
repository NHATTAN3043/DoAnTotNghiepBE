package vn.nextcore.device.validation.anotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import vn.nextcore.device.validation.handler_anotations.AllowedParamsValidator;

@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AllowedParamsValidator.class)
public @interface AllowedParams {
    String message() default "Invalid parameter(s)";

    String[] allowed() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
