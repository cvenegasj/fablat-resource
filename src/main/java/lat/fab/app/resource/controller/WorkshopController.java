package lat.fab.app.resource.controller;

import lat.fab.app.resource.dto.WorkshopDTO;
import lat.fab.app.resource.dto.WorkshopTutorDTO;
import lat.fab.app.resource.entities.Location;
import lat.fab.app.resource.entities.SubGroup;
import lat.fab.app.resource.entities.Workshop;
import lat.fab.app.resource.entities.WorkshopTutor;
import lat.fab.app.resource.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/auth/workshops")
public class WorkshopController {
	private final DateTimeFormatter dateTimeFormatterIn = DateTimeFormatter.ofPattern("d-M-yyyy h:m a"); // for creation/update
	private final DateTimeFormatter dateTimeFormatterCalendar = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"); // for google calendar
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
	private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM");
	private final DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern("MMM d(EEE) h:mm a");
	
	@Autowired
	private SubGroupDAO subGroupDAO;
	@Autowired
	private SubGroupMemberDAO subGroupMemberDAO;
	@Autowired
	private WorkshopDAO workshopDAO;
	@Autowired
	private WorkshopTutorDAO workshopTutorDAO;
	@Autowired
	private LocationDAO locationDAO;
	
	@RequestMapping(value = "/upcoming", method = RequestMethod.GET)
    public List<WorkshopDTO> findUpcoming() {
		List<WorkshopDTO> returnList = new ArrayList<>();
		
        // find all workshops after today
		for (Workshop w : workshopDAO.findByStartDateTimeAfter(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC))) {
			WorkshopDTO wDTO = convertToDTO(w);
			// workshop's tutors
			List<WorkshopTutorDTO> tutors = new ArrayList<>();
			for (WorkshopTutor wt : w.getWorkshopTutors()) {
				WorkshopTutorDTO wtDTO = convertToDTO(wt);
				tutors.add(wtDTO);
			}
			wDTO.setTutors(tutors);
			returnList.add(wDTO);
		}
		
