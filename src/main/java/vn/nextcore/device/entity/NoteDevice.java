package vn.nextcore.device.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "GHICHUTHIETBI")
public class NoteDevice {
    public static final String ID = "maGCTB";
    public static final String DESCRIPTION_DEVICE = "moTaTrangThaiTB";
    public static final String PRICE_DEVICE = "soTienGhiChu";
    public static final String DELIVERY_NOTE_ID = "maLSTB";
    public static final String DEVICE_ID = "maThietBi";
    public static final String DATE_NOTE = "ngayGhiChu";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = DESCRIPTION_DEVICE)
    private String descriptionDevice;

    @Column(name = PRICE_DEVICE)
    private Double priceDevice;

    @Column(name = DATE_NOTE)
    private Date dateNote;

    @ManyToOne
    @JoinColumn(name = DELIVERY_NOTE_ID)
    private DeliveryNote deliveryNote;

    @ManyToOne
    @JoinColumn(name = DEVICE_ID)
    private Device device;
}
