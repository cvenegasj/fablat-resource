package lat.fab.app.resource.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "FabberInfo")
@Getter
@Setter
public class FabberInfo implements java.io.Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idFabberInfo", unique = true, nullable = false)
	private Integer id;

	@Column(name = "scoreGeneral", nullable = false)
	private Integer scoreGeneral;

	@Column(name = "scoreCoordinator", nullable = false)
	private Integer scoreCoordinator;

	@Column(name = "scoreCollaborator", nullable = false)
	private Integer scoreCollaborator;

	@Column(name = "scoreReplicator", nullable = false)
	private Integer scoreReplicator;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idFabber")
	private Fabber fabber;
}
