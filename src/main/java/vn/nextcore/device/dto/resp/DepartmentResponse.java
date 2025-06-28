package vn.nextcore.device.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DepartmentResponse {
    private Long id;

    private String name;

    private List<UserResponse> users = new ArrayList<>();

    public DepartmentResponse(Long id) {
        this.id = id;
    }

    public DepartmentResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
