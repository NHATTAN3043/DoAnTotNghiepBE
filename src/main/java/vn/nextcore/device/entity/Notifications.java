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
@Table(name = "THONGBAO")
public class Notifications {
    public static final String ID = "maThongBao";
    public static final String TITLE = "tieuDe";
    public static final String CONTENT = "noiDung";
    public static final String READ = "daDoc";
    public static final String PATH = "duongDan";
    public static final String USER_ID = "maNguoiDung";
    public static final String CREATED_BY = "maNguoiTao";
    public static final String UPDATED_AT = "updated_at";
    public static final String CREATED_AT = "created_at";
    public static final String DELETED_AT = "deleted_at";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = TITLE, length = 150)
    private String title;

    @Column(name = CONTENT, length = 500)
    private String content;

    @Column(name = READ)
    private Boolean read = false;

    @Column(name = PATH, length = 500)
    private String path;

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
