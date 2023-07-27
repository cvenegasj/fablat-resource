package lat.fab.app.resource.repository;

import lat.fab.app.resource.entities.SubGroupMember;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubGroupMemberDAO extends CrudRepository<SubGroupMember, Integer> {
	
	Integer countByGroupMember_Fabber_EmailAndIsCoordinatorIs(String email, boolean isCoordinator);

//	Integer countAllByFabberAsCollaborator(String email);

	SubGroupMember findBySubGroupIdAndGroupMemberFabberEmail(Integer idSubGroup, String email);
	
	List<SubGroupMember> findBySubGroup_Id(Integer idSubGroup); // TODO
	
	List<SubGroupMember> findBySubGroup_Group_IdAndGroupMember_Fabber_EmailOrderByCreationDateTimeAsc(Integer idGroup, String email); // TODO
	
	List<SubGroupMember> findBySubGroup_Group_IdAndGroupMember_Fabber_IdOrderByCreationDateTimeAsc(Integer idGroup, Integer idFabber); // TODO

	Integer countDistinctBySubGroupId(Integer idSubGroup);
	
}
