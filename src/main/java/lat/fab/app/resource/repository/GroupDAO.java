package lat.fab.app.resource.repository;

import lat.fab.app.resource.entities.Group;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupDAO extends CrudRepository<Group, Integer> {

	List<Group> findByNameIgnoreCase(String term);
	
	List<Group> findAllByOrderByCreationDateTimeAsc();
	
//	Integer getMembersCount(Integer idGroup);
	
//	Integer getSubGroupsCount(Integer idGroup);
	
//	Boolean checkIfMember(Integer idGroup, String email);
	
//	Boolean checkIfCoordinator(Integer idGroup, String email);

}
