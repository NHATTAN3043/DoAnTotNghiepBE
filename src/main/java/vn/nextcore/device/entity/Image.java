package vn.nextcore.device.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "HINHANH")
public class Image {
    public static final String ID = "maHinhAnh";
    public static final String NAME = "tenHinhAnh";
    public static final String DEVICE_ID = "maThietBi";
    public static final String PATH = "duongDan";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = NAME, nullable = false, length = 500)
    private String name;

    @Column(name = PATH, nullable = false, length = 500)
    private String path;

    @ManyToOne
    @JoinColumn(name = DEVICE_ID)
    private Device device;
}
