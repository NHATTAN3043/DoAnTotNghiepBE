package vn.nextcore.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nextcore.device.entity.UserProject;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, Long> {
    void deleteById(Long id);
}
