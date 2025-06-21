package vn.nextcore.device.scheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.nextcore.device.entity.Device;
import vn.nextcore.device.repository.DeviceRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WarrantyNotificationScheduler {
    @Autowired
    private DeviceRepository deviceRepository;

    @Scheduled(cron = "0 0 9 * * *")
    public void checkWarrantyAndNotify() {
        LocalDate todayLocal = LocalDate.now();
        LocalDate next7DaysLocal = todayLocal.plusDays(7);

        Date today = java.sql.Date.valueOf(todayLocal);
        Date next7Days = java.sql.Date.valueOf(next7DaysLocal);

        List<Device> devices = deviceRepository.findDevicesWithMaintenanceExpiringWithin7Days(today, next7Days);


    }
}
