package vn.nextcore.device.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nextcore.device.entity.key.UserRequestKey;

@Entity
@Table(name = "NGUOIDUNG_YEUCAU_AN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestVisibility {
    public static final String USER_ID = "maNguoiDung";
    public static final String REQUEST_ID = "maYeuCau";
    public static final String IS_HIDDEN = "biAn";

    @EmbeddedId
    private UserRequestKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = USER_ID)
    private User user;

    @ManyToOne
    @MapsId("requestId")
    @JoinColumn(name = REQUEST_ID)
    private Request request;

    @Column(name = IS_HIDDEN, nullable = false)
    private boolean hidden = false;
}
