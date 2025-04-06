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
public class ReqResponse {
    private Long requestId;

    private String title;

    private String descriptions;

    private String createdDate;

    private String status;

    private String approvedDate;

    private String type;

    private UserResponse createdBy;

    public ReqResponse(Long requestId) {
        this.requestId = requestId;
    }
}
