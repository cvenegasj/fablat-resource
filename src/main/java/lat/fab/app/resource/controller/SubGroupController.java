package lat.fab.app.resource.controller;

import lat.fab.app.resource.dto.SubGroupDTO;
import lat.fab.app.resource.dto.SubGroupMemberDTO;
import lat.fab.app.resource.dto.WorkshopDTO;
import lat.fab.app.resource.entities.*;
import lat.fab.app.resource.repository.*;
import lat.fab.app.resource.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth/subgroups")
public class SubGroupController {

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private final DateTimeFormatter timeFormatterIn = DateTimeFormatter.ofPattern("h:m a");
	private final DateTimeFormatter timeFormatterOut = DateTimeFormatter.ofPattern("h:mm a");
	private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM");
	private final DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern("MMM d(EEE) h:mm a");
	
	@Autowired
	private FabberDAO fabberDAO;
	@Autowired
	private SubGroupDAO subGroupDAO;
	@Autowired
	private SubGroupMemberDAO subGroupMemberDAO;
	@Autowired
	private GroupDAO groupDAO;
	@Autowired
	private GroupMemberDAO groupMemberDAO;
	@Autowired
	private WorkshopDAO workshopDAO;
	@Autowired
	private ActivityLogDAO activityLogDAO;

	@RequestMapping(value = "/{idSubGroup}/{email}", method = RequestMethod.GET)
	public SubGroupDTO findOne(@PathVariable Integer idSubGroup, @PathVariable String email) {
		SubGroup subGroup = subGroupDAO.findById(idSubGroup).get();
		SubGroupDTO sDTO = convertToDTO(subGroup);

		// additional properties
		SubGroupMember userAsSubGroupMember = subGroupMemberDAO
				.findBySubGroupIdAndGroupMemberFabberEmail(idSubGroup, email);
		if (userAsSubGroupMember != null) {
			sDTO.setAmIMember(true);
			sDTO.setAmICoordinator(userAsSubGroupMember.getIsCoordinator());
		} else {
			sDTO.setAmIMember(false);
		}

		// subgroup's workshops
		List<WorkshopDTO> workshops = new ArrayList<WorkshopDTO>();
		for (Workshop w : workshopDAO.findBySubGroupId(idSubGroup)) {
			WorkshopDTO wDTO = convertToDTO(w);
			workshops.add(wDTO);
		}
		sDTO.setWorkshops(workshops);

		// subgroup's members
		List<SubGroupMemberDTO> members = new ArrayList<SubGroupMemberDTO>();
		for (SubGroupMember sgm : subGroupMemberDAO.findBySubGroup_Id(subGroup.getId())) {
			SubGroupMemberDTO sgmDTO = convertToDTO(sgm);
			members.add(sgmDTO);
		}
		sDTO.setMembers(members);

		return sDTO;
	}
	
	@RequestMapping(value = "/{idSubGroup}/verify-me/{email}", method = RequestMethod.GET)
	public SubGroupDTO verifyMe(@PathVariable Integer idSubGroup, @PathVariable String email) {
		SubGroup subGroup = subGroupDAO.findById(idSubGroup).get();
		SubGroupDTO sDTO = convertToDTO(subGroup);

		// additional properties
		SubGroupMember userAsSubGroupMember = subGroupMemberDAO.findBySubGroupIdAndGroupMemberFabberEmail(idSubGroup, email);
		if (userAsSubGroupMember != null) {
			sDTO.setAmIMember(true);
			sDTO.setAmICoordinator(userAsSubGroupMember.getIsCoordinator());
		} else {
			sDTO.setAmIMember(false);
		}

		return sDTO;
	}

	@RequestMapping(value = "/{email}", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public SubGroupDTO create(@PathVariable String email, @RequestBody SubGroupDTO subGroupDTO) {
		SubGroup subGroup = convertToEntity(subGroupDTO);
		subGroup.setEnabled(true);
		// set creation datetime
		Instant now = Instant.now();
		subGroup.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));

