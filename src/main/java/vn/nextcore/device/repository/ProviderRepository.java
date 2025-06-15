package vn.nextcore.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nextcore.device.entity.Provider;

import java.util.List;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    Provider findProviderByIdAndDeletedAtIsNull(Long id);

    List<Provider> findProviderByNameIsLikeIgnoreCaseAndDeletedAtIsNull(String name);

    List<Provider> findProviderByDeletedAtIsNull();
}
