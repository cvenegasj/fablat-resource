package lat.fab.app.resource.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "SubGroupMember")
@Getter
@Setter
public class SubGroupMember implements java.io.Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idSubGroupMember", unique = true, nullable = false)
	private Integer id;

	@Column(name = "isCoordinator", nullable = false)
	private Boolean isCoordinator;

	@Column(name = "notificationsEnabled", nullable = false)
	private Boolean notificationsEnabled;

	@Column(name = "creationDateTime", nullable = false)
	private LocalDateTime creationDateTime;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idSubGroup", nullable = false)
	private SubGroup subGroup;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idGroupMember", nullable = false)
	private GroupMember groupMember;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "subGroupMember")
	private Set<WorkshopTutor> workshopTutors = new HashSet<>();

}
