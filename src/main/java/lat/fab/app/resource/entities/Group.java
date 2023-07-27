package lat.fab.app.resource.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Group")
@Getter
@Setter
public class Group implements java.io.Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idGroup", unique = true, nullable = false)
	private Integer id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "reunionDay")
	private String reunionDay;

	@Column(name = "reunionTime")
	private LocalTime reunionTime;

	@Column(name = "mainUrl")
	private String mainUrl;

	@Column(name = "secondaryUrl")
	private String secondaryUrl;

	@Column(name = "symbioUrl")
	private String symbioUrl;

	@Column(name = "photoUrl")
	private String photoUrl;

	@Column(name = "creationDateTime", nullable = false)
	private LocalDateTime creationDateTime;

	@Column(name = "enabled", nullable = false)
	private Boolean enabled;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "group")
	@OrderBy("date(creationDateTime) asc")
	private Set<GroupMember> groupMembers = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "group")
	private Set<SubGroup> subGroups = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "group")
	private Set<ActivityLog> activities = new HashSet<>();

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
		Group other = (Group) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
