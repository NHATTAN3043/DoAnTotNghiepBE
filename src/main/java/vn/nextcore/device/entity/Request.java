package vn.nextcore.device.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requests")
public class Request {
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String STATUS = "status";
    public static final String CREATED_DATE = "created_date";
    public static final String APPROVED_DATE = "approved_date";
    public static final String CREATED_BY = "created_by";
    public static final String USER_ASSIGNED = "user_assigned";
    public static final String APPROVER = "approver";
    public static final String PROJECT_ID = "project_id";
    public static final String REQUEST = "request";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = TITLE, nullable = false, length = 150)
    private String title;

    @Column(name = DESCRIPTION, length = 500)
    private String description;

    @Column(name = STATUS, nullable = false, length = 50)
    private String status;

    @Column(name = CREATED_DATE)
    private Date createdDate;

    @Column(name = APPROVED_DATE)
    private Date approvedDate;

    // relationship of user
    @ManyToOne
    @JoinColumn(name = CREATED_BY, referencedColumnName = ID, nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = USER_ASSIGNED, referencedColumnName = ID, nullable = false)
    private User userAssigned;

    @ManyToOne
    @JoinColumn(name = APPROVER, referencedColumnName = ID, nullable = false)
    private User approver;

    // relationship of project
    @ManyToOne
    @JoinColumn(name = PROJECT_ID)
    private Project project;

    // relationship of request_group
    @OneToMany(mappedBy = REQUEST, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RequestGroup> requestGroups = new HashSet<>();

    //relationship of delivery_notes
    @OneToMany(mappedBy = REQUEST, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeliveryNote> deliveryNotes = new ArrayList<>();
}
