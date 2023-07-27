package lat.fab.app.resource.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "RoleFabber")
@Data
public class RoleFabber implements java.io.Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idRoleFabber", unique = true, nullable = false)
	private Integer id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idRole", nullable = false)
	private Role role;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idFabber", nullable = false)
	private Fabber fabber;
}
