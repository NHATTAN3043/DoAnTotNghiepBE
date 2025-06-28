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
@Table(name = "PHONGBAN")
public class Department {
    public static final String ID = "maPhongBan";
    public static final String NAME = "tenPhongBan";
    public static final String DEPARTMENT = "department";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = NAME, length = 100)
    private String name;

    @OneToMany(mappedBy = DEPARTMENT, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();
}
