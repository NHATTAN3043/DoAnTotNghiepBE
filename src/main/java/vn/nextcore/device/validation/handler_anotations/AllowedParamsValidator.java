package vn.nextcore.device.validation.handler_anotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.nextcore.device.validation.anotations.AllowedParams;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AllowedParamsValidator implements ConstraintValidator<AllowedParams, Map<String, String>> {
    private List<String> allowedParams;

    @Override
    public void initialize(AllowedParams constraintAnnotation) {
        allowedParams = List.of(constraintAnnotation.allowed());
    }

    @Override
    public boolean isValid(Map<String, String> params, ConstraintValidatorContext context) {
        if (params == null) return true;

        List<String> invalidParams = params.keySet().stream()
                .filter(param -> !allowedParams.contains(param))
                .collect(Collectors.toList());

        if (!invalidParams.isEmpty()) {
            String invalidParamsJson = convertListToJson(invalidParams);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(invalidParamsJson)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private String convertListToJson(List<String> invalidParams) {
        return "[" + invalidParams.stream()
                .collect(Collectors.joining(", ")) + "]";
    }
}
