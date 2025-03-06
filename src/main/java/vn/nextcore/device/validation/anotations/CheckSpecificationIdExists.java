package vn.nextcore.device.validation.anotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import vn.nextcore.device.validation.handler_anotations.CheckSpecificationIdExistsValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CheckSpecificationIdExistsValidator.class)
@Target({ ElementType.TYPE_USE, ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckSpecificationIdExists {
    String message() default "SpecificationId not exists!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
