package vn.nextcore.device.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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
}
