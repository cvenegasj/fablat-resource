package lat.fab.app.resource.repository;

import lat.fab.app.resource.entities.FabberInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FabberInfoDAO extends CrudRepository<FabberInfo, Integer> {

}