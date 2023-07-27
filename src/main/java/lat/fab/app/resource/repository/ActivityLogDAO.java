package lat.fab.app.resource.repository;

import lat.fab.app.resource.entities.ActivityLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogDAO extends CrudRepository<ActivityLog, Integer> {

	// External group activities
	List<ActivityLog> findByVisibility(String visibility);

	// External and Internal group activity
	List<ActivityLog> findByLevelAndGroupIdOrderById(String level, Integer idGroup);

	// External and Internal subgroup activity
	List<ActivityLog> findByLevelAndSubGroupIdOrderById(String level, Integer idSubGroup);

}
