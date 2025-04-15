package vn.nextcore.device.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import vn.nextcore.device.dto.resp.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import vn.nextcore.device.enums.ErrorCodeEnum;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Map<String, String> errorMap = new HashMap<>();

    static {
        errorMap.put("ER001", "Email not blank");
        errorMap.put("ER002", "Password not blank");
//        errorMap.put("ER003", "Email has the suffix @nextcore.vn");
        errorMap.put("ER004", "The password length >= 8 and <= 50 characters.");
        errorMap.put("ER005", "Internal server error. Please try again later.");
        errorMap.put("ER006", "Email length <= 100 characters");
        errorMap.put("ER008", "Name not blank");
        errorMap.put("ER009", "Price buy not blank");
        errorMap.put("ER010", "Price buy must be number >= 0");
        errorMap.put("ER011", "Price sell must be number >= 0");
        errorMap.put("ER012", "Date buy not blank");
        errorMap.put("ER013", "Date buy format is dd/MM/yyyy");
        errorMap.put("ER014", "Date buy invalid date");
        errorMap.put("ER015", "Date sell format is dd/MM/yyyy");
        errorMap.put("ER016", "Date sell invalid date");
        errorMap.put("ER017", "dateMaintenance not blank");
        errorMap.put("ER018", "dateMaintenance format is dd/MM/yyyy");
        errorMap.put("ER019", "dateMaintenance invalid date");
        errorMap.put("ER020", "GroupId not blank");
        errorMap.put("ER021", "GroupId must be a positive integer");
        errorMap.put("ER022", "GroupId not exists");
        errorMap.put("ER023", "createBy not blank");
        errorMap.put("ER024", "createBy must be a positive integer");
        errorMap.put("ER025", "createBy not exists");
        errorMap.put("ER026", "providerId not blank");
        errorMap.put("ER027", "providerId must be a positive integer");
        errorMap.put("ER028", "providerId not exists");
        errorMap.put("ER029", "File size maximum 5MB");
        errorMap.put("ER030", "Invalid file type. Only JPG, PNG, JPEG, and GIF are allowed.");
        errorMap.put("ER031", "Upload maximum 5 file");
        errorMap.put("ER032", "Upload image failed!");
        errorMap.put("ER033", "specificationId must be a positive integer");
        errorMap.put("ER034", "specificationId not exists");
        errorMap.put("ER035", "dateMaintenance > dateBuy");
        errorMap.put("ER036", "Specification name not blank");
        errorMap.put("ER037", "Specification value not blank");
        errorMap.put("ER057", "deviceId not found");
        errorMap.put("ER058", "deviceId must be positive integer");
        errorMap.put("ER113", "repeatPassword not blank");
        errorMap.put("ER114", "password length must be > = 8 and <= 50 char");
        errorMap.put("ER115", "Name provider not blank");
        errorMap.put("ER116", "Phone provider not blank");
        errorMap.put("ER117", "Phone provider invalid");
        errorMap.put("ER118", "Address provider not blank");
        errorMap.put("ER119", "name provider max length 250");
        errorMap.put("ER120", "address provider max length 250");
        errorMap.put("ER121", "Title not blank");
        errorMap.put("ER122", "Description not blank");
        errorMap.put("ER123", "projectId not blank");
        errorMap.put("ER124", "requestDevices not blank");
        errorMap.put("ER125", "groupId invalid");
        errorMap.put("ER126", "quantity invalid");
        errorMap.put("ER127", "createdDate invalid");
        errorMap.put("ER128", "approvedDate invalid");
        errorMap.put("ER129", "status must in pending|approved|done");
        errorMap.put("ER130", "Offset must >= 0");
        errorMap.put("ER131", "Limit must >= 1");
        errorMap.put("ER132", "sortCreatedDate must in desc|asc");
        errorMap.put("ER133", "sortApprovedDate must in desc|asc");
        errorMap.put("ER134", "type not exists");
    }

    @ExceptionHandler(HandlerException.class)
    public ResponseEntity<ErrorResponse> handleHandlerException(HandlerException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ex.getCode());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setPath(ex.getPath());

        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();

        String path = request.getRequestURI();
        errorResponse.setPath(path);

        // handle exception field
        handleFieldValidationErrors(ex, errorResponse);

        // handle exception class
        handleClassValidationErrors(ex, errorResponse);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private void handleFieldValidationErrors(MethodArgumentNotValidException ex, ErrorResponse errorResponse) {
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String code = error.getDefaultMessage();
            String message = errorMap.get(code);

            errorResponse.setErrorCode(code);
            errorResponse.setMessage(message);
            break;
        }
    }

    private void handleClassValidationErrors(MethodArgumentNotValidException ex, ErrorResponse errorResponse) {
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            String code = error.getDefaultMessage();
            String message = errorMap.get(code);

            errorResponse.setErrorCode(code);
            errorResponse.setMessage(message);
            break;
        }
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String path = request.getRequestURI();

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorCodeEnum.ER104.getCode());
        errorResponse.setMessage(ErrorCodeEnum.ER104.getMessage());
        errorResponse.setPath(path);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(Exception ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        ex.printStackTrace();
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorCodeEnum.ER005.getCode());
        errorResponse.setMessage(ErrorCodeEnum.ER005.getMessage());
        errorResponse.setPath(path);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(ConstraintViolationException ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        String message = extractValue(ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorCodeEnum.ER104.getCode());
        errorResponse.setMessage(message);
        errorResponse.setPath(path);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    public String extractValue(String input) {
        if (input != null && input.contains(":")) {
            String[] parts = input.split(":");
            String message = "Field: " + parts[1].trim() + " not allowed!";
            return message;
        }
        return input;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(NoHandlerFoundException  ex, HttpServletRequest request) {
        String path = request.getRequestURI();

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorCodeEnum.ER404.getCode());
        errorResponse.setMessage(ErrorCodeEnum.ER404.getMessage());
        errorResponse.setPath(path);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        ex.printStackTrace();
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorCodeEnum.ER029.getCode());
        errorResponse.setMessage(ErrorCodeEnum.ER029.getMessage());
        errorResponse.setPath(path);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