		return returnList;
	}
	
	@RequestMapping(value = "/{idWorkshop}/{email}", method = RequestMethod.GET)
    public WorkshopDTO findOne(@PathVariable Integer idWorkshop, @PathVariable String email) {
		Workshop workshop = workshopDAO.findById(idWorkshop).get();
		WorkshopDTO wDTO = convertToDTO(workshop);
		
		// additional properties
		WorkshopTutor userAsWorkshopTutor = workshopTutorDAO.findByWorkshopIdAndSubGroupMember_GroupMember_Fabber_Email(idWorkshop, email);
		if (userAsWorkshopTutor != null) {
			wDTO.setAmITutor(true);
		}
		
		// workshop's tutors
		List<WorkshopTutorDTO> tutors = new ArrayList<WorkshopTutorDTO>();
		for (WorkshopTutor wt : workshop.getWorkshopTutors()) {
			WorkshopTutorDTO wtDTO = convertToDTO(wt);
			tutors.add(wtDTO);
		}
		wDTO.setTutors(tutors);
		
		return wDTO;
	}
	
	@RequestMapping(value = "/{email}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public WorkshopDTO create(@PathVariable String email, @RequestBody WorkshopDTO workshopDTO) throws ParseException {
		Workshop workshop = convertToEntity(workshopDTO);
		workshop.setEnabled(true);
		// set creation datetime 
        Instant now = Instant.now();
        workshop.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));
        
        // Parent SubGroup
        workshop.setSubGroup(subGroupDAO.findById(workshopDTO.getSubGroupId()).get());
        // Creator
        WorkshopTutor wt = new WorkshopTutor();
        wt.setSubGroupMember(subGroupMemberDAO.findBySubGroupIdAndGroupMemberFabberEmail(workshopDTO.getSubGroupId(), email));
        wt.setWorkshop(workshop);
        workshop.getWorkshopTutors().add(wt);
        
        // Location
        if (workshopDTO.getLocationId() == null) {
        	// create new Location
        	Location location = new Location();
        	location.setAddress1(workshopDTO.getLocationAddress());
        	location.setCity(workshopDTO.getLocationCity());
        	location.setCountry(workshopDTO.getLocationCountry());
        	location.setLatitude(workshopDTO.getLocationLatitude());
        	location.setLongitude(workshopDTO.getLocationLongitude());
        	locationDAO.save(location);
        	workshop.setLocation(location);
        } else {
        	workshop.setLocation(locationDAO.findById(workshopDTO.getLocationId()).get());
        }
        
        // replication number: inserting and updating
        workshop.setReplicationNumber(workshop.getSubGroup().getWorkshops().size() + 1);
        Workshop workshopCreated = workshopDAO.save(workshop);
        // then updating the replication number of the workshops
        updateReplicationNumbers(workshopDTO.getSubGroupId());
		
		return convertToDTO(workshopCreated);
	}
	
	private void updateReplicationNumbers(Integer idSubGroup) {
		int i = 1;
		for (Workshop w : subGroupDAO.findById(idSubGroup).get().getWorkshops()) {
        	w.setReplicationNumber(i);
        	workshopDAO.save(w);
        	i++;
        }
	}
	
	@RequestMapping(value = "/{idWorkshop}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("idWorkshop") Integer idWorkshop, @RequestBody WorkshopDTO workshopDTO) throws ParseException {
		Workshop workshop = workshopDAO.findById(idWorkshop).get();
		workshop.setName(workshopDTO.getName());
		workshop.setDescription(workshopDTO.getDescription());
		workshop.setStartDateTime(LocalDateTime.parse(workshopDTO.getStartDate() + " " + workshopDTO.getStartTime(), dateTimeFormatterIn));
		workshop.setEndDateTime(LocalDateTime.parse(workshopDTO.getEndDate() + " " + workshopDTO.getEndTime(), dateTimeFormatterIn));
		workshop.setIsPaid(workshopDTO.getIsPaid());
		workshop.setPrice(workshopDTO.getPrice());
		workshop.setCurrency(workshopDTO.getCurrency());
		workshop.setFacebookUrl(workshopDTO.getFacebookUrl());
		workshop.setTicketsUrl(workshopDTO.getTicketsUrl());
		if (workshopDTO.getLocationId() != null) {
			workshop.setLocation(locationDAO.findById(workshopDTO.getLocationId()).get());
		} else if (workshopDTO.getLocationAddress() != null && workshopDTO.getLocationCity() != null 
				&& workshopDTO.getLocationCountry() != null) {
			// create new location
			Location location = new Location();
			location.setAddress1(workshopDTO.getLocationAddress());
			location.setCity(workshopDTO.getLocationCity());
			location.setCountry(workshopDTO.getLocationCountry());
			location.setLatitude(workshopDTO.getLocationLatitude() != null ? workshopDTO.getLocationLatitude() : null);
			location.setLongitude(workshopDTO.getLocationLongitude() != null ? workshopDTO.getLocationLongitude() : null);
			locationDAO.save(location);
			workshop.setLocation(location);
		}
		
		workshopDAO.save(workshop);
		updateReplicationNumbers(workshop.getSubGroup().getId());
	}
	
	@RequestMapping(value = "/{idWorkshop}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable("idWorkshop") Integer idWorkshop) {
		Workshop workshop = workshopDAO.findById(idWorkshop).get();
		SubGroup subGroup = workshop.getSubGroup();
		workshopDAO.delete(workshop);
		// update replication numbers
		updateReplicationNumbers(subGroup.getId());
	}
	
	
	// ========== DTO conversion ==========
	
	private WorkshopDTO convertToDTO(Workshop workshop) {
		WorkshopDTO wDTO = new WorkshopDTO();
		wDTO.setIdWorkshop(workshop.getId());
		wDTO.setReplicationNumber(workshop.getReplicationNumber());
		wDTO.setName(workshop.getName());
		wDTO.setDescription(workshop.getDescription());
		wDTO.setStartDate(dateFormatter.format(workshop.getStartDateTime()));
		wDTO.setStartTime(timeFormatter.format(workshop.getStartDateTime()));
		wDTO.setEndDate(dateFormatter.format(workshop.getEndDateTime()));
		wDTO.setEndTime(timeFormatter.format(workshop.getEndDateTime()));
		
		wDTO.setStartDateDay(workshop.getStartDateTime().getDayOfMonth());
		wDTO.setStartDateMonth(monthFormatter.format(workshop.getStartDateTime()));
		wDTO.setStartDateFormatted(dateFormatter2.format(workshop.getStartDateTime()));
		wDTO.setEndDateFormatted(dateFormatter2.format(workshop.getEndDateTime()));
		
		wDTO.setStartDateTimeISO(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(workshop.getStartDateTime()));
		wDTO.setEndDateTimeISO(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(workshop.getEndDateTime()));
		
		wDTO.setStartDateTimeCalendar(dateTimeFormatterCalendar.format(workshop.getStartDateTime()));
		wDTO.setEndDateTimeCalendar(dateTimeFormatterCalendar.format(workshop.getEndDateTime()));
		
		wDTO.setIsPaid(workshop.getIsPaid());
		wDTO.setPrice(workshop.getPrice());
		wDTO.setCurrency(workshop.getCurrency());
		wDTO.setFacebookUrl(workshop.getFacebookUrl());
		wDTO.setTicketsUrl(workshop.getTicketsUrl());
		wDTO.setCreationDateTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(workshop.getCreationDateTime()));
		wDTO.setLocationId(workshop.getLocation().getId());
		wDTO.setLocationAddress(workshop.getLocation().getAddress1());
		wDTO.setLocationCity(workshop.getLocation().getCity());
		wDTO.setLocationCountry(workshop.getLocation().getCountry());
		wDTO.setLocationLatitude(workshop.getLocation().getLatitude());
		wDTO.setLocationLongitude(workshop.getLocation().getLongitude());
		wDTO.setLabName(workshop.getLocation().getLab() != null ? workshop.getLocation().getLab().getName() : null);
		wDTO.setSubGroupId(workshop.getSubGroup().getId());
		wDTO.setSubGroupName(workshop.getSubGroup().getName());
		wDTO.setGroupId(workshop.getSubGroup().getGroup().getId());
		wDTO.setGroupName(workshop.getSubGroup().getGroup().getName());
		
		return wDTO;
	}
	
	private Workshop convertToEntity(WorkshopDTO wDTO) throws ParseException {
		Workshop w = new Workshop();
		w.setName(wDTO.getName());
		w.setDescription(wDTO.getDescription());
		// format the date and time strings
		w.setStartDateTime(LocalDateTime.parse(wDTO.getStartDate() + " " + wDTO.getStartTime(), dateTimeFormatterIn));
		w.setEndDateTime(LocalDateTime.parse(wDTO.getEndDate() + " " + wDTO.getEndTime(), dateTimeFormatterIn));
		w.setIsPaid(wDTO.getIsPaid());
		w.setPrice(wDTO.getPrice());
		w.setCurrency(wDTO.getCurrency());
		w.setFacebookUrl(wDTO.getFacebookUrl());
		w.setTicketsUrl(wDTO.getTicketsUrl());
			
		return w;
	}
	
	private WorkshopTutorDTO convertToDTO(WorkshopTutor wt) {
		WorkshopTutorDTO wtDTO = new WorkshopTutorDTO();
		wtDTO.setIdWorkshopTutor(wt.getId());
		wtDTO.setName(wt.getSubGroupMember().getGroupMember().getFabber().getFirstName() == null
				|| wt.getSubGroupMember().getGroupMember().getFabber().getLastName() == null
				? wt.getSubGroupMember().getGroupMember().getFabber().getName()
				: wt.getSubGroupMember().getGroupMember().getFabber().getFirstName()
				+ " " + wt.getSubGroupMember().getGroupMember().getFabber().getLastName());
		wtDTO.setFirstName(wt.getSubGroupMember().getGroupMember().getFabber().getFirstName());
		wtDTO.setLastName(wt.getSubGroupMember().getGroupMember().getFabber().getLastName());
		wtDTO.setEmail(wt.getSubGroupMember().getGroupMember().getFabber().getEmail());
		wtDTO.setFabberId(wt.getSubGroupMember().getGroupMember().getFabber().getId());
		
		return wtDTO;
	}

}
