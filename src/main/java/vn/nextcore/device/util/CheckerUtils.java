package vn.nextcore.device.util;

import org.springframework.http.HttpStatus;
import vn.nextcore.device.dto.req.FilterRequest;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.validation.HandlerValidateParams;

import java.util.List;

public class CheckerUtils {
    public static boolean checkIsNotEmptyValues(List<String> values) {
        return values != null && values.stream()
                .allMatch(value -> value != null && !value.isEmpty());
    }

    public static void validateFilterField(FilterRequest filter, List<String> allowedFields) {
        if (!allowedFields.contains(filter.getField())) {
            throw new HandlerException(ErrorCodeEnum.ER051.getCode(), ErrorCodeEnum.ER051.getMessage(filter.getField()), HttpStatus.BAD_REQUEST);
        }
    }

    public static void validateDateFilter(FilterRequest filter) {
        // check date format and valid
        for (String value : filter.getValues()) {
            HandlerValidateParams.validateFormatDateField(value, ErrorCodeEnum.ER143);
            HandlerValidateParams.checkInvalidDateField(value, ErrorCodeEnum.ER144);
        }

        if (filter.getValues().size() == 2) {
            HandlerValidateParams.checkCompareDates(filter.getValues().get(0), filter.getValues().get(1),
                    ErrorCodeEnum.ER050);
        }
    }

}
