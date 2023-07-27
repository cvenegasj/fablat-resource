package lat.fab.app.resource.repository;

import lat.fab.app.resource.entities.GroupMember;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMemberDAO extends CrudRepository<GroupMember, Integer> {

	Integer countByFabberEmailAndIsCoordinatorIs(String email, boolean isCoordinator);

//	Integer countAllByFabberAsCollaborator(String email);
	
	GroupMember findByGroupIdAndFabberEmail(Integer idGroup, String email);
	
	GroupMember findByGroupIdAndFabberId(Integer idGroup, Integer idFabber);
	
	List<GroupMember> findAllByGroupId(Integer idGroup);
	
	List<GroupMember> findAllByFabberEmail(String email);
	
	List<GroupMember> findAllByFabberId(Integer idFabber);

	Integer countDistinctByGroupId(Integer idGroup);

}
