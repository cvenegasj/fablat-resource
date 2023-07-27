package lat.fab.app.resource.repository.impl;

import lat.fab.app.resource.entities.GroupMember;
import lat.fab.app.resource.repository.GroupMemberDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Repository
public class GroupMemberDAOImpl extends GenericDAOImpl<GroupMember, Integer> {

	/*@Transactional
	public Integer countAllByFabberEmailAndIsCoordinatorIs(String email) {
		Long count = entityManager
				.createQuery(
						"select count(x) from " + getDomainClassName() + " x "
								+ "where x.isCoordinator = 1 and x.fabber.email = :email", Long.class)
				.setParameter("email", email)
				.getSingleResult();
		
		return count.intValue();
	}

	@Transactional
	public Integer countAllByFabberAsCollaborator(String email) {
		Long count = entityManager
				.createQuery(
						"select count(x) from " + getDomainClassName() + " x "
								+ "where x.isCoordinator = 0 and x.fabber.email = :email", Long.class)
				.setParameter("email", email)
				.getSingleResult();
		
		return count.intValue();
	}

	@Transactional
	public GroupMember findByGroupIdAndFabberEmail(Integer idGroup, String email) {
		GroupMember e = null;
		e = (GroupMember) entityManager
				.createQuery(
						"select x from " + getDomainClassName() + " x "
								+ "where x.group.id = :idGroup and x.fabber.email = :email")
				.setParameter("idGroup", idGroup)
				.setParameter("email", email)
				.setMaxResults(1)
				.getSingleResult();

		return e;
	}
	
	@Transactional
	public GroupMember findByGroupIdAndFabberId(Integer idGroup, Integer idFabber) {
		GroupMember e = null;
		e = (GroupMember) entityManager
				.createQuery(
						"select x from " + getDomainClassName() + " x "
								+ "where x.group.id = :idGroup and x.fabber.id = :idFabber")
				.setParameter("idGroup", idGroup)
				.setParameter("idFabber", idFabber)
				.setMaxResults(1)
				.getSingleResult();

		return e;
	}

	@Transactional
	public List<GroupMember> findAllByGroupId(Integer idGroup) {
		List<GroupMember> list = null;
		list = (List<GroupMember>) entityManager
				.createQuery(
						"select x from " + getDomainClassName() + " x "
								+ "where x.group.id = :idGroup "
								+ "order by case when x.isCoordinator = 1 then 0 else 1 end, "
								+ "date(x.creationDateTime) asc")
				.setParameter("idGroup", idGroup)
				.getResultList();

		return list;
	}

	@Transactional
	public List<GroupMember> findAllByFabberEmail(String email) {
		List<GroupMember> list = null;
		list = (List<GroupMember>) entityManager
				.createQuery(
						"select x from " + getDomainClassName() + " x "
								+ "where x.fabber.email = :email ")
				.setParameter("email", email)
				.getResultList();

		return list;
	}

	@Transactional
	public List<GroupMember> findAllByFabberId(Integer idFabber) {
		List<GroupMember> list = null;
		list = (List<GroupMember>) entityManager
				.createQuery(
						"select x from " + getDomainClassName() + " x "
								+ "where x.fabber.id = :idFabber ")
				.setParameter("idFabber", idFabber)
				.getResultList();

		return list;
	}*/

}
