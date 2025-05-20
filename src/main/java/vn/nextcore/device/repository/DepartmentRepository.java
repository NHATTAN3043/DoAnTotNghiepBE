package vn.nextcore.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nextcore.device.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Department findDepartmentById(Long id);
}
