package vn.nextcore.device.entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "OTP")
public class ForgotPassword {
    public static final String ID = "ma";
    public static final String OTP = "otp";
    public static final String EXP = "thoiHanOTP";
    public static final String USER_ID = "maNguoiDung";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID)
    private Long id;

    @Column(name = OTP, nullable = false)
    private Integer otp;

    @Column(name = EXP, nullable = false)
    private Date expirationTime;

    @OneToOne
    @JoinColumn(name = USER_ID, referencedColumnName = USER_ID)
    private User user;
}
