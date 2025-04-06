package vn.nextcore.device.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class DataRequest {
    @NotBlank(message = "ER121")
    private String title;

    @NotBlank(message = "ER122")
    private String descriptions;

    private Long projectId;

    private String requestType;

    @NotBlank(message = "ER124")
    private List<ReqTypesRequest> requestGroups;
}
