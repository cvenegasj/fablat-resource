package lat.fab.app.resource.repository;

import lat.fab.app.resource.entities.Fabber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FabberDAO extends ListCrudRepository<Fabber, Integer>,
		ListPagingAndSortingRepository<Fabber, Integer> {

	Optional<Fabber> findByEmail(String email);

	List<Fabber> findByEmailLike(String term);

	Page<Fabber> findByNameIgnoreCaseLikeOrderByNameAsc(String name, Pageable pageable);

	Page<Fabber> findByNameIgnoreCaseLikeAndCountryIsInOrderByNameAsc(
			String name, List<String> countriesKeys, Pageable pageable);

	Page<Fabber> findByCountryIsInOrderByNameAsc(List<String> countriesKeys, Pageable pageable);

	// with sorting

	Page<Fabber> findAllByOrderByNameDesc(Pageable pageable);

	Page<Fabber> findAllByOrderByFabberInfoScoreGeneralDesc(Pageable pageable);

	@Query(value = "from Fabber f order by size(f.groupMembers) desc")
	Page<Fabber> findAllOrderByGroupsCountDesc(Pageable pageable);

//	@Query(value = "from Fabber f order by size(f.groupMembers) desc")
//	Page<Fabber> findAllOrderByWorkshopsCount(Pageable pageable);
}
