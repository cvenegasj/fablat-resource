package lat.fab.app.resource.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "WorkshopTutor")
@Getter
@Setter
public class WorkshopTutor implements java.io.Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idWorkshopTutor", unique = true, nullable = false)
	private Integer id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idSubGroupMember", nullable = false)
	private SubGroupMember subGroupMember;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idWorkshop", nullable = false)
	private Workshop workshop;

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
		WorkshopTutor other = (WorkshopTutor) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
