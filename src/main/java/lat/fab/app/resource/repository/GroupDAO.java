package lat.fab.app.resource.repository;

import lat.fab.app.resource.entities.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupDAO extends CrudRepository<Group, Integer>, ListPagingAndSortingRepository<Group, Integer> {

	List<Group> findByNameIgnoreCase(String term);
	
	List<Group> findAllByOrderByCreationDateTimeAsc();

	List<Group> findAllByOrderByCreationDateTimeAsc(Pageable pageable);
	
//	Integer getMembersCount(Integer idGroup);
	
//	Integer getSubGroupsCount(Integer idGroup);
	
//	Boolean checkIfMember(Integer idGroup, String email);
	
//	Boolean checkIfCoordinator(Integer idGroup, String email);


	Page<Group> findByNameIgnoreCaseLikeOrderByNameAsc(String name, Pageable pageable);

	// with sorting

	Page<Group> findAllByOrderByNameDesc(Pageable pageable);

//	Page<Group> findAllByOrderByFabberInfoScoreGeneralDesc(Pageable pageable); // by group score

	@Query(value = "from Group g order by size(g.groupMembers) desc")
	Page<Group> findAllOrderByMembersCountDesc(Pageable pageable);

//	@Query(value = "from Fabber f order by size(f.groupMembers) desc")
//	Page<Group> findAllOrderByWorkshopsCount(Pageable pageable); // by workshops count
}
