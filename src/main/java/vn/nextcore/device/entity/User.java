package vn.nextcore.device.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "NGUOIDUNG")
public class User {
    public static final String ID = "maNguoiDung";
    public static final String NAME = "ten";
    public static final String EMAIL = "email";
    public static final String PHONE_NUMBER = "soDienThoai";
    public static final String GENDER = "gioiTinh";
    public static final String DATE_OF_BIRTH = "ngaySinh";
    public static final String ADDRESS = "diaChi";
    public static final String AVATAR_URL = "anhDaiDien";
    public static final String PASSWORD = "matKhau";
    public static final String ROLE_ID = "maChucVu";
    public static final String DEPARTMENT_ID = "maPhongBan";
    public static final String USER = "user";
    public static final String CREATED_BY = "createdBy";
    public static final String USING_BY = "usingBy";
    public static final String USER_ASSIGNED = "userAssigned";
    public static final String APPROVER = "approver";
    public static final String UPDATED_AT = "updated_at";
    public static final String CREATED_AT = "created_at";
    public static final String DELETED_AT = "deleted_at";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = NAME, length = 100, nullable = false)
    private String userName;

    @Column(name = EMAIL, length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = PHONE_NUMBER, length = 20)
    private String phoneNumber;

    @Column(name = GENDER)
    private String gender;

    @Column(name = DATE_OF_BIRTH)
    private Date dateOfBirth;

    @Column(name = ADDRESS)
    private String address;

    @Column(name = AVATAR_URL, length = 500)
    private String avatarUrl;

    @Column(name = PASSWORD, nullable = false)
    @Size(min = 6, max = 500)
    private String password;

    @Column(name = UPDATED_AT)
    private Date updatedAt;

    @Column(name = CREATED_AT)
    private Date createdAt;

    @Column(name = DELETED_AT)
    private Date deletedAt;

    @ManyToOne
    @JoinColumn(name = ROLE_ID)
    private Role role;

    @ManyToOne
    @JoinColumn(name = DEPARTMENT_ID)
    private Department department;

    // relationship of user projects
    @OneToMany(mappedBy = USER, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UserProject> userProjects = new ArrayList<>();

    // relationship of device
    @OneToMany(mappedBy = CREATED_BY, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Device> devices = new ArrayList<>();

    @OneToMany(mappedBy = USING_BY, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Device> devicesUsing = new ArrayList<>();

    // relationship of request
    @OneToMany(mappedBy = CREATED_BY, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Request> requestsForCreatedBy = new ArrayList<>();

    @OneToMany(mappedBy = USER_ASSIGNED, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Request> requestsForUserAssigned = new ArrayList<>();

    @OneToMany(mappedBy = APPROVER, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Request> requestsForApprover = new ArrayList<>();

    // relationship of delivery_note
    @OneToMany(mappedBy = CREATED_BY, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<DeliveryNote> deliveryNotes = new HashSet<>();

    //relationship of forgot_password
    @OneToOne(mappedBy = USER, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ForgotPassword forgotPassword;

    // relationship of notifications
    @OneToMany(mappedBy = CREATED_BY, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notifications> CreatedByOfNotifications = new ArrayList<>();

    @OneToMany(mappedBy = USER, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notifications> userNotifications = new ArrayList<>();

    // relationship of deviceTokens
    @OneToMany(mappedBy = CREATED_BY, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeviceTokens> CreatedByOfDeviceTokens = new ArrayList<>();

    @OneToMany(mappedBy = USER, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeviceTokens> userDeviceTokens = new ArrayList<>();
}
