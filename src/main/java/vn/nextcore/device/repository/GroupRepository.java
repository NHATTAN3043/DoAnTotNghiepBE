package vn.nextcore.device.repository;

import vn.nextcore.device.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findGroupById(Long id);
}
