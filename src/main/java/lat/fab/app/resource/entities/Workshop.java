package lat.fab.app.resource.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Workshop")
@Getter
@Setter
public class Workshop implements java.io.Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idWorkshop", unique = true, nullable = false)
	private Integer id;

	@Column(name = "replicationNumber", nullable = false)
	private Integer replicationNumber;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "startDateTime", nullable = false)
	private LocalDateTime startDateTime;

	@Column(name = "endDateTime", nullable = false)
	private LocalDateTime endDateTime;

	@Column(name = "isPaid", nullable = false)
	private Boolean isPaid;

	@Column(name = "price", precision = 8, scale = 2)
	private BigDecimal price;

	@Column(name = "currency")
	private String currency;

	@Column(name = "facebookUrl")
	private String facebookUrl;

	@Column(name = "ticketsUrl")
	private String ticketsUrl;

	@Column(name = "creationDateTime", nullable = false)
	private LocalDateTime creationDateTime;

	@Column(name = "enabled", nullable = false)
	private Boolean enabled;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idLocation", nullable = false)
	private Location location;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idSubGroup", nullable = false)
	private SubGroup subGroup;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "workshop")
	@Fetch(FetchMode.JOIN)
	private Set<WorkshopTutor> workshopTutors = new HashSet<>();

}
