package lat.fab.app.resource.repository.impl;

import lat.fab.app.resource.entities.SubGroupMember;
import lat.fab.app.resource.repository.SubGroupMemberDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Repository
public class SubGroupMemberDAOImpl extends GenericDAOImpl<SubGroupMember, Integer> {

	/*@Transactional
	public Integer countByFabberEmailAndIsCoordinatorIs(String email, boolean isCoordinator) {
		Long count = entityManager
				.createQuery(
						"select count(x) from " + getDomainClassName() + " x "
								+ "where x.isCoordinator = 1 and x.groupMember.fabber.email = :email", Long.class)
				.setParameter("email", email)
				.getSingleResult();
		
		return count.intValue();
	}

	@Transactional
	public Integer countAllByFabberAsCollaborator(String email) {
		Long count = entityManager
				.createQuery(
						"select count(x) from " + getDomainClassName() + " x "
								+ "where x.isCoordinator = 0 and x.groupMember.fabber.email = :email", Long.class)
				.setParameter("email", email)
				.getSingleResult();
		
		return count.intValue();
	}
	
	@Transactional
	public SubGroupMember findBySubGroupIdAndFabberEmail(Integer idSubGroup, String email) {
		SubGroupMember e = null;
		e = (SubGroupMember) entityManager
				.createQuery(
						"from " + getDomainClassName() + " x where x.subGroup.id = :idSubGroup "
								+ "and x.groupMember.fabber.email = :email")
				.setParameter("idSubGroup", idSubGroup)
				.setParameter("email", email).setMaxResults(1)
				.getSingleResult();

		return e;
	}

	@Transactional
	public List<SubGroupMember> findBySubGroupId(Integer idSubGroup) {
		List<SubGroupMember> list = null;
		list = (List<SubGroupMember>) entityManager
				.createQuery(
						"select x from " + getDomainClassName() + " x "
								+ "where x.subGroup.id = :idSubGroup "
								+ "order by case when x.isCoordinator = 1 then 0 else 1 end, "
								+ "date(x.creationDateTime) asc")
				.setParameter("idSubGroup", idSubGroup)
				.getSingleResult();

		return list;
	}

	@Transactional
	public List<SubGroupMember> findByGroupIdAndFabberEmail(Integer idGroup, String email) {
		List<SubGroupMember> list = null;
		list = (List<SubGroupMember>) entityManager
				.createQuery(
						"select x from " + getDomainClassName() + " x "
								+ "where x.subGroup.group.id = :idGroup "
								+ "and x.groupMember.fabber.email = :email "
								+ "order by date(x.creationDateTime) asc")
				.setParameter("idGroup", idGroup)
				.setParameter("email", email)
				.getSingleResult();

		return list;
	}

	@Transactional
	public List<SubGroupMember> findAllByGroupAndFabber(Integer idGroup, Integer idFabber) {
		List<SubGroupMember> list = null;
		list = (List<SubGroupMember>) entityManager
				.createQuery(
						"select x from " + getDomainClassName() + " x "
								+ "where x.subGroup.group.id = :idGroup "
								+ "and x.groupMember.fabber.id = :idFabber "
								+ "order by date(x.creationDateTime) asc")
				.setParameter("idGroup", idGroup)
				.setParameter("idFabber", idFabber)
				.getResultList();

		return list;
	}
	*/
}
