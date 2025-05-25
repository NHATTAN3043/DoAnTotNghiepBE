package vn.nextcore.device.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Schema
public class QuantityStatisticsResponse {
    private Integer totalDevice;

    private Integer totalUsed;
    private Integer totalStock;
    private Integer totalMaintenance;

    List<GroupResponse> groupStatistics = new ArrayList<>();
}
