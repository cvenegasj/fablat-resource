package lat.fab.app.resource.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

	@Column(name = "replicationNumber")
	private Integer replicationNumber;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description", nullable = false)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private EventType type;

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

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "idLocation", nullable = false)
	private Location location;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idSubGroup", nullable = false)
	private SubGroup subGroup;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "workshop")
//	@Fetch(FetchMode.JOIN)
	private Set<WorkshopTutor> workshopTutors = new HashSet<>();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Workshop other = (Workshop) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
