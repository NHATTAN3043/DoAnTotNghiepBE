package vn.nextcore.device.dto.req;

import lombok.Data;

import java.util.List;

@Data
public class DeliveryNoteRequest {
    private Long id;

    private String typeNote;

    private String description;

    private Long requestId;

    private Long providerId;

    private String appointmentDate;

    private Boolean isConfirm;

    private List<NoteDeviceRequest> noteDeviceRequests;

}
