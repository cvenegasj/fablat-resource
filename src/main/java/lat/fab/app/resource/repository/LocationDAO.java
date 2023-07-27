package lat.fab.app.resource.repository;

import lat.fab.app.resource.entities.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationDAO extends CrudRepository<Location, Integer> {

	List<Location> findByAddress1LikeOrAddress2Like(String term1, String term2); // TODO

}
