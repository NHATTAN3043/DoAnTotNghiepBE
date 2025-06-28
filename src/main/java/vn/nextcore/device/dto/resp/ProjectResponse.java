package vn.nextcore.device.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Schema
public class ProjectResponse {
    private String id;

    private String name;

    private String dateJoin;

    private Integer quantity;

    private List<UserResponse> users = new ArrayList<>();
}
