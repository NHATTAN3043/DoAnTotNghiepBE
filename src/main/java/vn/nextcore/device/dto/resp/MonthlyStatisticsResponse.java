package vn.nextcore.device.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Schema
public class MonthlyStatisticsResponse {
    private Integer month;

    private Integer allocateQuantity;

    private Integer retrieveQuantity;

    private Integer maintenanceQuantity;

}
