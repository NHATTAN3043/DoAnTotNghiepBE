package vn.nextcore.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.nextcore.device.entity.Specification;

import java.util.List;
import java.util.Set;

public interface SpecificationRepository extends JpaRepository<Specification, Long> {
    Specification findSpecificationById(Long id);

    @Query("SELECT s FROM Specification s WHERE s.id IN :ids ")
    Set<Specification> findAllByIdIn(@Param("ids") Set<Long> ids);

    @Query("SELECT s FROM Specification s WHERE lower(s.name) = lower(:name) AND lower(s.value) = lower(:value)")
    Specification findSpecificationByNameAndValue(@Param("name") String name, @Param("value") String value);
}
