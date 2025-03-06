package vn.nextcore.device.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "devices")
public class Device {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PRICE_BUY = "price_buy";
    public static final String PRICE_SELL = "price_sell";
    public static final String DESCRIPTION = "description";
    public static final String STATUS = "status";
    public static final String DATE_BUY = "date_buy";
    public static final String DATE_SELL = "date_sell";
    public static final String DATE_MAINTENANCE = "date_maintenance";
    public static final String UPDATED_AT = "updated_at";
    public static final String CREATED_AT = "created_at";
    public static final String DELETED_AT = "deleted_at";
    public static final String GROUP_ID = "group_id";
    public static final String CREATED_BY = "create_by";
    public static final String PROVIDER_ID = "provider_id";
    public static final String DEVICE_SPECIFICATIONS = "device_specifications";
    public static final String DEVICE_ID = "device_id";
    public static final String SPECIFICATION_ID = "specification_id";
    public static final String DEVICE = "device";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = NAME)
    private String name;

    @Column(name = PRICE_BUY)
    @Min(0)
    private Double priceBuy;

    @Column(name = PRICE_SELL)
    @Min(0)
    private Double priceSell;

    @Column(name = DESCRIPTION)
    private String description;

    @Column(name = STATUS, nullable = false, length = 100)
    private String status;

    @Column(name = DATE_BUY)
    private Date dateBuy;

    @Column(name = DATE_SELL)
    private Date dateSell;

    @Column(name = DATE_MAINTENANCE)
    private Date dateMaintenance;

    @Column(name = UPDATED_AT)
    private Date updatedAt;

    @Column(name = CREATED_AT)
    private Date createdAt;

    @Column(name = DELETED_AT)
    private Date deletedAt;

    @ManyToOne
    @JoinColumn(name = GROUP_ID)
    private Group group;

    @ManyToOne
    @JoinColumn(name = CREATED_BY)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = PROVIDER_ID)
    private Provider provider;

    // relationship of device_specification
    @ManyToMany
    @JoinTable(
            name = DEVICE_SPECIFICATIONS,
            joinColumns = @JoinColumn(name = DEVICE_ID),
            inverseJoinColumns = @JoinColumn(name = SPECIFICATION_ID)
    )
    private Set<Specification> specifications = new HashSet<>();

    // relationship of note_device
    @OneToMany(mappedBy = DEVICE, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NoteDevice> noteDevices = new ArrayList<>();

    //relationship of images
    @OneToMany(mappedBy = DEVICE, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Image> images = new HashSet<>();

    public void addSpecification(Specification specification) {
        if (specification != null) {
            specifications.add(specification);
            specification.getDevices().add(this);
        }
    }

    public void removeSpecification(Specification specification) {
        if (specification != null && this.specifications.contains(specification)) {
            specifications.remove(specification);
            if (specification.getDevices() != null){
                specification.getDevices().remove(this);
            }
        } else {
            throw new IllegalArgumentException();
        }

    }
}
