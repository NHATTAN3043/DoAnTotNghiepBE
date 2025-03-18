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
@Table(name = "NGUOIDUNG_DUAN")
public class UserProject {
    public static final String ID = "maNDDA";
    public static final String DATE_OF_JOIN = "ngayThamGia";
    public static final String USER_ID = "maNguoiDung";
    public static final String PROJECT_ID = "maDuAn";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = DATE_OF_JOIN)
    private Date dateOfJoin;

    @ManyToOne
    @JoinColumn(name = USER_ID)
    private User user;

    @ManyToOne
    @JoinColumn(name = PROJECT_ID)
    private Project project;
}
