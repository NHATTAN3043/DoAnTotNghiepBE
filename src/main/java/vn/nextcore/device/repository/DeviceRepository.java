package vn.nextcore.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.nextcore.device.entity.Device;

import java.util.Date;
import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    Device findDeviceById(Long id);
    Device findDeviceByIdAndDeletedAtIsNull(Long id);
    Integer countDeviceByDeletedAtIsNull();

    Integer countDeviceByStatusAndDeletedAtIsNull(String status);

    Integer countDeviceByGroupIdAndStatusAndDeletedAtIsNull(Long groupId, String status);

    @Query("SELECT COUNT(s) > 0 FROM Device d JOIN d.specifications s " +
            "WHERE d.id = :deviceId AND s.id = :specificationId")
    boolean existsByDeviceIdAndSpecificationId(@Param("deviceId") Long deviceId,
                                               @Param("specificationId") Long specificationId);

    boolean existsDeviceByProviderIdAndDeletedAtIsNull(Long providerId);

    boolean existsDeviceByGroupIdAndDeletedAtIsNull(Long id);

    @Query("SELECT d FROM Device d WHERE d.dateMaintenance BETWEEN :today AND :next7Days AND (d.isNotified = false OR d.isNotified IS NULL)")
    List<Device> findDevicesWithMaintenanceExpiringWithin7Days(Date today, Date next7Days);
}
