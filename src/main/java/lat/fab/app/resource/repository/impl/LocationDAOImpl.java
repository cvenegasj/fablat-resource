package lat.fab.app.resource.repository.impl;

import lat.fab.app.resource.entities.Location;
import lat.fab.app.resource.repository.LocationDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Repository
public class LocationDAOImpl extends GenericDAOImpl<Location, Integer> {

	@Transactional
	public List<Location> findByTerm(String term) {
		List<Location> list = null;
		list = (List<Location>) entityManager
				.createQuery("select x from " + getDomainClassName() + " x " 
						+ "left join x.lab l "
						+ "where lower(x.address1) like :term or "
						+ "lower(x.address2) like :term or "
						+ "lower(l.name) like :term")
				.setParameter("term", "%" + term.toLowerCase() + "%")
				.getResultList();
		
		return list;
	}

}
