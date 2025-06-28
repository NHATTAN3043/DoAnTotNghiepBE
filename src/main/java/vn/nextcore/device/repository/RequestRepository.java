package vn.nextcore.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nextcore.device.entity.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long>{
    Request findRequestById(Long id);
}
