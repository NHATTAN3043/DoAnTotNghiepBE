package vn.nextcore.device.controller;

import com.google.firebase.messaging.BatchResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.nextcore.device.dto.req.FcmToken;
import vn.nextcore.device.dto.resp.DataResponse;
import vn.nextcore.device.dto.resp.GroupResponse;
import vn.nextcore.device.dto.resp.NotificationResponse;
import vn.nextcore.device.service.notification.INotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {
    @Autowired
    private final INotificationService notificationService;

    @PostMapping("/save-fcm-token")
    @ResponseStatus(HttpStatus.OK)
    public void saveFcmToken(@RequestBody FcmToken req) {
         notificationService.saveFcmToken(req);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<List<NotificationResponse>> getNotificationsOfUser(HttpServletRequest request) {
        List<NotificationResponse> result = notificationService.getNotificationByUser(request);
        return new DataResponse<>(result);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateNotification(@PathVariable("id") String id) {
        notificationService.updateNotification(id);
    }

}
