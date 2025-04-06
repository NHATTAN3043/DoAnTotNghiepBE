package vn.nextcore.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nextcore.device.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findProjectById(Long id);
}
