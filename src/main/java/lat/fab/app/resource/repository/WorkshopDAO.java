package lat.fab.app.resource.repository;

import lat.fab.app.resource.entities.EventType;
import lat.fab.app.resource.entities.Workshop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkshopDAO extends CrudRepository<Workshop, Integer> {
	
	List<Workshop> findByStartDateTimeAfterOrderByStartDateTimeAsc(LocalDateTime date);

	Page<Workshop> findByStartDateTimeAfterOrderByStartDateTimeAsc(LocalDateTime date, Pageable pageable);

	Page<Workshop> findByStartDateTimeBeforeOrderByStartDateTimeDesc(LocalDateTime date, Pageable pageable);

	Page<Workshop> findByNameContainingAndLocationCountryIsInAndStartDateTimeAfterOrderByStartDateTimeAsc(
			String name, List<String> countriesKeys, LocalDateTime dateTime, Pageable pageable);

	Page<Workshop> findByNameContainingAndLocationCountryIsInAndStartDateTimeBeforeOrderByStartDateTimeDesc(
			String name, List<String> countriesKeys, LocalDateTime dateTime, Pageable pageable);

	Page<Workshop> findByNameContainingAndStartDateTimeAfterOrderByStartDateTimeAsc(
			String name, LocalDateTime dateTime, Pageable pageable);

	Page<Workshop> findByNameContainingAndStartDateTimeBeforeOrderByStartDateTimeDesc(
			String name, LocalDateTime dateTime, Pageable pageable);

	Page<Workshop> findByLocationCountryIsInAndStartDateTimeAfterOrderByStartDateTimeAsc(
			List<String> countriesKeys, LocalDateTime dateTime, Pageable pageable);

	Page<Workshop> findByLocationCountryIsInAndStartDateTimeBeforeOrderByStartDateTimeDesc(
			List<String> countriesKeys, LocalDateTime dateTime, Pageable pageable);
	
	List<Workshop> findBySubGroupId(Integer idSubGroup);

	Integer countDistinctBySubGroup_GroupId(Integer idGroup);

	Integer countDistinctBySubGroup_GroupId_AndTypeIsIn(Integer idGroup, List<EventType> types);
}
