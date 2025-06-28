package vn.nextcore.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.nextcore.device.entity.DeviceTokens;

@Repository
public interface DeviceTokensRepository extends JpaRepository<DeviceTokens, Long> {
    DeviceTokens findDeviceTokensByUserIdAndPlatformAndDeletedAtIsNull(Long userId, String platform);

    @Modifying
    @Transactional
    @Query("DELETE FROM DeviceTokens d WHERE d.user.id = :userId AND d.platform = :platform AND d.deletedAt IS NULL")
    void deleteDeviceTokensByUserIdAndPlatform(Long userId, String platform);
}
