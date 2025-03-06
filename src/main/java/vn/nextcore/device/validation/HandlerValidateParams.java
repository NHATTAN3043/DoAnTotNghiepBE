package vn.nextcore.device.validation;

import org.springframework.http.HttpStatus;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.exception.HandlerException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class HandlerValidateParams {
    private static final String REGEX_ORD = "^(asc|desc)$";
    private static final String REGEX_INT = "^[0-9]\\d*$";
    private static final String REGEX_POSITIVE_INT = "^[1-9]\\d*$";
    private static final String REGEX_STATUS = "^(stock|active|maintenance)$";
    private static final String REGEX_DATE_FORMAT = "^\\d{2}/\\d{2}/\\d{4}$";
    private static final String REGEX_OTP = "^\\d{6}$";
    private static final String REGEX_EMAIL = "^[A-Za-z0-9._%+-]+@nextcore\\.vn$";

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void validateEmailFormat(String value, ErrorCodeEnum errorCodeEnum) {
        if (value != null && !value.isEmpty()) {
            if (!value.toLowerCase().matches(REGEX_EMAIL)) {
                throw new HandlerException(errorCodeEnum.getCode(), errorCodeEnum.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    public static void validateOtpFormat(String value, ErrorCodeEnum errorCodeEnum) {
        if (value != null && !value.isEmpty()) {
            if (!value.trim().matches(REGEX_OTP)) {
                throw new HandlerException(errorCodeEnum.getCode(), errorCodeEnum.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    public static void validateSortField(String value, ErrorCodeEnum errorCodeEnum) {
        if (value != null && !value.isEmpty()) {
            if (!value.toLowerCase().matches(REGEX_ORD)) {
                throw new HandlerException(errorCodeEnum.getCode(), errorCodeEnum.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    public static void validateInt(String value, ErrorCodeEnum errorCodeEnum) {
        if (value != null && !value.isEmpty()) {
            if (!value.matches(REGEX_INT)) {
                throw new HandlerException(errorCodeEnum.getCode(), errorCodeEnum.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    public static void validatePositiveInt(String value, ErrorCodeEnum errorCodeEnum) {
        if (value != null && !value.isEmpty()) {
            if (!value.matches(REGEX_POSITIVE_INT)) {
                throw new HandlerException(errorCodeEnum.getCode(), errorCodeEnum.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    public static void validateStatusField(String value, ErrorCodeEnum errorCodeEnum) {
        if (value != null && !value.isEmpty()) {
            if (!value.toLowerCase().matches(REGEX_STATUS)) {
                throw new HandlerException(errorCodeEnum.getCode(), errorCodeEnum.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    public static void validateFormatDateField(String value, ErrorCodeEnum errorCodeEnum) {
        if (value != null && !value.isEmpty()) {
            if (!value.matches(REGEX_DATE_FORMAT)) {
                throw new HandlerException(errorCodeEnum.getCode(), errorCodeEnum.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    public static void checkInvalidDateField(String value, ErrorCodeEnum errorCodeEnum) {
        try {
            if (value != null && !value.isEmpty()) {
                LocalDate.parse(value,
                        DateTimeFormatter.ofPattern("dd/MM/uuuu")
                                .withResolverStyle(ResolverStyle.STRICT)
                );
            }
        } catch (DateTimeParseException e) {
            throw new HandlerException(errorCodeEnum.getCode(), errorCodeEnum.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public static void checkCompareDates(String valueStartDate, String valueEndDate, ErrorCodeEnum errorCodeEnum) {
        if (!valueStartDate.isEmpty() && !valueEndDate.isEmpty()) {
            LocalDate startDate = LocalDate.parse(valueStartDate, formatter);
            LocalDate endDate = LocalDate.parse(valueEndDate, formatter);
            if (startDate.isAfter(endDate)) {
                throw new HandlerException(errorCodeEnum.getCode(), errorCodeEnum.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }
}
