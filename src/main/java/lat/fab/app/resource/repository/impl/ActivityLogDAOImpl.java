package lat.fab.app.resource.repository.impl;

import lat.fab.app.resource.entities.ActivityLog;
import lat.fab.app.resource.repository.ActivityLogDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Repository
public class ActivityLogDAOImpl extends GenericDAOImpl<ActivityLog, Integer> {

	@Transactional
	public List<ActivityLog> findByVisibility() {
		List<ActivityLog> list = null;
		list = (List<ActivityLog>) entityManager
				.createQuery(
						"select x from " + getDomainClassName() + " x "
						+ "where x.visibility = 'EXTERNAL' "
						+ "order by x.id desc")
				.getResultList();
		return list;
	}

	@Transactional
	public List<ActivityLog> findAllByGroup(Integer idGroup) {
		List<ActivityLog> list = null;
		list = (List<ActivityLog>) entityManager
				.createQuery(
						"select x from " + getDomainClassName() + " x "
						+ "where (x.visibility = 'INTERNAL' or x.visibility = 'EXTERNAL') "
						+ "and x.level = 'GROUP' "
						+ "and x.group.id = :idGroup "
						+ "order by date(x.creationDateTime) desc")
				.setParameter("idGroup", idGroup)
				.getResultList();
		return list;
	}

	@Transactional
	public List<ActivityLog> findAllBySubGroup(Integer idSubGroup) {
		List<ActivityLog> list = null;
		list = (List<ActivityLog>) entityManager
				.createQuery(
						"select x from " + getDomainClassName() + " x "
						+ "where (x.visibility = 'INTERNAL' or x.visibility = 'EXTERNAL') "
						+ "and x.level = 'SUBGROUP' "
						+ "and x.subGroup.id = :idSubGroup "
						+ "order by date(x.creationDateTime) desc")
				.setParameter("idSubGroup", idSubGroup)
				.getResultList();
		return list;
	}

}
