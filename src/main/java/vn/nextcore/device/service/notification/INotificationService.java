package vn.nextcore.device.service.notification;

import com.google.firebase.messaging.BatchResponse;
import vn.nextcore.device.dto.req.FcmToken;

import java.util.List;

public interface INotificationService {
    String sendNotification(List<String> tokens, String title, String content, String noticeType, String status);

    void saveFcmToken(FcmToken req);
}
