package vn.nextcore.device.service.notification;

import com.google.firebase.messaging.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.nextcore.device.dto.req.FcmToken;
import vn.nextcore.device.dto.resp.NotificationResponse;
import vn.nextcore.device.entity.DeviceTokens;
import vn.nextcore.device.entity.Notifications;
import vn.nextcore.device.entity.User;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.DeviceTokensRepository;
import vn.nextcore.device.repository.NotificationRepository;
import vn.nextcore.device.repository.UserRepository;
import vn.nextcore.device.security.jwt.JwtUtil;
import vn.nextcore.device.util.ParseUtils;
import vn.nextcore.device.validation.HandlerValidateParams;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements INotificationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceTokensRepository deviceTokensRepository;

    @Autowired
    FirebaseMessaging firebaseMessaging;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private NotificationRepository notificationRepository;

    public String sendNotification(List<String> tokens, String title, String content, String noticeType, String status) {
        List<String> registrationTokens = tokens;
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(content)
                .build();

        Map<String, String> data = new HashMap<>();
        data.put("type", noticeType);
        data.put("status", status);

        int successCount = 0;
        int failureCount = 0;

        for (String token : tokens) {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(notification)
                    .putAllData(data)
                    .build();
            try {
                firebaseMessaging.send(message);
                successCount++;
            } catch (FirebaseMessagingException e) {
                failureCount++;
                e.printStackTrace();
            }
        }

        return "Successfully sent to " + successCount + " devices, failures: " + failureCount;
    }

    @Override
    public void saveFcmToken(FcmToken req) {
        DeviceTokens deviceTokens = new DeviceTokens();

        try {
            User user = userRepository.findUserByIdAndDeletedAtIsNull(req.getUserId());
            if (user == null) {
                throw new HandlerException(ErrorCodeEnum.ER148.getCode(), ErrorCodeEnum.ER148.getMessage(), "api/save-fcm-token", HttpStatus.BAD_REQUEST);
            }
            DeviceTokens deviceTokensExists = deviceTokensRepository
                    .findDeviceTokensByUserIdAndDeletedAtIsNull(req.getUserId());
            if (deviceTokensExists != null) {
                deviceTokensExists.setToken(req.getToken());
                deviceTokensExists.setExpired(false);
                deviceTokensExists.setUpdatedAt(new Date());
                deviceTokensRepository.save(deviceTokensExists);
            } else {
                deviceTokens.setToken(req.getToken());
                deviceTokens.setExpired(false);
                deviceTokens.setUser(user);
                deviceTokens.setCreatedAt(new Date());

                deviceTokensRepository.save(deviceTokens);
            }
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), "api/save-fcm-token", handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), "api/save-fcm-token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<NotificationResponse> getNotificationByUser(HttpServletRequest request) {
        List<NotificationResponse> result = new ArrayList<>();
        try {
            User user = jwtUtil.extraUserFromRequest(request);
            user.getUserNotifications().stream()
                    .filter(notifications -> notifications.getDeletedAt() == null)
                    .sorted(Comparator.comparing(Notifications::getCreatedAt).reversed())
                    .forEach(notifications -> {
                        NotificationResponse response = ParseUtils.convertNotificationToNotificationResponse(notifications);
                        result.add(response);
                    });
            return result;
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), "api/notification/save-fcm-token", handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), "api/notification/save-fcm-token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updateNotification(String id) {
        try {
            HandlerValidateParams.validateInt(id, ErrorCodeEnum.ER149);
            Notifications notifications = notificationRepository.getNotificationsByIdAndDeletedAtIsNull(Long.parseLong(id));
            if (notifications == null) {
                throw new HandlerException(ErrorCodeEnum.ER150.getCode(), ErrorCodeEnum.ER150.getMessage(), HttpStatus.BAD_REQUEST);
            }

            notifications.setRead(true);
            notifications.setDeletedAt(new Date());
            notifications.setUpdatedAt(new Date());
            notificationRepository.save(notifications);
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), "api/notification", handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), "api/notification", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
