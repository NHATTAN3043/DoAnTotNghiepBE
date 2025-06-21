package vn.nextcore.device.controller;

import com.google.firebase.messaging.BatchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.nextcore.device.dto.req.FcmToken;
import vn.nextcore.device.service.notification.INotificationService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {
    @Autowired
    private final INotificationService notificationService;

    @PostMapping("/save-fcm-token")
    @ResponseStatus(HttpStatus.OK)
    public void saveFcmToken(@RequestBody FcmToken req) {
         notificationService.saveFcmToken(req);
    }

}
