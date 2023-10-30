package lat.fab.app.resource.controller;

import lat.fab.app.resource.dto.*;
import lat.fab.app.resource.entities.*;
import lat.fab.app.resource.repository.*;
import lat.fab.app.resource.util.Constants;
import lat.fab.app.resource.util.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/auth/groups")
public class GroupController {

	private final DateTimeFormatter timeFormatterIn = DateTimeFormatter.ofPattern("h:m a");
	private final DateTimeFormatter timeFormatterOut = DateTimeFormatter.ofPattern("h:mm a");

	@Autowired
	private FabberDAO fabberDAO;
	@Autowired
	private GroupDAO groupDAO;
	@Autowired
	private SubGroupDAO subGroupDAO;
	@Autowired
	private GroupMemberDAO groupMemberDAO;
	@Autowired
	private SubGroupMemberDAO subGroupMemberDAO;
	@Autowired
	private ActivityLogDAO activityLogDAO;
	@Autowired
    public EmailServiceImpl emailService;

	@GetMapping
	public List<GroupLandingDto2> findAllLanding(
			@RequestParam Optional<Integer> page,
			@RequestParam Optional<Integer> size) {
		List<Group> groups = page.isPresent() && size.isPresent()
				? groupDAO.findAllByOrderByCreationDateTimeAsc(PageRequest.of(page.get(), size.get()))
				: groupDAO.findAllByOrderByCreationDateTimeAsc();

		return groups.stream()
				.map(group -> {
					String groupImgUrl = StringUtils.hasText(group.getPhotoUrl())
							? "http://res.cloudinary.com/dymje6shc/image/upload/w_220,h_165,c_fit/"
							+ group.getPhotoUrl()
							: null;

					return GroupLandingDto2.builder()
							.id(group.getId())
							.name(group.getName())
							.description(group.getDescription())
							.score(0) // TODO
							.members(
									groupMemberDAO.findAllByGroupId(group.getId()).stream()
											.map(gm -> FabberDTO.builder()
													.idFabber(gm.getFabber().getId())
													.name(gm.getFabber().getName())
													.avatarUrl(gm.getFabber().getAvatarUrl())
													.build())
											.toList())
							.membersCount(groupMemberDAO.countDistinctByGroupId(group.getId()))
							.imgUrl(groupImgUrl)
							.build();
				})
				.toList();
	}

	@GetMapping("/one/{idGroup}")
	public GroupLandingDto2 findOneLanding(@PathVariable String idGroup) {

		return groupDAO.findById(Integer.valueOf(idGroup))
				.map(group -> {
					String groupImgUrl = StringUtils.hasText(group.getPhotoUrl())
							? "http://res.cloudinary.com/dymje6shc/image/upload/w_220,h_165,c_fit/"
							+ group.getPhotoUrl()
							: null;

					return GroupLandingDto2.builder()
							.id(group.getId())
							.name(group.getName())
							.description(group.getDescription())
							.score(0)
							.members(
									groupMemberDAO.findAllByGroupId(group.getId()).stream()
											.map(gm -> FabberDTO.builder()
													.idFabber(gm.getFabber().getId())
													.name(gm.getFabber().getName())
													.generalScore(gm.getFabber().getFabberInfo().getScoreGeneral())
													.avatarUrl(gm.getFabber().getAvatarUrl())
													.build())
											.toList())
							.membersCount(groupMemberDAO.countDistinctByGroupId(group.getId()))
							.imgUrl(groupImgUrl)
							.build();
				})
				.orElse(null);
	}

