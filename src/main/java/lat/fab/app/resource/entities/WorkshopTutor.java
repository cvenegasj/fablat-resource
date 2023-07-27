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

}
