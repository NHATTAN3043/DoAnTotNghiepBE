package vn.nextcore.device.service.notification;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.nextcore.device.dto.req.FcmToken;
import vn.nextcore.device.entity.DeviceTokens;
import vn.nextcore.device.entity.User;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.DeviceTokensRepository;
import vn.nextcore.device.repository.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements INotificationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceTokensRepository deviceTokensRepository;

    @Autowired FirebaseMessaging firebaseMessaging;

    public String sendNotification(List<String> tokens, String title, String content, String noticeType, String status) {
        List<String> registrationTokens = tokens;
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(content)
                .build();

        Map<String, String> data = new HashMap<>();
        data.put("type", noticeType);
        data.put("status", status);

        Message message = Message
                .builder()
                .setToken(registrationTokens.get(0))
                .setNotification(notification)
                .putAllData(data)
                .build();
        try {
            firebaseMessaging.send(message);
            return "Success sending notification";
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return "Error sending notification";
        }
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
}