	@GetMapping("/filter")
	@ResponseStatus(HttpStatus.OK)
	public Page<GroupLandingDto2> findAllLandingWithFilter(
			@RequestParam Integer page,
			@RequestParam Integer size,
			@RequestParam Optional<String> name) { // group name

		PageRequest pagination = PageRequest.of(page, size);

		String nameFilter = name.isPresent() ? "%" + name.get() + "%" : "%";
		Page<Group> groups = null;

		if (name.isPresent()) {
			groups = groupDAO.findByNameIgnoreCaseLikeOrderByNameAsc(nameFilter, pagination);
		} else {
			groups = groupDAO.findAll(pagination);
		}

		return groups
				.map(group -> {
					String groupImgUrl = StringUtils.hasText(group.getPhotoUrl())
							? "http://res.cloudinary.com/dymje6shc/image/upload/w_220,h_165,c_fit/"
							+ group.getPhotoUrl()
							: null;

					return GroupLandingDto2.builder()
							.id(group.getId())
							.name(group.getName())
							.description(group.getDescription())
							.score(0)
							.members(
									groupMemberDAO.findAllByGroupId(group.getId()).stream()
											.map(gm -> FabberDTO.builder()
													.idFabber(gm.getFabber().getId())
													.name(gm.getFabber().getName())
													.avatarUrl(gm.getFabber().getAvatarUrl())
													.build())
											.toList())
							.membersCount(groupMemberDAO.countDistinctByGroupId(group.getId()))
							.imgUrl(groupImgUrl)
							.build();
				});
	}

	@GetMapping("/sort")
	@ResponseStatus(HttpStatus.OK)
	public Page<GroupLandingDto2> findAllLandingWithSorting(
			@RequestParam Integer page,
			@RequestParam Integer size,
			@RequestParam String sortBy) {

		PageRequest pagination = PageRequest.of(page, size);

		Page<Group> groups = switch (sortBy) {
			case "name" -> groupDAO.findAllByOrderByNameDesc(pagination);
//			case "score" -> groupDAO.findAllByOrderByFabberInfoScoreGeneralDesc(pagination);
			case "membersCount" -> groupDAO.findAllOrderByMembersCountDesc(pagination);
			default -> Page.empty();
		};

		return groups
				.map(group -> {
					String groupImgUrl = StringUtils.hasText(group.getPhotoUrl())
							? "http://res.cloudinary.com/dymje6shc/image/upload/w_220,h_165,c_fit/"
							+ group.getPhotoUrl()
							: null;

					return GroupLandingDto2.builder()
							.id(group.getId())
							.name(group.getName())
							.description(group.getDescription())
							.score(0)
							.members(
									groupMemberDAO.findAllByGroupId(group.getId()).stream()
											.map(gm -> FabberDTO.builder()
													.idFabber(gm.getFabber().getId())
													.name(gm.getFabber().getName())
													.avatarUrl(gm.getFabber().getAvatarUrl())
													.build())
											.toList())
							.membersCount(groupMemberDAO.countDistinctByGroupId(group.getId()))
							.imgUrl(groupImgUrl)
							.build();
				});
	}

	@RequestMapping(value = "/{email}", method = RequestMethod.GET)
	public List<GroupDTO> findAll(@PathVariable String email) {
		List<GroupDTO> returnList = new ArrayList<GroupDTO>();
		
		for (Group group : groupDAO.findAllByOrderByCreationDateTimeAsc()) {
			GroupDTO gDTO = convertToDTO(group);
			gDTO.setMembersCount(groupMemberDAO.countDistinctByGroupId(group.getId()));
			gDTO.setSubGroupsCount(subGroupDAO.countDistinctByGroupId(group.getId()));
			
			gDTO.setAmIMember(groupMemberDAO.findByGroupIdAndFabberEmail(group.getId(), email) != null);

      	  	returnList.add(gDTO);
		}
	
		return returnList;
	}
	
	@RequestMapping(value = "/find-all-mine/{email}", method = RequestMethod.GET)
	public List<GroupDTO> findAllMine(@PathVariable String email) {
		List<GroupDTO> returnList = new ArrayList<GroupDTO>();
		
		// Mapping user's group and subgroups
		for (GroupMember gm : groupMemberDAO.findAllByFabberEmail(email)) {
			GroupDTO gDTO = convertToDTO(gm.getGroup());
			gDTO.setMembersCount(groupMemberDAO.countDistinctByGroupId(gm.getGroup().getId()));
			gDTO.setSubGroupsCount(subGroupDAO.countDistinctByGroupId(gm.getGroup().getId()));
			
			// additional properties
			gDTO.setAmIMember(true);
      	  	gDTO.setAmICoordinator(gm.getIsCoordinator());
			
      	  	// subgroups
      	  	List<SubGroupDTO> subGroups = new ArrayList<SubGroupDTO>();
      	  	for (SubGroupMember sgm : subGroupMemberDAO.findBySubGroup_Group_IdAndGroupMember_Fabber_EmailOrderByCreationDateTimeAsc(
						gm.getGroup().getId(), email)) {
      	  		SubGroupDTO sDTO = convertToDTO(sgm.getSubGroup());
      	  		sDTO.setMembersCount(subGroupMemberDAO.countDistinctBySubGroupId(sgm.getSubGroup().getId()));
				sDTO.setAmIMember(true);
				sDTO.setAmICoordinator(sgm.getIsCoordinator());
      	  		
      	  		subGroups.add(sDTO);
      	  	}
      	  	gDTO.setSubGroups(subGroups);
      	  	returnList.add(gDTO);
		}	
		
		return returnList;
	}
	
