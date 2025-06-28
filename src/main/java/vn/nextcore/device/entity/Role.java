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
@Table(name = "CHUCVU")
public class Role {
    public static final String ID = "maChucVu";
    public static final String NAME = "tenChucVu";
    public static final String ROLE = "role";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = NAME, length = 100)
    private String name;

    @OneToMany(mappedBy = ROLE, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();
}
