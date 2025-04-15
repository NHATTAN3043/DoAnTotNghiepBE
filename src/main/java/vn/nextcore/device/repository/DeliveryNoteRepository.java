package vn.nextcore.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nextcore.device.entity.DeliveryNote;

@Repository
public interface DeliveryNoteRepository extends JpaRepository<DeliveryNote, Long> {
}
