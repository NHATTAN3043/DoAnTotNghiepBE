package vn.nextcore.device.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "LICHSUTHIETBI")
public class DeliveryNote {
    public static final String ID = "maLSTB";
    public static final String TITLE = "tieuDe";
    public static final String DELIVERY_DATE = "thoiGianNhan";
    public static final String TYPE_NOTE = "loaiHanhDong";
    public static final String DESCRIPTION = "moTa";
    public static final String IS_CONFIRM = "daXacNhan";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATE_AT = "updated_at";
    public static final String DELETED_AT = "deleted_at";
    public static final String REQUEST_ID = "maYeuCau";
    public static final String CREATED_BY = "maNguoiTao";
    public static final String PROVIDER_ID = "maNhaCungCap";
    public static final String DELIVERY_NOTE = "deliveryNote";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = TITLE, length = 100)
    private String title;

    @Column(name = DELIVERY_DATE, nullable = false)
    private Date deliveryDate;

    @Column(name = TYPE_NOTE, length = 100)
    private String typeNote;

    @Column(name = DESCRIPTION, length = 500)
    private String description;

    @Column(name = IS_CONFIRM)
    private Boolean isConfirm;

    @Column(name = CREATED_AT)
    private Date createdAt;

    @Column(name = UPDATE_AT)
    private Date updatedAt;

    @Column(name = DELETED_AT)
    private Date deletedAt;

    // relationship of request
    @ManyToOne
    @JoinColumn(name = REQUEST_ID)
    private Request request;

    // relationship of user
    @ManyToOne
    @JoinColumn(name = CREATED_BY, nullable = false)
    private User createdBy;

    // relationship of provider
    @ManyToOne
    @JoinColumn(name = PROVIDER_ID)
    private Provider provider;

    // relationship of note_device
    @OneToMany(mappedBy = DELIVERY_NOTE, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NoteDevice> noteDevices = new ArrayList<>();
}
