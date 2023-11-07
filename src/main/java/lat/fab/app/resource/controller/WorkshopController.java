package lat.fab.app.resource.controller;

import lat.fab.app.resource.dto.WorkshopDTO;
import lat.fab.app.resource.dto.WorkshopTutorDTO;
import lat.fab.app.resource.entities.Location;
import lat.fab.app.resource.entities.Workshop;
import lat.fab.app.resource.entities.WorkshopTutor;
import lat.fab.app.resource.repository.*;
import lat.fab.app.resource.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth/workshops")
@RequiredArgsConstructor
public class WorkshopController {
	private final DateTimeFormatter dateTimeFormatterIn = DateTimeFormatter.ofPattern("d-M-yyyy h:m a"); // for creation/update
	private final DateTimeFormatter dateTimeFormatterCalendar = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"); // for google calendar
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
	private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM");
	private final DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern("MMM d(EEE) h:mm a");

	private final SubGroupDAO subGroupDAO;
	private final SubGroupMemberDAO subGroupMemberDAO;
	private final WorkshopDAO workshopDAO;
	private final WorkshopTutorDAO workshopTutorDAO;
	private final LocationDAO locationDAO;
	
	@GetMapping("/upcoming")
    public List<WorkshopDTO> findUpcoming() {
		List<WorkshopDTO> returnList = new ArrayList<>();
		
        // find all workshops after today
		for (Workshop w : workshopDAO.findByStartDateTimeAfterOrderByStartDateTimeAsc(LocalDateTime.now())) {
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
	
	@GetMapping("/{idWorkshop}/{email}")
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
	
	@PostMapping("/{email}")
    @ResponseStatus(HttpStatus.CREATED)
    public WorkshopDTO create(@PathVariable String email, @RequestBody WorkshopDTO workshopDTO) {
		Workshop workshop = convertToEntity(workshopDTO);
		workshop.setEnabled(true);
		// set creation datetime 
        Instant now = Instant.now();
        workshop.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));
        
        // Parent SubGroup
        workshop.setSubGroup(subGroupDAO.findById(workshopDTO.getSubGroupId()).get());
        // Creator
        WorkshopTutor wt = new WorkshopTutor();
        wt.setSubGroupMember(subGroupMemberDAO.findBySubGroupIdAndGroupMemberFabberEmail(
				workshopDTO.getSubGroupId(), email).get());
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

        Workshop workshopCreated = workshopDAO.save(workshop);
		return convertToDTO(workshopCreated);
	}
	
	@PutMapping("/{idWorkshop}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("idWorkshop") Integer idWorkshop, @RequestBody WorkshopDTO workshopDTO) {
		Workshop workshop = workshopDAO.findById(idWorkshop).get();
		workshop.setType(workshopDTO.getType());
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
	}
	
	@DeleteMapping("/{idWorkshop}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable("idWorkshop") Integer idWorkshop) {
		Workshop workshop = workshopDAO.findById(idWorkshop).get();
		workshopDAO.delete(workshop);
	}

	@GetMapping(value = "/{idWorkshop}")
	@ResponseStatus(HttpStatus.OK)
	public WorkshopDTO findOneLanding(@PathVariable("idWorkshop") Integer idWorkshop) {
		return workshopDAO.findById(idWorkshop)
				.map(workshop -> {
					WorkshopDTO dto = convertToDTO(workshop);
					dto.setTutors(workshop.getWorkshopTutors().stream()
							.map(this::convertToDTO)
							.toList());
					return dto;
				})
				.orElse(null);
	}

