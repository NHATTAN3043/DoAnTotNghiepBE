package vn.nextcore.device.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "LOAITHIETBIYEUCAU")
public class RequestGroup {
    public static final String ID = "maLTBYC";
    public static final String QUANTITY = "soLuong";
    public static final String STATUS = "trangThai";
    public static final String GROUP_ID = "maLoaiThietBi";
    public static final String REQUEST_ID = "maYeuCau";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = QUANTITY, nullable = false)
    @Min(1)
    private Integer quantity;

    @Column(name = STATUS)
    private String status;

    @ManyToOne
    @JoinColumn(name = GROUP_ID)
    private Group group;

    @ManyToOne
    @JoinColumn(name = REQUEST_ID)
    private Request request;
}
