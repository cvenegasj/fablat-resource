package lat.fab.app.resource.repository;

import lat.fab.app.resource.entities.EventType;
import lat.fab.app.resource.entities.WorkshopTutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkshopTutorDAO extends CrudRepository<WorkshopTutor, Integer> {

	Integer countBySubGroupMember_GroupMember_FabberEmail_AndWorkshopTypeIsIn(String email, List<EventType> types);

	WorkshopTutor findByWorkshopIdAndSubGroupMember_GroupMember_Fabber_Email(Integer idWorkshop, String email);

}
