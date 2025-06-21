package vn.nextcore.device.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "YEUCAU")
public class Request {
    public static final String ID = "maYeuCau";
    public static final String TITLE = "tieuDe";
    public static final String DESCRIPTION = "moTa";
    public static final String STATUS = "trangThai";
    public static final String CREATED_DATE = "ngayTao";
    public static final String APPROVED_DATE = "ngayDuyet";
    public static final String CREATED_BY = "maNguoiTao";
    public static final String USER_ASSIGNED = "nguoiDuocPhan";
    public static final String APPROVER = "maNguoiDuyet";
    public static final String PROJECT_ID = "maDuAn";
    public static final String REQUEST = "request";
    public static final String USER_ID = "maNguoiDung";
    public static final String REQUEST_TYPE = "loaiYeuCau";
    public static final String UPDATED_AT = "updated_at";
    public static final String CREATED_AT = "created_at";
    public static final String DELETED_AT = "deleted_at";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = TITLE, nullable = false, length = 150)
    private String title;

    @Column(name = DESCRIPTION, length = 500)
    private String description;

    @Column(name = STATUS, nullable = false, length = 50)
    private String status;

    @Column(name = REQUEST_TYPE, nullable = false, length = 50)
    private String requestType;

    @Column(name = CREATED_DATE)
    private Date createdDate;

    @Column(name = APPROVED_DATE)
    private Date approvedDate;

    @Column(name = UPDATED_AT)
    private Date updatedAt;

    @Column(name = CREATED_AT)
    private Date createdAt;

    @Column(name = DELETED_AT)
    private Date deletedAt;

    // relationship of user
    @ManyToOne
    @JoinColumn(name = CREATED_BY, referencedColumnName = USER_ID, nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = USER_ASSIGNED, referencedColumnName = USER_ID, nullable = true)
    private User userAssigned;

    @ManyToOne
    @JoinColumn(name = APPROVER, referencedColumnName = USER_ID, nullable = true)
    private User approver;

    // relationship of project
    @ManyToOne
    @JoinColumn(name = PROJECT_ID)
    private Project project;

    // relationship of request_group
    @OneToMany(mappedBy = REQUEST, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RequestGroup> requestGroups = new HashSet<>();

    //relationship of delivery_notes
    @OneToMany(mappedBy = REQUEST, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeliveryNote> deliveryNotes = new ArrayList<>();
}
