package vn.nextcore.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.nextcore.device.entity.Device;

import java.util.Date;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Device findDeviceByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT COUNT(s) > 0 FROM Device d JOIN d.specifications s " +
            "WHERE d.id = :deviceId AND s.id = :specificationId")
    boolean existsByDeviceIdAndSpecificationId(@Param("deviceId") Long deviceId,
                                               @Param("specificationId") Long specificationId);
}
