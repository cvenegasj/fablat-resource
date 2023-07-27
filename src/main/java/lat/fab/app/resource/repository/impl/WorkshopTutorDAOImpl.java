package lat.fab.app.resource.repository.impl;

import lat.fab.app.resource.entities.WorkshopTutor;
import lat.fab.app.resource.repository.WorkshopTutorDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

//@Repository
public class WorkshopTutorDAOImpl extends GenericDAOImpl<WorkshopTutor, Integer> {

	/*@Transactional
	public Integer countByFabberEmail(String email) {
		Long count = entityManager
				.createQuery(
						"select count(x) from " + getDomainClassName() + " x "
								+ "where x.subGroupMember.groupMember.fabber.email = :email", Long.class)
				.setParameter("email", email)
				.getSingleResult();
		
		return count.intValue();
	}

	@Transactional
	public WorkshopTutor findByWorkshopIdAndSubGroupMember_GroupMember_Fabber_Email(Integer idWorkshop, String email) {
		WorkshopTutor e = null;
		e = (WorkshopTutor) entityManager
				.createQuery(
						"from " + getDomainClassName() + " x where x.workshop.id = :idWorkshop "
								+ "and x.subGroupMember.groupMember.fabber.email = :email")
				.setParameter("idWorkshop", idWorkshop)
				.setParameter("email", email).setMaxResults(1).getSingleResult();

		return e;
	}
*/
}
