package vn.nextcore.device.validation.anotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import vn.nextcore.device.validation.handler_anotations.CheckUserIdExistsValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CheckUserIdExistsValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckUserIdExists {
    String message() default "UserId not exists!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
