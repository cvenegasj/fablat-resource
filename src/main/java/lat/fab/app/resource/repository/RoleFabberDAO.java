package lat.fab.app.resource.repository;

import lat.fab.app.resource.entities.RoleFabber;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleFabberDAO extends CrudRepository<RoleFabber, Integer> {

}
