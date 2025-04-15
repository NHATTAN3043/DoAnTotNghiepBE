package vn.nextcore.device.dto.req;

import lombok.Data;

import java.util.List;

@Data
public class DeliveryNoteRequest {
    private String typeNote;

    private String description;

    private Long requestId;

    private Long providerId;

    private List<NoteDeviceRequest> noteDeviceRequests;

}
