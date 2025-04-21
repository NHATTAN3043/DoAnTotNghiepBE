package vn.nextcore.device.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema
public class DeliveryNoteResponse {
    private Long deliveryNoteId;

    private String typeAction;

    private String description;

    private String timeCreated;

    private Boolean isConfirm;

    private Boolean isPartial;

    private UserResponse createdBy;

    private ProviderResponse provider;

    private List<NoteDeviceResponse> noteDeviceResponses;

    public DeliveryNoteResponse(Long deliveryNoteId) {
        this.deliveryNoteId = deliveryNoteId;
    }
}