	@RequestMapping(value = "/find-all-fabber/{idFabber}", method = RequestMethod.GET)
	public List<GroupDTO> findAllFabber(@PathVariable Integer idFabber) {
		List<GroupDTO> returnList = new ArrayList<>();
		
		// Mapping user's group and subgroups
		for (GroupMember gm : groupMemberDAO.findAllByFabberId(idFabber)) {
			GroupDTO gDTO = convertToDTO(gm.getGroup());
			gDTO.setMembersCount(groupMemberDAO.countDistinctByGroupId(gm.getGroup().getId()));
			gDTO.setSubGroupsCount(subGroupDAO.countDistinctByGroupId(gm.getGroup().getId()));
			
			// additional properties
			gDTO.setAmIMember(true);
      	  	gDTO.setAmICoordinator(gm.getIsCoordinator());
			
      	  	// subgroups
      	  	List<SubGroupDTO> subGroups = new ArrayList<SubGroupDTO>();
      	  	for (SubGroupMember sgm : subGroupMemberDAO.findBySubGroup_Group_IdAndGroupMember_Fabber_IdOrderByCreationDateTimeAsc(gm.getGroup().getId(), idFabber)) {
      	  		SubGroupDTO sDTO = convertToDTO(sgm.getSubGroup());
				sDTO.setAmIMember(true);
				sDTO.setAmICoordinator(sgm.getIsCoordinator());
      	  		
      	  		subGroups.add(sDTO);
      	  	}
      	  	gDTO.setSubGroups(subGroups);
      	  	returnList.add(gDTO);
		}	
		
		return returnList;
	}
	
	@RequestMapping(value = "/search/{searchText}", method = RequestMethod.GET)
	public List<GroupDTO> findByTerm(@PathVariable("searchText") String searchText) { 
		List<GroupDTO> returnList = new ArrayList<>();
		for (Group g : groupDAO.findByNameIgnoreCase(searchText)) {
			GroupDTO gDTO = new GroupDTO();
			gDTO.setIdGroup(g.getId());
			gDTO.setName(g.getName());
			returnList.add(gDTO);
		}
		
		return returnList;
	}
	
	@RequestMapping(value = "/{idGroup}/{email}", method = RequestMethod.GET)
    public GroupDTO findOne(@PathVariable Integer idGroup, @PathVariable String email) {
		Group group = groupDAO.findById(idGroup).get();
		GroupDTO gDTO = convertToDTO(group);
		
		// additional properties
		Optional<GroupMember> userAsGroupMember = groupMemberDAO.findByGroupIdAndFabberEmail(idGroup, email);
		if (userAsGroupMember.isPresent()) {
			gDTO.setAmIMember(true);
			gDTO.setAmICoordinator(userAsGroupMember.get().getIsCoordinator());
		} else {
			gDTO.setAmIMember(false);
		}
		
		// group's subgroups
		List<SubGroupDTO> subGroups = new ArrayList<SubGroupDTO>();
		for (SubGroup sg : subGroupDAO.findAllByGroupId(group.getId())) {
			SubGroupDTO sDTO = convertToDTO(sg);
			sDTO.setMembersCount(subGroupMemberDAO.countDistinctBySubGroupId(sg.getId()));
			
			SubGroupMember userAsSubGroupMember = subGroupMemberDAO.findBySubGroupIdAndGroupMemberFabberEmail(
					sg.getId(), email);
			if (userAsSubGroupMember != null) {
				sDTO.setAmIMember(true);
				sDTO.setAmICoordinator(userAsSubGroupMember.getIsCoordinator());
			} else {
				sDTO.setAmIMember(false);
			}
			
			subGroups.add(sDTO);
		}
		gDTO.setSubGroups(subGroups);
		
		// group's members
		List<GroupMemberDTO> members = new ArrayList<>();
		for (GroupMember gm : groupMemberDAO.findAllByGroupId(group.getId())) {
			GroupMemberDTO gmDTO = convertToDTO(gm);
			members.add(gmDTO);
		}
		gDTO.setMembers(members);
		
        return gDTO;
    }
	
