package vn.nextcore.device.repository;

import org.springframework.data.domain.Sort;
import vn.nextcore.device.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findGroupById(Long id);

    List<Group> findAll(Sort sort);
}
