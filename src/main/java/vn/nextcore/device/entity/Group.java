package vn.nextcore.device.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "LOAI")
public class Group {
    public static final String ID = "maLoai";
    public static final String NAME = "tenLoai";
    public static final String QUANTITY = "soLuong";
    public static final String GROUP = "group";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = NAME, nullable = false)
    private String name;

    @Column(name = QUANTITY, nullable = false)
    @Min(0)
    private Integer quantity;

    // relationship of device
    @OneToMany(mappedBy = GROUP, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Device> devices = new ArrayList<>();

    // relationship of request_group
    @OneToMany(mappedBy = GROUP, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RequestGroup> requestGroups = new HashSet<>();
}
