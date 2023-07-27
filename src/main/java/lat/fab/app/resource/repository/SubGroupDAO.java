package lat.fab.app.resource.repository;

import lat.fab.app.resource.entities.SubGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubGroupDAO extends CrudRepository<SubGroup, Integer> {

//	List<SubGroup> findAllOrderedAsc();
	
	List<SubGroup> findAllByGroupId(Integer idGroup);
	
//	Integer getMembersCount(Integer idSubGroup);

//	Integer getWorkshopsCount(Integer idSubGroup);

	Integer countDistinctByGroupId(Integer idGroup);

}
