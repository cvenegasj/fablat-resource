package lat.fab.app.resource.repository.impl;

import lat.fab.app.resource.entities.Workshop;
import lat.fab.app.resource.repository.WorkshopDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

//@Repository
public class WorkshopDAOImpl extends GenericDAOImpl<Workshop, Integer> {

	/*@Transactional
	public List<Workshop> findAllAfterDate(LocalDateTime date) {
		List<Workshop> list = null;
		list = (List<Workshop>) entityManager
				.createQuery(
						"select x from " + getDomainClassName() + " x "
								+ "where x.startDateTime > :date "
								+ "order by date(x.startDateTime) asc")
				.setParameter("date", date)
				.getResultList();
		
		return list;
	}

	@Transactional
	public List<Workshop> findAllBySubGroup(Integer idSubGroup) {
		List<Workshop> list = null;
		list = (List<Workshop>) entityManager
				.createQuery(
						"select x from " + getDomainClassName() + " x "
								+ "where x.subGroup.id = :idSubGroup ")
				.setParameter("idSubGroup", idSubGroup)
				.getResultList();
		
		return list;
	}*/

}
