package vn.nextcore.device.validation.handler_anotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.http.HttpStatus;
import vn.nextcore.device.dto.req.DeviceRequest;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.validation.anotations.CheckDateMaintenanceLaterThanDateBuy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CompareDatesValidator implements ConstraintValidator<CheckDateMaintenanceLaterThanDateBuy, DeviceRequest> {
    private String message;

    private final String REGEX_FORMAT_DATE = "^\\d{2}/\\d{2}/\\d{4}$";

    @Override
    public void initialize(CheckDateMaintenanceLaterThanDateBuy constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(DeviceRequest request, ConstraintValidatorContext context) {
        try {
            String dateBuy = request.getDateBuy();
            String dateMaintenance = request.getDateMaintenance();

            if (dateBuy == null || dateMaintenance == null || dateBuy.isEmpty() || dateMaintenance.isEmpty()) {
                return true;
            }
            if (!dateBuy.matches(REGEX_FORMAT_DATE) || !dateMaintenance.matches(REGEX_FORMAT_DATE)) {
                return true;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate startDate = LocalDate.parse(dateBuy, formatter);
            LocalDate endDate = LocalDate.parse(dateMaintenance, formatter);
            if (startDate.isBefore(endDate)) {
                return true;
            }else {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                return false;
            }
        }catch (DateTimeParseException e){
            e.printStackTrace();
            return true;
        }
    }
}
