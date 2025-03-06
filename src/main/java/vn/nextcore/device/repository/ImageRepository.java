package vn.nextcore.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.nextcore.device.entity.Image;

import java.util.Set;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("SELECT i FROM Image i WHERE i.id IN :ids AND i.device.id = :deviceId")
    Set<Image> findAllByDeviceIdAndIdIn(@Param("ids") Set<Long> ids, @Param("deviceId") Long deviceId);

    boolean existsByIdAndDeviceId(Long id, Long deviceId);

    int countByDeviceId(Long deviceId);
}
