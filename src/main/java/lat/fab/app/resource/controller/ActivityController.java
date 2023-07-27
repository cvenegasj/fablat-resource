package lat.fab.app.resource.controller;

import lat.fab.app.resource.dto.ActivityLogDTO;
import lat.fab.app.resource.entities.ActivityLog;
import lat.fab.app.resource.repository.ActivityLogDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth/activities")
public class ActivityController {

	private final ActivityLogDAO activityLogDAO;

	@RequestMapping(value = "/find-all-external", method = RequestMethod.GET)
	public List<ActivityLogDTO> findAllExternal() {
		// Single list with all types of activities
		List<ActivityLogDTO> returnList = new ArrayList<ActivityLogDTO>();
		
		for (ActivityLog a : activityLogDAO.findByVisibility("EXTERNAL")) {
			ActivityLogDTO aDTO = convertToDTO(a);
			returnList.add(aDTO);
		}
		return returnList;
	}
	
	@RequestMapping(value = "/group/{idGroup}", method = RequestMethod.GET)
	public List<ActivityLogDTO> findAllFromGroup(@PathVariable("idGroup") Integer idGroup) {
		List<ActivityLogDTO> returnList = new ArrayList<>();
		
		for (ActivityLog a : activityLogDAO.findByLevelAndGroupIdOrderById("GROUP", idGroup)) {
			ActivityLogDTO aDTO = convertToDTO(a);
			returnList.add(aDTO);
		}
		return returnList;
	}
	
	@RequestMapping(value = "/subgroup/{idSubGroup}", method = RequestMethod.GET)
	public List<ActivityLogDTO> findAllFromSubGroup(@PathVariable("idSubGroup") Integer idSubGroup) {
		List<ActivityLogDTO> returnList = new ArrayList<>();
		
		for (ActivityLog a : activityLogDAO.findByLevelAndSubGroupIdOrderById("SUBGROUP", idSubGroup)) {
			ActivityLogDTO aDTO = convertToDTO(a);
			returnList.add(aDTO);
		}
		return returnList;
	}
	
	private ActivityLogDTO convertToDTO(ActivityLog a) {
		ActivityLogDTO aDTO = new ActivityLogDTO();
		aDTO.setIdActivityLog(a.getId());
		aDTO.setLevel(a.getLevel());
		aDTO.setType(a.getType());
		aDTO.setVisibility(a.getVisibility());
		
		aDTO.setCreationDateTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(a.getCreationDateTime()));
		aDTO.setCreationDateTimeRaw(a.getCreationDateTime());
		aDTO.setGroupId(a.getGroup() != null ? a.getGroup().getId() : null);
		aDTO.setGroupName(a.getGroup() != null ? a.getGroup().getName() : null);
		aDTO.setSubGroupId(a.getSubGroup() != null ? a.getSubGroup().getId() : null);
		aDTO.setSubGroupName(a.getSubGroup() != null ? a.getSubGroup().getName() : null);
		aDTO.setFabberId(a.getFabber().getId());
		aDTO.setFabberFirstName(a.getFabber().getFirstName());
		aDTO.setFabberLastName(a.getFabber().getLastName());
		return aDTO;
	}
	
}
