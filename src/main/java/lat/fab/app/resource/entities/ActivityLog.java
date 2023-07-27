package lat.fab.app.resource.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "ActivityLog")
@Data
public class ActivityLog implements java.io.Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idActivityLog", unique = true, nullable = false)
	private Integer id;

	@Column(name = "level", nullable = false)
	private String level;

	@Column(name = "type", nullable = false)
	private String type;

	@Column(name = "visibility", nullable = false)
	private String visibility;

	@Column(name = "creationDateTime", nullable = false)
	private LocalDateTime creationDateTime;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idFabber", nullable = false)
	private Fabber fabber;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idGroup")
	private Group group;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idSubGroup")
	private SubGroup subGroup;
}
