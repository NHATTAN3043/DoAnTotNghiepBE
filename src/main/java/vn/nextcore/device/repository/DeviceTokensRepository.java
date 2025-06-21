package vn.nextcore.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nextcore.device.entity.DeviceTokens;

@Repository
public interface DeviceTokensRepository extends JpaRepository<DeviceTokens, Long> {
    DeviceTokens findDeviceTokensByUserIdAndDeletedAtIsNull(Long userId);

}
