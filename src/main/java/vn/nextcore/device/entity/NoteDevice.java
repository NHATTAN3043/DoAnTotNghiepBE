package vn.nextcore.device.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "note_devices")
public class NoteDevice {
    public static final String ID = "id";
    public static final String DESCRIPTION_DEVICE = "description_device";
    public static final String PRICE_DEVICE = "price_device";
    public static final String DELIVERY_NOTE_ID = "delivery_note_id";
    public static final String DEVICE_ID = "device_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = DESCRIPTION_DEVICE)
    private String descriptionDevice;

    @Column(name = PRICE_DEVICE)
    private Double priceDevice;

    @ManyToOne
    @JoinColumn(name = DELIVERY_NOTE_ID)
    private DeliveryNote deliveryNote;

    @ManyToOne
    @JoinColumn(name = DEVICE_ID)
    private Device device;
}
