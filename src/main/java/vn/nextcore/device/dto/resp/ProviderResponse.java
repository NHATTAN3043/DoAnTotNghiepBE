package vn.nextcore.device.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Schema
@AllArgsConstructor
@NoArgsConstructor
public class ProviderResponse {
    private Long id;

    private String name;

    private String address;

    private String phoneNumber;

    private List<DeviceResponse> devices = new ArrayList<>();

    public ProviderResponse(Long id) {
        this.id = id;
    }
}
