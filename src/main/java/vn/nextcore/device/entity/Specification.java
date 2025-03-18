package vn.nextcore.device.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "THONGSO")
public class Specification {
    public static final String ID = "maThongSo";
    public static final String NAME = "tenThongSo";
    public static final String VALUE ="giaTri";
    public static final String SPECIFICATIONS = "specifications";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = NAME, length = 100, nullable = false)
    private String name;

    @Column(name = VALUE, nullable = false)
    private String value;

    @ManyToMany(mappedBy = SPECIFICATIONS)
    private Set<Device> devices = new HashSet<>();
}
