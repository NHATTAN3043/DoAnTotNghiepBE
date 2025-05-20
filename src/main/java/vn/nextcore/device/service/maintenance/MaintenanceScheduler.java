package vn.nextcore.device.service.maintenance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.nextcore.device.dto.resp.DeviceResponse;
import vn.nextcore.device.entity.Device;
import vn.nextcore.device.repository.DeviceRepository;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class MaintenanceScheduler {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private DeviceRepository deviceRepository;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


    @Scheduled(cron = "0 * * * * *")
    public void notifyUpcomingMaintenance() {
        Date now = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        Date sevenDaysLater = calendar.getTime();

        List<Device> devices = deviceRepository.findDeviceByDateMaintenanceBetween(now, sevenDaysLater);

        for (Device device : devices) {
            if (!device.getIsNotified()) {
                DeviceResponse deviceResponse = new DeviceResponse();
                deviceResponse.setDeviceId(device.getId());
                deviceResponse.setName(device.getName());
                deviceResponse.setDateMaintenance(dateFormat.format(device.getDateMaintenance()));

                messagingTemplate.convertAndSend("/topic/maintenance", deviceResponse);
                device.setIsNotified(true);
                deviceRepository.save(device);
                System.out.println("aaaaa");
            }
        }
    }
}
