package vn.nextcore.device.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Schema
public class GroupResponse {
    @Schema
    private Long id;

    @Schema
    private String name;

    @Schema
    private Integer quantity;

    private Integer usedQuantity;

    private Integer stockQuantity;

    private Integer maintenanceQuantity;

    private Integer scrapedQuantity;

    private List<DeviceResponse> devices = new ArrayList<>();
}
