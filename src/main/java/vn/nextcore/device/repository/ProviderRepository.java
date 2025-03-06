package vn.nextcore.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nextcore.device.entity.Provider;
@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    Provider findProviderById(Long id);
}
