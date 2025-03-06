package vn.nextcore.device.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Schema(description = "Json error response")
public class ErrorResponse {
    @Schema(example = "ER000")
    private String errorCode;

    @Schema(example = "message failed something!")
    private String message;

    @Schema
    private String path;

    public ErrorResponse() {
    }

    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorResponse(String errorCode, String message, String path) {
        this.errorCode = errorCode;
        this.message = message;
        this.path = path;
    }
}
