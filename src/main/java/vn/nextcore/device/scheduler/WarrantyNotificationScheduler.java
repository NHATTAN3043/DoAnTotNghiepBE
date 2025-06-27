package vn.nextcore.device.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.nextcore.device.entity.Device;
import vn.nextcore.device.entity.DeviceTokens;
import vn.nextcore.device.entity.Notifications;
import vn.nextcore.device.entity.User;
import vn.nextcore.device.repository.DeviceRepository;
import vn.nextcore.device.repository.NotificationRepository;
import vn.nextcore.device.repository.UserRepository;
import vn.nextcore.device.service.notification.INotificationService;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WarrantyNotificationScheduler {
    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private INotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private String PATH_DEVICE = "/next-device/device-info/";

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Transactional
    @Scheduled(cron = "0 0 8 * * *")
    public void checkWarrantyAndNotify() {
        LocalDate todayLocal = LocalDate.now();
        Date today = java.sql.Date.valueOf(todayLocal);
        LocalDate next7Days = todayLocal.plusDays(7);
        Date next7DaysDate = Date.from(
                next7Days.atStartOfDay(ZoneId.systemDefault()).toInstant()
        );

        List<Device> devices = deviceRepository.findDevicesWithMaintenanceExpiringWithin7Days(today, next7DaysDate);

        List<User> listBO = userRepository.findAllByRoleIdAndDeletedAtIsNull(1l);

        for (Device device : devices) {
            for (User user : listBO) {
                Notifications notifications = new Notifications();
                notifications.setTitle("Hết hạn bảo hành");
                notifications.setContent("#" + device.getId() + " " + device.getName() + " sắp hết hạn bảo hành " + dateFormat.format(device.getDateMaintenance()));
                notifications.setUser(user);
                notifications.setCreatedBy(user);
                notifications.setCreatedAt(new Date());
                notifications.setPath(PATH_DEVICE + device.getId());

                notificationRepository.save(notifications);
                List<String> tokens = user
                        .getUserDeviceTokens()
                        .stream()
                        .map(DeviceTokens::getToken )
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                String batchResponse = notificationService.sendNotification(tokens,
                        "Hết hạn bảo hành", "#" + device.getId() + " " + device.getName() + " sắp hết hạn bảo hành " + dateFormat.format(device.getDateMaintenance()),
                        "dateMaintenance", "new",PATH_DEVICE + device.getId());
            }
            device.setIsNotified(true);
            deviceRepository.save(device);
        }

    }
}
