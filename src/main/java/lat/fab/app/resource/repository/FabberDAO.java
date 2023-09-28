package lat.fab.app.resource.repository;

import lat.fab.app.resource.entities.Fabber;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FabberDAO extends ListCrudRepository<Fabber, Integer> {

	Optional<Fabber> findByEmail(String email);

	List<Fabber> findByEmailLike(String term);

//	Integer countAll();
	
}
