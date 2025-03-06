package vn.nextcore.device.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class DataResponse<T> {
    private T data;

    public DataResponse(T data) {
        this.data = data;
    }
}
