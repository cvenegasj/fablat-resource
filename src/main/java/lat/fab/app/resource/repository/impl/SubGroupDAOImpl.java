package lat.fab.app.resource.repository.impl;

import lat.fab.app.resource.entities.SubGroup;
import lat.fab.app.resource.repository.SubGroupDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Repository
public class SubGroupDAOImpl extends GenericDAOImpl<SubGroup, Integer> {

	@Transactional
	public List<SubGroup> findAllOrderedAsc() {
		List<SubGroup> list = null;
		
		list = (List<SubGroup>) entityManager
				.createQuery("from " + getDomainClassName() + " x " 
						+ "order by x.name asc")
				.getResultList();

		return list;
	}

	@Transactional
	public List<SubGroup> findAllByGroup(Integer idGroup) {
		List<SubGroup> list = null;
		
		list = (List<SubGroup>) entityManager
				.createQuery("from " + getDomainClassName() + " x "
						+ "where x.group.id = :idGroup "
						+ "order by x.name asc")
				.setParameter("idGroup", idGroup)
				.getResultList();

		return list;
	}

	@Transactional
	public Integer getMembersCount(Integer idSubGroup) {
		Long count = entityManager
				.createQuery(
						"select count(distinct x) from SubGroupMember x "
								+ "where x.subGroup.id = :idSubGroup", Long.class)
				.setParameter("idSubGroup", idSubGroup)
				.getSingleResult();
		
		return count.intValue();
	}

	@Transactional
	public Integer getWorkshopsCount(Integer idSubGroup) {
		Long count = entityManager
				.createQuery(
						"select count(distinct x) from Workshop x "
								+ "where x.subGroup.id = :idSubGroup", Long.class)
				.setParameter("idSubGroup", idSubGroup)
				.getSingleResult();
		
		return count.intValue();
	}
	
}
