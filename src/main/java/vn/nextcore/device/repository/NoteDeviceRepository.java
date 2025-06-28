package vn.nextcore.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.nextcore.device.entity.NoteDevice;

import java.util.List;

@Repository
public interface NoteDeviceRepository extends JpaRepository<NoteDevice, Long> {
    @Query("SELECT COUNT( nd.device.id ) " +
            "FROM NoteDevice nd " +
            "JOIN nd.device d " +
            "JOIN nd.deliveryNote dn WHERE d.group.id = :groupId AND dn.typeNote = :typeNote AND dn.request.id = :requestId"

    )
    Integer countDeviceResByGroupIdAndRequestIdAndStatus(@Param("groupId") Long groupId,
                                                         @Param("requestId") Long requestId,
                                                         @Param("typeNote") String typeNote);


    @Query("SELECT COUNT( nd.device.id ) " +
            "FROM NoteDevice nd " +
            "JOIN nd.deliveryNote dn WHERE dn.typeNote = :action AND EXTRACT(YEAR FROM dn.createdAt) =:year"
    )
    Integer countDeviceByActionAndYear(@Param("action") String action, @Param("year") String year);

    @Query("SELECT COUNT( nd.device.id ) " +
            "FROM NoteDevice nd " +
            "JOIN nd.deliveryNote dn WHERE dn.typeNote = :action AND EXTRACT(MONTH FROM dn.createdAt) =:month AND EXTRACT(YEAR FROM dn.createdAt) =:year"
    )
    Integer countDeviceByActionAndMonthAndYear(@Param("action") String action, @Param("month") int month,  @Param("year") String year);
}
