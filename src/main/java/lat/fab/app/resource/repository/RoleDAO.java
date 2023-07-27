package lat.fab.app.resource.repository;

import lat.fab.app.resource.entities.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDAO extends CrudRepository<Role, Integer> {

	Role findByName(String name);
}