		// parent group
		subGroup.setGroup(groupDAO.findById(subGroupDTO.getGroupId()).get());
		// creator
		SubGroupMember sgm = new SubGroupMember();
		sgm.setIsCoordinator(true);
		sgm.setNotificationsEnabled(true);
		sgm.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));
		Optional<GroupMember> gm = groupMemberDAO.findByGroupIdAndFabberEmail(subGroupDTO.getGroupId(), email);
		sgm.setGroupMember(gm.get());
		sgm.setSubGroup(subGroup);
		subGroup.getSubGroupMembers().add(sgm);
		
		// create activities on subgroup creation
        ActivityLog activity = new  ActivityLog();
        activity.setLevel(Constants.ACTIVITY_LEVEL_SUBGROUP);
        activity.setType(Constants.ACTIVITY_TYPE_ORIGIN); // it's the origin of the subgroup
        activity.setVisibility(Constants.ACTIVITY_VISIBILITY_EXTERNAL); // app-wide visibility
        activity.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));
        activity.setSubGroup(subGroup);
        activity.setGroup(subGroup.getGroup());
        activity.setFabber(sgm.getGroupMember().getFabber());
        // activity for the parent group
        ActivityLog activity2 = new ActivityLog();
        activity2.setLevel(Constants.ACTIVITY_LEVEL_GROUP);
        activity2.setType(Constants.ACTIVITY_TYPE_SUBGROUP_CREATED);
        activity2.setVisibility(Constants.ACTIVITY_VISIBILITY_INTERNAL);
        activity2.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));
        activity2.setGroup(groupDAO.findById(subGroupDTO.getGroupId()).get());
        activity2.setFabber(gm.get().getFabber());
		
		SubGroup subGroupCreated = subGroupDAO.save(subGroup);
		activityLogDAO.save(activity);
		activityLogDAO.save(activity2);

		return convertToDTO(subGroupCreated);
	}
	
	@RequestMapping(value = "/{idSubGroup}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("idSubGroup") Integer idSubGroup, @RequestBody SubGroupDTO subGroupDTO) throws ParseException {
		SubGroup subGroup = subGroupDAO.findById(idSubGroup).get();
		subGroup.setName(subGroupDTO.getName());
		subGroup.setDescription(subGroupDTO.getDescription());
		subGroup.setReunionDay(subGroupDTO.getReunionDay());
		subGroup.setReunionTime(subGroupDTO.getReunionTime() != null ? LocalTime.parse(subGroupDTO.getReunionTime(), timeFormatterIn) : null);
		subGroup.setMainUrl(subGroupDTO.getMainUrl());
		subGroup.setSecondaryUrl(subGroupDTO.getSecondaryUrl());
		subGroup.setPhotoUrl(subGroupDTO.getPhotoUrl());
		
		subGroupDAO.save(subGroup);
    }
	
	@RequestMapping(value = "/{idSubGroup}/{email}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable Integer idSubGroup, @PathVariable String email) {
		SubGroup subGroup = subGroupDAO.findById(idSubGroup).get();
		
		// activity for the parent group
        ActivityLog activity = new ActivityLog();
        activity.setLevel(Constants.ACTIVITY_LEVEL_GROUP);
        activity.setType(Constants.ACTIVITY_TYPE_SUBGROUP_DELETED);
        activity.setVisibility(Constants.ACTIVITY_VISIBILITY_INTERNAL);
        Instant now = Instant.now();
        activity.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));
        activity.setGroup(subGroup.getGroup());
        activity.setFabber(
				groupMemberDAO.findByGroupIdAndFabberEmail(subGroup.getGroup().getId(), email).get().getFabber());
        
        activityLogDAO.save(activity);
        subGroupDAO.save(subGroup);
	}
	
	@RequestMapping(value = "/{idSubGroup}/join/{email}", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public void join(@PathVariable Integer idSubGroup, @PathVariable String email) {
		// check if is already member
		if (subGroupMemberDAO.findBySubGroupIdAndGroupMemberFabberEmail(idSubGroup, email) != null) {
			return;
		}
		
		SubGroup subGroup = subGroupDAO.findById(idSubGroup).get();
		
		SubGroupMember member = new SubGroupMember();
		member.setIsCoordinator(subGroupMemberDAO.findBySubGroup_Id(subGroup.getId()).size() == 0 ? true : false);
		member.setNotificationsEnabled(true);
		// set creation datetime 
        Instant now = Instant.now();
        member.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));  
        
        Optional<GroupMember> gm = groupMemberDAO.findByGroupIdAndFabberEmail(subGroup.getGroup().getId(), email);
        // if user is not member of parent group, we add it
        if (gm.isEmpty()) {
			GroupMember gm_ = new GroupMember();
			gm_.setIsCoordinator(false);
			gm_.setNotificationsEnabled(true);
			gm_.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));
			gm_.setFabber(fabberDAO.findByEmail(email).get());
			gm_.setGroup(subGroup.getGroup());
        	groupMemberDAO.save(gm_);
        }
      
        member.setGroupMember(gm.get());
        member.setSubGroup(subGroup);
		subGroupMemberDAO.save(member);
		
		// generate activity
        ActivityLog activity = new  ActivityLog();
        activity.setLevel(Constants.ACTIVITY_LEVEL_SUBGROUP);
        activity.setType(Constants.ACTIVITY_TYPE_USER_JOINED);
        activity.setVisibility(Constants.ACTIVITY_VISIBILITY_INTERNAL); // internal visibility
        activity.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));
        activity.setSubGroup(subGroup);
        activity.setFabber(member.getGroupMember().getFabber());
        activityLogDAO.save(activity);
	}
	
	@RequestMapping(value = "/{idSubGroup}/leave/{email}", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void leave(@PathVariable Integer idSubGroup, @PathVariable String email) {
		SubGroupMember member = subGroupMemberDAO.findBySubGroupIdAndGroupMemberFabberEmail(idSubGroup, email);
		subGroupMemberDAO.delete(member);
		SubGroup subGroup = subGroupDAO.findById(idSubGroup).get();
		
		// if the user was the last member, the subgroup disappears
		if (subGroupMemberDAO.findBySubGroup_Id(subGroup.getId()).size() == 0) {
			subGroupDAO.delete(subGroup);
			return;
		}
		
		// if the user was the only coordinator, assign the oldest member as coordinator
		if (!subGroupMemberDAO.findBySubGroup_Id(subGroup.getId()).stream().anyMatch(item -> item.getIsCoordinator())) {
			SubGroupMember oldestMember = subGroupMemberDAO.findBySubGroup_Id(subGroup.getId()).stream().findFirst().get();
			oldestMember.setIsCoordinator(true);		
			subGroupMemberDAO.save(oldestMember);
		}
		
		// generate activity
        ActivityLog activity = new  ActivityLog();
        activity.setLevel(Constants.ACTIVITY_LEVEL_SUBGROUP);
        activity.setType(Constants.ACTIVITY_TYPE_USER_LEFT);
        activity.setVisibility(Constants.ACTIVITY_VISIBILITY_INTERNAL); // internal visibility
        Instant now = Instant.now();
        activity.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));
        activity.setSubGroup(subGroup);
        activity.setFabber(member.getGroupMember().getFabber());
        activityLogDAO.save(activity);
	}
	
	@RequestMapping(value = "/{idSubGroup}/add-member/{email}", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void addMember(@PathVariable Integer idSubGroup, @PathVariable String email,
						  @RequestBody SubGroupMemberDTO subGroupMemberDTO) {
		SubGroupMember me = subGroupMemberDAO.findBySubGroupIdAndGroupMemberFabberEmail(idSubGroup, email);
		// action only allowed to coordinators
		if (!me.getIsCoordinator()) {
			return;
		}
		
		SubGroup subGroup = subGroupDAO.findById(idSubGroup).get();
		SubGroupMember sgm = new SubGroupMember();
		sgm.setIsCoordinator(subGroupMemberDTO.getIsCoordinator());
		sgm.setNotificationsEnabled(true);
		Instant now = Instant.now();
        sgm.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));
        // sgm.setGroupMember(groupMemberDAO.findByGroupAndFabber(subGroup.getGroup().getIdGroup(), email));
        sgm.setSubGroup(subGroup);
		
		subGroupMemberDAO.save(sgm);
	}
	
	@RequestMapping(value = "/{idSubGroup}/delete-member/{email}", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void deleteMember(@PathVariable Integer idSubGroup, @PathVariable String email,
							 @RequestBody SubGroupMemberDTO subGroupMemberDTO) {
		SubGroupMember me = subGroupMemberDAO.findBySubGroupIdAndGroupMemberFabberEmail(idSubGroup, email);
		// action only allowed to coordinators
		if (!me.getIsCoordinator()) {
			return;
		}
		
		SubGroupMember member = subGroupMemberDAO.findById(subGroupMemberDTO.getIdSubGroupMember()).get();
		subGroupMemberDAO.save(member);
	}
	
	@RequestMapping(value = "/{idSubGroup}/name-coordinator/{email}", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void nameCoordinator(@PathVariable Integer idSubGroup, @PathVariable String email,
								@RequestBody SubGroupMemberDTO subGroupMemberDTO) {
		SubGroupMember me = subGroupMemberDAO.findBySubGroupIdAndGroupMemberFabberEmail(idSubGroup, email);
		// action only allowed to coordinators
		if (!me.getIsCoordinator()) {
			return;
		}
		
		SubGroupMember member = subGroupMemberDAO.findById(subGroupMemberDTO.getIdSubGroupMember()).get();
		member.setIsCoordinator(true);
		subGroupMemberDAO.save(member);
	}

	
	// ========== DTO conversion ==========
	
	private SubGroupDTO convertToDTO(SubGroup subGroup) {
		SubGroupDTO sDTO = new SubGroupDTO();
		sDTO.setIdSubGroup(subGroup.getId());
		sDTO.setName(subGroup.getName());
		sDTO.setDescription(subGroup.getDescription());
		sDTO.setReunionDay(subGroup.getReunionDay());
		sDTO.setReunionTime(subGroup.getReunionTime() != null ? timeFormatterOut.format(subGroup.getReunionTime()) : null);
		sDTO.setMainUrl(subGroup.getMainUrl());
		sDTO.setSecondaryUrl(subGroup.getSecondaryUrl());
		sDTO.setPhotoUrl(subGroup.getPhotoUrl());
		sDTO.setCreationDateTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(subGroup.getCreationDateTime()));
		sDTO.setGroupId(subGroup.getGroup().getId());
		sDTO.setGroupName(subGroup.getGroup().getName());

		return sDTO;
	}
	
	private SubGroup convertToEntity(SubGroupDTO sgDTO) {
		SubGroup sg = new SubGroup();
		sg.setName(sgDTO.getName());
		sg.setDescription(sgDTO.getDescription());
		
		return sg;
	}

	private WorkshopDTO convertToDTO(Workshop workshop) {
		WorkshopDTO wDTO = new WorkshopDTO();
		wDTO.setIdWorkshop(workshop.getId());
		wDTO.setReplicationNumber(workshop.getReplicationNumber());
		wDTO.setName(workshop.getName());
		// workshopDTO.setDescription(workshop.getDescription());
		wDTO.setStartDate(dateFormatter.format(workshop.getStartDateTime()));
		wDTO.setStartTime(timeFormatterOut.format(workshop.getStartDateTime()));
		wDTO.setEndDate(dateFormatter.format(workshop.getEndDateTime()));
		wDTO.setEndTime(timeFormatterOut.format(workshop.getEndDateTime()));
		
		wDTO.setStartDateDay(workshop.getStartDateTime().getDayOfMonth());
		wDTO.setStartDateMonth(monthFormatter.format(workshop.getStartDateTime()));
		wDTO.setStartDateFormatted(dateFormatter2.format(workshop.getStartDateTime()));
		wDTO.setEndDateFormatted(dateFormatter2.format(workshop.getEndDateTime()));
		
		wDTO.setStartDateTimeISO(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(workshop.getStartDateTime()));
		wDTO.setEndDateTimeISO(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(workshop.getEndDateTime()));
		
		wDTO.setIsPaid(workshop.getIsPaid());
		wDTO.setPrice(workshop.getPrice());
		wDTO.setCurrency(workshop.getCurrency());
		wDTO.setLabName(workshop.getLocation().getLab() != null ? workshop.getLocation().getLab().getName() : null);

		return wDTO;
	}

	private SubGroupMemberDTO convertToDTO(SubGroupMember sgm) {
		SubGroupMemberDTO sgmDTO = new SubGroupMemberDTO();
		sgmDTO.setIdSubGroupMember(sgm.getId());
		sgmDTO.setName(sgm.getGroupMember().getFabber().getFirstName() == null || sgm.getGroupMember().getFabber().getLastName() == null
				? sgm.getGroupMember().getFabber().getName()
				: sgm.getGroupMember().getFabber().getFirstName() + " " + sgm.getGroupMember().getFabber().getLastName());
		sgmDTO.setFirstName(sgm.getGroupMember().getFabber().getFirstName());
		sgmDTO.setLastName(sgm.getGroupMember().getFabber().getLastName());
		sgmDTO.setEmail(sgm.getGroupMember().getFabber().getEmail());
		sgmDTO.setIsCoordinator(sgm.getIsCoordinator());
		sgmDTO.setCreationDateTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(sgm.getCreationDateTime()));
		sgmDTO.setFabberId(sgm.getGroupMember().getFabber().getId());

		return sgmDTO;
	}

}
