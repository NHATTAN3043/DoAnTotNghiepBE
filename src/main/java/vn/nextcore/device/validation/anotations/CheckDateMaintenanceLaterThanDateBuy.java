package vn.nextcore.device.validation.anotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import vn.nextcore.device.validation.handler_anotations.CompareDatesValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CompareDatesValidator.class)
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckDateMaintenanceLaterThanDateBuy {
    String message() default "dateMaintenance > dateBuy";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
