package vn.nextcore.device.dto.record;

import lombok.Builder;

@Builder
public record MailBody(String to, String subject, String text) {
}
