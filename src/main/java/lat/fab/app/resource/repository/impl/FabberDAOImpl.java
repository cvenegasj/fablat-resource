package lat.fab.app.resource.repository.impl;

import lat.fab.app.resource.entities.Fabber;
import lat.fab.app.resource.repository.FabberDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Repository
//@Transactional
public class FabberDAOImpl extends GenericDAOImpl<Fabber, Integer> {

/*//	@Transactional
	public Fabber findByEmail(String email) {
		Fabber fabber = null;
		fabber = (Fabber) entityManager.createQuery("from " + getDomainClassName() + " f where f.email = :email")
				.setParameter("email", email).setMaxResults(1).getSingleResult();
		return fabber;
	}

//	@Transactional
	public List<Fabber> findByTerm(String term) {
		List<Fabber> list = null;
		list = (List<Fabber>) entityManager
				.createQuery(
						"select x from " + getDomainClassName() + " x "
								+ "where lower(concat(x.firstName, ' ', x.lastName)) like :term "
								+ "or x.email like :term ")
				.setParameter("term", "%" + term.toLowerCase() + "%")
				.getResultList();
		
		return list;
	}

//	@Transactional
	public Integer countAll() {
		Integer count = null;
		count = (Integer) entityManager
				.createQuery("select count(x) from " + getDomainClassName() + " x ")
				.getSingleResult();
		return count;
	}*/
	
}
