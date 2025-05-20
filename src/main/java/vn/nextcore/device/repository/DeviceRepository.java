package vn.nextcore.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.nextcore.device.entity.Device;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    Device findDeviceById(Long id);
    Device findDeviceByIdAndDeletedAtIsNull(Long id);

    List<Device> findDeviceByDateMaintenanceBetween(Date start, Date end);

    @Query("SELECT COUNT(s) > 0 FROM Device d JOIN d.specifications s " +
            "WHERE d.id = :deviceId AND s.id = :specificationId")
    boolean existsByDeviceIdAndSpecificationId(@Param("deviceId") Long deviceId,
                                               @Param("specificationId") Long specificationId);
}
