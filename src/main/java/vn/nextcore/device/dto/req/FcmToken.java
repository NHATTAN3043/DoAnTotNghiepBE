package vn.nextcore.device.dto.req;

import lombok.Data;

@Data
public class FcmToken {
    private String token;

    private String platform;

    private String browser;

    private Long userId;
}
