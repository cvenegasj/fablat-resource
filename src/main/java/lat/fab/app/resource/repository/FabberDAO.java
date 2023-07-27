package lat.fab.app.resource.repository;

import lat.fab.app.resource.entities.Fabber;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FabberDAO extends CrudRepository<Fabber, Integer> {

	Fabber findByEmail(String email);

	List<Fabber> findByEmailLike(String term);

//	Integer countAll();
	
}
