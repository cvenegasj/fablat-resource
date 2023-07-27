package lat.fab.app.resource.repository;

import lat.fab.app.resource.entities.Workshop;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkshopDAO extends CrudRepository<Workshop, Integer> {
	
	List<Workshop> findByStartDateTimeAfter(LocalDateTime date);
	
	List<Workshop> findBySubGroupId(Integer idSubGroup);

}
