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
public class NotificationResponse {
    private Long id;

    private String title;

    private String content;

    private String createdAt;

    private UserResponse createdBy;

    private Boolean read;

    private String path;

}
