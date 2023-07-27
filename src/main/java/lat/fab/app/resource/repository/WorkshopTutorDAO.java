package lat.fab.app.resource.repository;

import lat.fab.app.resource.entities.WorkshopTutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkshopTutorDAO extends CrudRepository<WorkshopTutor, Integer> {

	Integer countBySubGroupMember_GroupMember_FabberEmail(String email);

	WorkshopTutor findByWorkshopIdAndSubGroupMember_GroupMember_Fabber_Email(Integer idWorkshop, String email);

}
