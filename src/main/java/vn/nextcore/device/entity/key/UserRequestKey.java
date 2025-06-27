package vn.nextcore.device.entity.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestKey implements Serializable {
    public static final String USER_ID = "maNguoiDung";
    public static final String REQUEST_ID = "maYeuCau";

    @Column(name = USER_ID)
    private Long userId;

    @Column(name = REQUEST_ID)
    private Long requestId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRequestKey that = (UserRequestKey) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, requestId);
    }
}
