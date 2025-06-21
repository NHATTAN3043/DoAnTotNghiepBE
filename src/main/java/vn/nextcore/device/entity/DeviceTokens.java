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
@Table(name = "DEVICE_TOKENS")
public class DeviceTokens {
    public static final String ID = "id";
    public static final String TOKEN = "token";
    public static final String USER_ID = "maNguoiDung";
    public static final String PLATFORM = "platform";
    public static final String EXPIRED = "expired";
    public static final String BROWSER = "browser";
    public static final String CREATED_BY = "maNguoiTao";
    public static final String UPDATED_AT = "updated_at";
    public static final String CREATED_AT = "created_at";
    public static final String DELETED_AT = "deleted_at";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = TOKEN, length = 150)
    private String token;

    @Column(name = PLATFORM, length = 500)
    private String platform;

    @Column(name = BROWSER, length = 50)
    private String browser;

    @Column(name = EXPIRED, length = 50)
    private Boolean expired;

    @Column(name = UPDATED_AT)
    private Date updatedAt;

    @Column(name = CREATED_AT)
    private Date createdAt;

    @Column(name = DELETED_AT)
    private Date deletedAt;

    @ManyToOne
    @JoinColumn(name = CREATED_BY, referencedColumnName = USER_ID)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = USER_ID, referencedColumnName = USER_ID, nullable = false)
    private User user;
}
