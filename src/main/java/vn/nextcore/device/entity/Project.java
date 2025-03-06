package vn.nextcore.device.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "projects")
public class Project {
    public static final String ID = "id";
    public static final String PROJECT_NAME = "project_name";
    public static final String PROJECT = "project";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = PROJECT_NAME, nullable = false)
    private String projectName;

    @OneToMany(mappedBy = PROJECT, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserProject> userProjects = new ArrayList<>();

    @OneToMany(mappedBy = PROJECT, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Request> requests = new ArrayList<>();
}
