package vn.nextcore.device.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "NHACUNGCAP")
public class Provider {
    public static final String ID = "maNhaCungCap";
    public static final String NAME = "tenNhaCungCap";
    public static final String ADDRESS = "diaChi";
    public static final String PHONE_NUMBER = "soDienThoai";
    public static final String PROVIDER = "provider";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = ID, nullable = false)
    private Long id;

    @Column(name = NAME, nullable = false)
    private String name;

    @Column(name = ADDRESS)
    private String address;

    @Column(name = PHONE_NUMBER)
    private String phoneNumber;

    // relationship of devices
    @OneToMany(mappedBy = PROVIDER, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Device> devices = new ArrayList<>();

    // relationship of delivery_notes
    @OneToMany(mappedBy = PROVIDER, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeliveryNote> deliveryNotes = new ArrayList<>();
}
