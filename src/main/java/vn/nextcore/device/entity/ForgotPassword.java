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
@Table(name = "forgot_password")
public class ForgotPassword {
    public static final String ID = "id";
    public static final String OTP = "otp";
    public static final String EXP = "expiration_time";
    public static final String USER_ID = "user_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID)
    private Long id;

    @Column(name = OTP, nullable = false)
    private Integer otp;

    @Column(name = EXP, nullable = false)
    private Date expirationTime;

    @OneToOne
    @JoinColumn(name = USER_ID, referencedColumnName = ID)
    private User user;
}
