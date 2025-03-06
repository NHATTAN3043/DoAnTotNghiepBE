package vn.nextcore.device.validation.anotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import vn.nextcore.device.validation.handler_anotations.CheckProviderIdExistsValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CheckProviderIdExistsValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckProviderIdExists {
    String message() default "providerId not exists!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
