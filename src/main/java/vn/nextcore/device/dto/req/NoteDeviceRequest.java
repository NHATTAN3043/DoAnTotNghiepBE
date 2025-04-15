package vn.nextcore.device.dto.req;

import lombok.Data;

@Data
public class NoteDeviceRequest {
    private String descriptionDevice;

    private Double priceDevice;

    private Long deviceId;
}