	@GetMapping("/filter")
	@ResponseStatus(HttpStatus.OK)
	public Page<WorkshopDTO> findAllLandingWithFilter(
			@RequestParam Integer page,
			@RequestParam Integer size,
			@RequestParam boolean past,
			@RequestParam Optional<String> name, // workshop name
			@RequestParam Optional<List<String>> countries) { // comma-separated list of countries keys

		PageRequest pagination = PageRequest.of(page, size);

		Page<Workshop> workshops = null;

		if (name.isPresent() && countries.isPresent()) {
			workshops = past
					? workshopDAO.findByNameContainingAndLocationCountryIsInAndStartDateTimeBeforeOrderByStartDateTimeDesc(
							name.get(), countries.get(), LocalDateTime.now(ZoneId.of(Constants.LIMA_ZONE_ID)), pagination)
					: workshopDAO.findByNameContainingAndLocationCountryIsInAndStartDateTimeAfterOrderByStartDateTimeAsc(
							name.get(), countries.get(), LocalDateTime.now(ZoneId.of(Constants.LIMA_ZONE_ID)), pagination);
		} else if (name.isPresent()) {
			workshops = past
					? workshopDAO.findByNameContainingAndStartDateTimeBeforeOrderByStartDateTimeDesc(
							name.get(), LocalDateTime.now(ZoneId.of(Constants.LIMA_ZONE_ID)), pagination)
					: workshopDAO.findByNameContainingAndStartDateTimeAfterOrderByStartDateTimeAsc(
							name.get(), LocalDateTime.now(ZoneId.of(Constants.LIMA_ZONE_ID)), pagination);
		} else if (countries.isPresent()) {
			workshops = past
					? workshopDAO.findByLocationCountryIsInAndStartDateTimeBeforeOrderByStartDateTimeDesc(
							countries.get(), LocalDateTime.now(ZoneId.of(Constants.LIMA_ZONE_ID)), pagination)
					: workshopDAO.findByLocationCountryIsInAndStartDateTimeAfterOrderByStartDateTimeAsc(
							countries.get(), LocalDateTime.now(ZoneId.of(Constants.LIMA_ZONE_ID)), pagination);
		} else {
			workshops = past
					? workshopDAO.findByStartDateTimeBeforeOrderByStartDateTimeDesc(
							LocalDateTime.now(ZoneId.of(Constants.LIMA_ZONE_ID)), pagination)
					: workshopDAO.findByStartDateTimeAfterOrderByStartDateTimeAsc(
							LocalDateTime.now(ZoneId.of(Constants.LIMA_ZONE_ID)), pagination);
		}

		return workshops.map(workshop -> {
			WorkshopDTO dto = convertToDTO(workshop);
			dto.setTutors(workshop.getWorkshopTutors().stream()
					.map(this::convertToDTO)
					.toList());
			return dto;
		});
	}
	
	
	// ========== DTO conversion ==========
	
	private WorkshopDTO convertToDTO(Workshop workshop) {
		return WorkshopDTO.builder()
				.idWorkshop(workshop.getId())
				.replicationNumber(workshop.getReplicationNumber())
				.name(workshop.getName())
				.type(workshop.getType())
				.description(workshop.getDescription())
				.startDate(dateFormatter.format(workshop.getStartDateTime()))
				.startTime(timeFormatter.format(workshop.getStartDateTime()))
				.endDate(dateFormatter.format(workshop.getEndDateTime()))
				.endTime(timeFormatter.format(workshop.getEndDateTime()))

				.startDateDay(workshop.getStartDateTime().getDayOfMonth())
				.startDateMonth(monthFormatter.format(workshop.getStartDateTime()))
				.startDateFormatted(dateFormatter2.format(workshop.getStartDateTime()))
				.endDateFormatted(dateFormatter2.format(workshop.getEndDateTime()))

				.startDateTimeISO(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(workshop.getStartDateTime()))
				.endDateTimeISO(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(workshop.getEndDateTime()))

				.startDateTimeCalendar(dateTimeFormatterCalendar.format(workshop.getStartDateTime()))
				.endDateTimeCalendar(dateTimeFormatterCalendar.format(workshop.getEndDateTime()))

				.isPaid(workshop.getIsPaid())
				.price(workshop.getPrice())
				.currency(workshop.getCurrency())
				.facebookUrl(workshop.getFacebookUrl())
				.ticketsUrl(workshop.getTicketsUrl())
				.creationDateTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(workshop.getCreationDateTime()))
				.locationId(workshop.getLocation().getId())
				.locationAddress(workshop.getLocation().getAddress1())
				.locationCity(workshop.getLocation().getCity())
				.locationCountry(workshop.getLocation().getCountry())
				.locationLatitude(workshop.getLocation().getLatitude())
				.locationLongitude(workshop.getLocation().getLongitude())
				.labName(workshop.getLocation().getLab() != null ? workshop.getLocation().getLab().getName() : null)
				.subGroupId(workshop.getSubGroup().getId())
				.subGroupName(workshop.getSubGroup().getName())
				.groupId(workshop.getSubGroup().getGroup().getId())
				.groupName(workshop.getSubGroup().getGroup().getName())
				.build();
	}
	
	private Workshop convertToEntity(WorkshopDTO wDTO) {
		Workshop w = new Workshop();
		w.setType(wDTO.getType());
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
		wtDTO.setFabberAvatarUrl(wt.getSubGroupMember().getGroupMember().getFabber().getAvatarUrl());
		
		return wtDTO;
	}
}
