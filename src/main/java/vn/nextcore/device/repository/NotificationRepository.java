package vn.nextcore.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nextcore.device.entity.Notifications;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, Long> {

    Notifications getNotificationsByIdAndDeletedAtIsNull(Long id);
}
