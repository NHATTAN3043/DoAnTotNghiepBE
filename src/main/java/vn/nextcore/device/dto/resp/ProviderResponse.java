package vn.nextcore.device.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Schema
public class ProviderResponse {
    private String id;

    private String name;

    private String address;

    private String phoneNumber;
}
