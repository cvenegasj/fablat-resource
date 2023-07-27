package lat.fab.app.resource.repository.impl;

import lat.fab.app.resource.entities.Role;
import lat.fab.app.resource.repository.RoleDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

//@Repository
public class RoleDAOImpl extends GenericDAOImpl<Role, Integer> {

	@Transactional
	public Role findByName(String name) {
		Role role = null;
		role = (Role) entityManager.createQuery("from " + getDomainClassName() + " x where x.name = :name")
				.setParameter("name", name)
				.setMaxResults(1)
				.getResultList();

		return role;
	}

}
