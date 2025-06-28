package vn.nextcore.device.service.notification;

import com.google.firebase.messaging.BatchResponse;
import jakarta.servlet.http.HttpServletRequest;
import vn.nextcore.device.dto.req.FcmToken;
import vn.nextcore.device.dto.resp.NotificationResponse;

import java.util.List;

public interface INotificationService {
    String sendNotification(List<String> tokens, String title, String content, String noticeType, String status, String url);

    void saveFcmToken(FcmToken req);

    List<NotificationResponse> getNotificationByUser(HttpServletRequest request);

    void updateNotification(String id);
}
