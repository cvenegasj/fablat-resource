package lat.fab.app.resource.repository.impl;

import lat.fab.app.resource.entities.Lab;
import lat.fab.app.resource.repository.LabDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Repository
public class LabDAOImpl extends GenericDAOImpl<Lab, Integer> {

	/*@Transactional
	public List<Lab> findByTerm(String term) {
		List<Lab> list = null;
		list = (List<Lab>) entityManager
				.createQuery("select x from " + getDomainClassName() + " x " + "where lower(x.name) like :term")
				.setParameter("term", "%" + term.toLowerCase() + "%")
				.getResultList();

		return list;
	}*/
	
}
