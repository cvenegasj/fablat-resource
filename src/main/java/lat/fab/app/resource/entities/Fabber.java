package lat.fab.app.resource.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Fabber")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fabber implements java.io.Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idFabber", unique = true, nullable = false)
	private Integer id;

	@Column(name = "email", unique = true, nullable = false)
	private String email;

//	@Column(name = "password", length = 68, nullable = false)
//	private String password;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "firstName")
	private String firstName;

	@Column(name = "lastName")
	private String lastName;

	@Column(name = "isFabAcademyGrad", nullable = false)
	private Boolean isFabAcademyGrad;

	@Column(name = "fabAcademyGradYear")
	private Integer fabAcademyGradYear;

	@Column(name = "cellPhoneNumber")
	private String cellPhoneNumber;

	@Column(name = "isNomade", nullable = false)
	private Boolean isNomade;

	@Column(name = "mainQuote")
	private String mainQuote;

	@Column(name = "city")
	private String city;

	@Column(name = "country")
	private String country;

	@Column(name = "weekGoal")
	private String weekGoal;

	@Column(name = "avatarUrl")
	private String avatarUrl;

	@Column(name = "enabled", nullable = false)
	private Boolean enabled;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idLab")
	private Lab lab;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "fabber")
	private FabberInfo fabberInfo;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "fabber")
	@OrderBy("id")
	private Set<GroupMember> groupMembers = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "fabber")
	@Fetch(FetchMode.JOIN)
	private Set<RoleFabber> roleFabbers = new HashSet<>();

	// rest of the attributes are inherited from fablabs.io API

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
		Fabber other = (Fabber) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
