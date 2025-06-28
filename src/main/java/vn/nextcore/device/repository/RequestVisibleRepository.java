package vn.nextcore.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nextcore.device.entity.UserRequestVisibility;
import vn.nextcore.device.entity.key.UserRequestKey;

import java.util.Optional;

@Repository
public interface RequestVisibleRepository extends JpaRepository<UserRequestVisibility, UserRequestKey> {
    Optional<UserRequestVisibility> findByUserIdAndRequestId(Long userId, Long requestId);
}
