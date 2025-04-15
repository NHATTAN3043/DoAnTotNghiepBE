package vn.nextcore.device.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema
public class ReqGroupResponse {
    private Long reqGroupId;

    private Long groupId;

    private String groupName;

    private Integer reqQuantity;

    private Integer resQuantity;

    private Integer remainingQuantity;

    private String status;
}
