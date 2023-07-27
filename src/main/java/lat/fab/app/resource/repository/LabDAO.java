package lat.fab.app.resource.repository;

import lat.fab.app.resource.entities.Lab;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabDAO extends CrudRepository<Lab, Integer> {

	List<Lab> findByNameLike(String term);
}