	@RequestMapping(value = "/{idGroup}/verify-me/{email}", method = RequestMethod.GET)
    public GroupDTO verifyMe(@PathVariable Integer idGroup, @PathVariable String email) {
		Group group = groupDAO.findById(idGroup).get();
		GroupDTO gDTO = convertToDTO(group);
		
		// additional properties
		Optional<GroupMember> userAsGroupMember = groupMemberDAO.findByGroupIdAndFabberEmail(idGroup, email);
		if (userAsGroupMember.isPresent()) {
			gDTO.setAmIMember(true);
			gDTO.setAmICoordinator(userAsGroupMember.get().getIsCoordinator());
		} else {
			gDTO.setAmIMember(false);
		}
		
        return gDTO;
    }
	
	@RequestMapping(value = "/{email}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public GroupDTO create(@PathVariable String email, @RequestBody GroupDTO groupDTO) {
		Group group = convertToEntity(groupDTO);
        group.setEnabled(true);
        // set creation datetime 
        Instant now = Instant.now();
        group.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));
        
        // creator
        GroupMember gm = new GroupMember();
        gm.setIsCoordinator(true);
        gm.setNotificationsEnabled(true);
        gm.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));
        gm.setFabber(fabberDAO.findByEmail(email).get());
        gm.setGroup(group);
        group.getGroupMembers().add(gm);
        
        // create activity on group creation
        ActivityLog activity = new  ActivityLog();
        activity.setLevel(Constants.ACTIVITY_LEVEL_GROUP);
        activity.setType(Constants.ACTIVITY_TYPE_ORIGIN); // it's the origin of the group
        activity.setVisibility(Constants.ACTIVITY_VISIBILITY_EXTERNAL); // app-wide visibility
        activity.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));
        activity.setGroup(group);
        activity.setFabber(gm.getFabber());
        
        Group groupCreated = groupDAO.save(group);
        activityLogDAO.save(activity);
        
        return convertToDTO(groupCreated);
    }
	
	@RequestMapping(value = "/{idGroup}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("idGroup") Integer idGroup, @RequestBody GroupDTO groupDTO) {
		Group group = groupDAO.findById(idGroup).get();
		group.setName(groupDTO.getName());
		group.setDescription(groupDTO.getDescription());
		group.setReunionDay(groupDTO.getReunionDay());
		group.setReunionTime(groupDTO.getReunionTime() != null ?
				LocalTime.parse(groupDTO.getReunionTime(), timeFormatterIn) : null);
		group.setMainUrl(groupDTO.getMainUrl());
		group.setSecondaryUrl(groupDTO.getSecondaryUrl());
		group.setSymbioUrl(groupDTO.getSymbioUrl());
		group.setPhotoUrl(groupDTO.getPhotoUrl());
		
		groupDAO.save(group);
    }
	
	@RequestMapping(value = "/{idGroup}/update-avatar", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void updateAvatar(@PathVariable("idGroup") Integer idGroup, @RequestBody GroupDTO groupDTO) {
		Group group = groupDAO.findById(idGroup).get();
		group.setPhotoUrl(groupDTO.getPhotoUrl());
		System.out.println("PHOTO URL: " + groupDTO.getPhotoUrl());
		
		groupDAO.save(group);
    }
	
	@DeleteMapping("/{idGroup}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable Integer idGroup) {
		groupDAO.deleteById(idGroup);
	}
	
	@RequestMapping(value = "/{idGroup}/join/{email}", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public void join(@PathVariable Integer idGroup, @PathVariable String email) {
		// check if is already member
		if (groupMemberDAO.findByGroupIdAndFabberEmail(idGroup, email) != null) {
			return;
		}
		
		Group group = groupDAO.findById(idGroup).get();
		
		GroupMember member = new GroupMember();
		member.setIsCoordinator(groupMemberDAO.countDistinctByGroupId(group.getId()) == 0);
		member.setNotificationsEnabled(true);
		// set creation datetime 
        Instant now = Instant.now();
        member.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));  
        
        member.setFabber(fabberDAO.findByEmail(email).get());
        member.setGroup(group);
		groupMemberDAO.save(member);
		
		// generate activity
		ActivityLog activity = new  ActivityLog();
		activity.setLevel(Constants.ACTIVITY_LEVEL_GROUP);
        activity.setType(Constants.ACTIVITY_TYPE_USER_JOINED);
        activity.setVisibility(Constants.ACTIVITY_VISIBILITY_INTERNAL); // internal visibility
        activity.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));
        activity.setGroup(group);
        activity.setFabber(member.getFabber());
        activityLogDAO.save(activity);
	}
	
	@RequestMapping(value = "/{idGroup}/leave/{email}", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void leave(@PathVariable Integer idGroup, @PathVariable String email) {
		Optional<GroupMember> member = groupMemberDAO.findByGroupIdAndFabberEmail(idGroup, email);
		groupMemberDAO.save(member.get());
		Group group = groupDAO.findById(idGroup).get();
		
		// if the user was the last member, the group disappears
		if (groupMemberDAO.countDistinctByGroupId(group.getId()) == 0) {
			groupDAO.save(group);
			return;
		}
		
		// if the user was the only coordinator, assign the oldest member as coordinator
		if (!groupMemberDAO.findAllByGroupId(idGroup).stream().anyMatch(item -> item.getIsCoordinator())) {
			GroupMember oldestMember = groupMemberDAO.findAllByGroupId(idGroup).stream().findFirst().get();
			oldestMember.setIsCoordinator(true);		
			groupMemberDAO.save(oldestMember);
		}
		
		// generate activity
        ActivityLog activity = new  ActivityLog();
        activity.setLevel(Constants.ACTIVITY_LEVEL_GROUP);
        activity.setType(Constants.ACTIVITY_TYPE_USER_LEFT);
        activity.setVisibility(Constants.ACTIVITY_VISIBILITY_INTERNAL); // internal visibility
        Instant now = Instant.now();
        activity.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));
        activity.setGroup(group);
        activity.setFabber(member.get().getFabber());
        activityLogDAO.save(activity);
	}
	
	@RequestMapping(value = "/{idGroup}/members", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void addMember(@PathVariable Integer idGroup, @RequestBody GroupMemberDTO groupMemberDTO) {
		//GroupMember me = groupMemberDAO.findByGroupAndFabber(idGroup, principal.getName());
		// action only allowed to coordinators
		//if (!me.getIsCoordinator()) {
		//	return;
		//}
		
		GroupMember gm = groupMemberDAO.findByGroupIdAndFabberId(idGroup, groupMemberDTO.getFabberId());
		// check if already member
		if (gm != null) {
			return;
		}
		
		gm = new GroupMember();
		gm.setIsCoordinator(false);
		gm.setNotificationsEnabled(true);
		Instant now = Instant.now();
		gm.setCreationDateTime(LocalDateTime.ofInstant(now, ZoneOffset.UTC));
		gm.setFabber(fabberDAO.findById(groupMemberDTO.getFabberId()).get());
		gm.setGroup(groupDAO.findById(idGroup).get());
		
		groupMemberDAO.save(gm);
	}
	
	@RequestMapping(value = "/{idGroup}/members/{idGroupMember}/{email}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void deleteMember(
			@PathVariable Integer idGroup, @PathVariable Integer idGroupMember, @PathVariable String email) {
		Optional<GroupMember> me = groupMemberDAO.findByGroupIdAndFabberEmail(idGroup, email);
		// action only allowed to coordinators
		if (!me.get().getIsCoordinator()) {
			return;
		}
		
		GroupMember member = groupMemberDAO.findById(idGroupMember).get();
		groupMemberDAO.save(member);
	}
	
	@RequestMapping(value = "/{idGroup}/members/send-invitation-email", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void sendInvitationEmail(@PathVariable("idGroup") Integer idGroup, @RequestBody FabberDTO fabberDTO) {
		Group g = groupDAO.findById(idGroup).get();
		
		emailService.sendHTMLMessage(fabberDTO.getEmail(),
                "Invitation to join a Fab Lat group", 
                "You have received an invitation to join the group " + g.getName() + ". <br />"
                		+ "Join the Fab Lat platform on <a href=\"fablat.net\">fablat.net</a>.");
	}
	
	@RequestMapping(value = "/{idGroup}/members/autocomplete/{searchText}", method = RequestMethod.GET)
	public List<HashMap<String, Object>> searchAutocomplete(
			@PathVariable("idGroup") Integer idGroup, @PathVariable("searchText") String searchText) {
		List<HashMap<String, Object>> models = new ArrayList<>();
		
		for (Fabber f : fabberDAO.findByEmailLike(searchText)) { // TODO
			// do not include already members
			if (groupMemberDAO.findByGroupIdAndFabberId(idGroup, f.getId()) != null) {
				continue;
			}
			HashMap<String, Object> model = new HashMap<String, Object>();
			model.put("idFabber", f.getId());
			model.put("email", f.getEmail());
			model.put("name", f.getName());
			model.put("firstName", f.getFirstName());
			model.put("lastName", f.getLastName());
			model.put("fullName", f.getFirstName() + " " + f.getLastName());
			
			models.add(model);
		}
		
		return models;
	}
	
	@RequestMapping(value = "/{idGroup}/name-coordinator/{email}", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void nameCoordinator(
			@PathVariable Integer idGroup, @PathVariable String email, @RequestBody GroupMemberDTO groupMemberDTO) {
		Optional<GroupMember> me = groupMemberDAO.findByGroupIdAndFabberEmail(idGroup, email);
		// action only allowed to coordinators
		if (!me.get().getIsCoordinator()) {
			return;
		}
		
		GroupMember member = groupMemberDAO.findById(groupMemberDTO.getIdGroupMember()).get();
		member.setIsCoordinator(true);
		groupMemberDAO.save(member);
	}

	@GetMapping("/count")
	@ResponseStatus(HttpStatus.OK)
	public long countAll() {
		return groupDAO.count();
	}
	
	
	// ========== DTO conversion ==========
	
	private GroupDTO convertToDTO(Group group) {
		GroupDTO groupDTO = new GroupDTO();
		groupDTO.setIdGroup(group.getId());
		groupDTO.setName(group.getName());
		groupDTO.setDescription(group.getDescription());
		groupDTO.setReunionDay(group.getReunionDay());
		groupDTO.setReunionTime(group.getReunionTime() != null ? timeFormatterOut.format(group.getReunionTime()) : null);
		groupDTO.setMainUrl(group.getMainUrl());
		groupDTO.setSecondaryUrl(group.getSecondaryUrl());
		groupDTO.setSymbioUrl(group.getSymbioUrl());
		groupDTO.setPhotoUrl(group.getPhotoUrl());
		groupDTO.setCreationDateTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(group.getCreationDateTime()));
		
		return groupDTO;
	}
	
	private Group convertToEntity(GroupDTO gDTO) {
		Group g = new Group();
		g.setName(gDTO.getName());
		g.setDescription(gDTO.getDescription());
		
		return g;
	}
	
	private SubGroupDTO convertToDTO(SubGroup subGroup) {
		SubGroupDTO subGroupDTO = new SubGroupDTO();
		subGroupDTO.setIdSubGroup(subGroup.getId());
		subGroupDTO.setName(subGroup.getName());
		subGroupDTO.setDescription(subGroup.getDescription());
		
		return subGroupDTO;
	}
	
	private GroupMemberDTO convertToDTO(GroupMember gm) {
		GroupMemberDTO gmDTO = new GroupMemberDTO();
		gmDTO.setIdGroupMember(gm.getId());
		gmDTO.setName(gm.getFabber().getFirstName() == null ||gm.getFabber().getLastName() == null
				? gm.getFabber().getName()
				: gm.getFabber().getFirstName() + " " + gm.getFabber().getLastName());
		gmDTO.setFirstName(gm.getFabber().getFirstName());
		gmDTO.setLastName(gm.getFabber().getLastName());
		gmDTO.setEmail(gm.getFabber().getEmail());
		gmDTO.setIsCoordinator(gm.getIsCoordinator());
		gmDTO.setCreationDateTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(gm.getCreationDateTime()));
		gmDTO.setFabberId(gm.getFabber().getId());
		
		return gmDTO;
	}
	
}
