package lat.fab.app.resource.controller;

import lat.fab.app.resource.dto.LabDTO;
import lat.fab.app.resource.dto.LabRequestWrapper;
import lat.fab.app.resource.entities.Lab;
import lat.fab.app.resource.entities.Location;
import lat.fab.app.resource.repository.LabDAO;
import lat.fab.app.resource.repository.LocationDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/labs")
@RequiredArgsConstructor
public class LabController {
	private final LabDAO labDAO;
	private final LocationDAO locationDAO;

	// General method for fetching data of all labs
	@RequestMapping(value = "/update-all", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public void updateAll(@RequestBody Map<String, List<LabRequestWrapper>> labs) {
		
		for (LabRequestWrapper item : labs.get("labs")) {
			Lab  lab = new Lab();
			lab.setId(item.getId());
			lab.setName(item.getName());
			lab.setDescription(item.getDescription());
			lab.setAvatar(item.getAvatar());
			lab.setPhone(item.getPhone());
			lab.setEmail(item.getEmail());
			lab.setUrl(item.getUrl());

			Location location = new Location();
			location.setAddress1(item.getAddress_1());
			location.setAddress2(item.getAddress_2());
			location.setCity(item.getCity());
			location.setCountry(item.getCountry_code());
			location.setLatitude(item.getLatitude() != null ? item.getLatitude().toString() : null);
			location.setLongitude(item.getLongitude() != null ? item.getLongitude().toString() : null);

			locationDAO.save(location);
			lab.setLocation(location);
			labDAO.save(lab);
		}
	}

	
	@RequestMapping(method = RequestMethod.GET)
	public List<LabDTO> findAll() {
		List<LabDTO> returnList = new ArrayList<>();

		for (Lab lab : labDAO.findAll()) {
			LabDTO lDTO = new LabDTO();
			lDTO.setIdLab(lab.getId());
			lDTO.setName(lab.getName());
			lDTO.setAddress1(lab.getLocation().getAddress1());
			lDTO.setCity(lab.getLocation().getCity());
			lDTO.setCountry(lab.getLocation().getCountry());
			
			returnList.add(lDTO);
		}
		
		return returnList;
	} 

	@RequestMapping(value = "/search/{searchText}", method = RequestMethod.GET)
	public List<LabDTO> findByTerm(@PathVariable("searchText") String searchText) {
		List<LabDTO> returnList = new ArrayList<>();
		for (Lab lab : labDAO.findByNameLike(searchText)) {
			LabDTO lDTO = new LabDTO();
			lDTO.setIdLab(lab.getId());
			lDTO.setName(lab.getName());
			lDTO.setAddress1(lab.getLocation().getAddress1());
			lDTO.setCity(lab.getLocation().getCity());
			lDTO.setCountry(lab.getLocation().getCountry());

			returnList.add(lDTO);
		}

		return returnList;
	}
	
}
