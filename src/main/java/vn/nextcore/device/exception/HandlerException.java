package vn.nextcore.device.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class HandlerException extends RuntimeException{
    private String code;
    private String message;
    private String path;
    private HttpStatus status;
    public HandlerException() {}

    public HandlerException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public HandlerException(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public HandlerException(String code, String message, String path, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.path = path;
        this.status = status;
    }
}
