package lat.fab.app.resource.controller;

import lat.fab.app.resource.dto.FabberDTO;
import lat.fab.app.resource.dto.GroupLandingDto2;
import lat.fab.app.resource.entities.*;
import lat.fab.app.resource.repository.FabberDAO;
import lat.fab.app.resource.repository.GroupMemberDAO;
import lat.fab.app.resource.repository.RoleDAO;
import lat.fab.app.resource.repository.WorkshopTutorDAO;
import lat.fab.app.resource.service.GroupStatsService;
import lat.fab.app.resource.service.UserStatsService;
import lat.fab.app.resource.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(value = "/auth/fabbers")
@RequiredArgsConstructor
@Slf4j
public class FabberController {

	private final FabberDAO fabberDAO;
	private final GroupMemberDAO groupMemberDAO;
	private final WorkshopTutorDAO workshopTutorDAO;
	private final RoleDAO roleDAO;
	private final UserStatsService userStatsService;
	private final GroupStatsService groupStatsService;

	@PostMapping("/createOrUpdateUser")
	@ResponseStatus(HttpStatus.CREATED)
	public FabberDTO createOrUpdateUser(@AuthenticationPrincipal JwtAuthenticationToken jwtAuthToken) {
		log.info("createOrUpdateUser(), principal.email: {}", jwtAuthToken.getTokenAttributes().get("email"));

        final Optional<Fabber> retrievedUser = fabberDAO.findByEmail(
				(String) jwtAuthToken.getTokenAttributes().get("email"));

        if (retrievedUser.isPresent()) { // if user exists in database, update it
            retrievedUser.get().setName((String) jwtAuthToken.getTokenAttributes().get("name"));
            retrievedUser.get().setFirstName((String) jwtAuthToken.getTokenAttributes().get("given_name"));
            retrievedUser.get().setLastName((String) jwtAuthToken.getTokenAttributes().get("family_name"));
            retrievedUser.get().setAvatarUrl((String) jwtAuthToken.getTokenAttributes().get("picture"));

            Fabber persisted = fabberDAO.save(retrievedUser.get());
			Fabber updated = userStatsService.updateFabberScores(persisted);
            return convertToDTO(updated);
        } else { // otherwise, create new user
            Fabber newUser = Fabber.builder()
                    .email((String) jwtAuthToken.getTokenAttributes().get("email"))
                    .name((String) jwtAuthToken.getTokenAttributes().get("name"))
                    .firstName((String) jwtAuthToken.getTokenAttributes().get("given_name"))
                    .lastName((String) jwtAuthToken.getTokenAttributes().get("family_name"))
                    .avatarUrl((String) jwtAuthToken.getTokenAttributes().get("picture"))
                    .isFabAcademyGrad(false)
                    .isNomade(true)
                    .enabled(true)
                    .build();

            // set RoleFabber: ROLE_USER
            RoleFabber roleFabber = new RoleFabber();
            roleFabber.setRole(roleDAO.findByName(Constants.ROLE_USER));
            roleFabber.setFabber(newUser);
            newUser.setRoleFabbers(Set.of(roleFabber));

            // set FabberInfo
            FabberInfo fabberInfo = new FabberInfo();
            fabberInfo.setFabber(newUser);
            fabberInfo.setScoreGeneral(0);
            fabberInfo.setScoreCoordinator(0);
            fabberInfo.setScoreCollaborator(0);
            fabberInfo.setScoreReplicator(0);
            newUser.setFabberInfo(fabberInfo);

            Fabber persisted = fabberDAO.save(newUser);
            FabberDTO fabberDTO = convertToDTO(persisted);
            return fabberDTO;
        }
	}
		
	@GetMapping("/me/profile/{email}")
	@ResponseStatus(HttpStatus.OK)
	public FabberDTO getMyProfile(@PathVariable String email) {
		Fabber fabber = fabberDAO.findByEmail(email).get();
        return convertToDTO(fabber);
	}
	
	@GetMapping(value = "/{idFabber}")
	@ResponseStatus(HttpStatus.OK)
    public FabberDTO findOneLanding(@PathVariable("idFabber") Integer idFabber) {
        return fabberDAO.findById(idFabber)
				.map(fabber -> {
					FabberDTO dto = this.convertToDTO(fabber);
					addNewFieldsToFabberDto(dto, groupMemberDAO.findAllByFabberId(fabber.getId()),
							Optional.empty(), Optional.empty());
					return dto;
				})
				.orElse(null);
    }

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<FabberDTO> findAllLanding(
			@RequestParam Optional<Integer> page,
			@RequestParam Optional<Integer> size) {
		List<Fabber> fabbers = page.isPresent() && size.isPresent()
				? fabberDAO.findAll(PageRequest.of(page.get(), size.get())).toList()
				: fabberDAO.findAll();

		return fabbers.stream()
				.map(fabber -> {
					FabberDTO dto = this.convertToDTO(fabber);
					addNewFieldsToFabberDto(dto, groupMemberDAO.findAllByFabberId(fabber.getId()),
							Optional.empty(), Optional.empty());
					return dto;
				})
				.toList();
	}

	@GetMapping("/filter")
	@ResponseStatus(HttpStatus.OK)
	public Page<FabberDTO> findAllLandingWithFilter(
			@RequestParam Integer page,
			@RequestParam Integer size,
			@RequestParam Optional<String> name, // fabber name
			@RequestParam Optional<List<String>> countries) { // comma-separated list of countries keys

		PageRequest pagination = PageRequest.of(page, size);

		String nameFilter = name.isPresent() ? "%" + name.get() + "%" : "%";
		Page<Fabber> fabbers = null;

		if (name.isPresent() && countries.isPresent()) {
			fabbers = fabberDAO.findByNameIgnoreCaseLikeAndCountryIsInOrderByNameAsc(
					nameFilter, countries.get(), pagination);
		} else if (name.isPresent()) {
			fabbers = fabberDAO.findByNameIgnoreCaseLikeOrderByNameAsc(nameFilter, pagination);
		} else if (countries.isPresent()) {
			fabbers = fabberDAO.findByCountryIsInOrderByNameAsc(countries.get(), pagination);
		} else {
			fabbers = fabberDAO.findAll(pagination);
		}

		return fabbers
				.map(fabber -> {
					int workshopsCount =
							workshopTutorDAO.countBySubGroupMember_GroupMember_FabberEmail_AndWorkshopTypeIsIn(
									fabber.getEmail(), List.of(EventType.WORKSHOP));
					int eventsCount =
							workshopTutorDAO.countBySubGroupMember_GroupMember_FabberEmail_AndWorkshopTypeIsIn(
									fabber.getEmail(), List.of(EventType.CONFERENCE, EventType.OTHER));

					FabberDTO dto = this.convertToDTO(fabber);
					addNewFieldsToFabberDto(dto, groupMemberDAO.findAllByFabberId(fabber.getId()),
							Optional.of(workshopsCount), Optional.of(eventsCount));
					return dto;
				});
	}

	@GetMapping("/sort")
	@ResponseStatus(HttpStatus.OK)
	public Page<FabberDTO> findAllLandingWithSorting(
			@RequestParam Integer page,
			@RequestParam Integer size,
			@RequestParam String sortBy) {

		PageRequest pagination = PageRequest.of(page, size);

		Page<Fabber> fabbers = switch (sortBy) {
			case "name" -> fabberDAO.findAllByOrderByNameDesc(pagination);
			case "score" -> fabberDAO.findAllByOrderByFabberInfoScoreGeneralDesc(pagination);
			case "groupsCount" -> fabberDAO.findAllOrderByGroupsCountDesc(pagination);
//			case "workshopCount" -> fabberDAO.findAllOrderByWorkshopsCount(pagination);
			default -> Page.empty();
		};

		return fabbers
				.map(fabber -> {
					FabberDTO dto = this.convertToDTO(fabber);
					addNewFieldsToFabberDto(dto, groupMemberDAO.findAllByFabberId(fabber.getId()),
							Optional.empty(), Optional.empty());
					return dto;
				});
	}
	
	@PutMapping("/me/update/{email}")
    @ResponseStatus(HttpStatus.OK)
    public FabberDTO updateMe(@PathVariable String email, @RequestBody FabberDTO fabberDTO) {
        Fabber fabber = fabberDAO.findByEmail(email).get();
        fabber.setIsFabAcademyGrad(fabberDTO.getIsFabAcademyGrad());
        fabber.setFabAcademyGradYear(fabberDTO.getIsFabAcademyGrad() ? fabberDTO.getFabAcademyGradYear() : null);
        fabber.setCountry(fabberDTO.getCountry());

		Fabber persisted = fabberDAO.save(fabber);
        return convertToDTO(persisted);
    }


	
	// ========== DTO conversion ==========
	private FabberDTO convertToDTO(Fabber fabber) {
		return FabberDTO.builder()
				.idFabber(fabber.getId())
				.email(fabber.getEmail())
				.name(fabber.getName())
				.firstName(fabber.getFirstName())
				.lastName(fabber.getLastName())
				.isFabAcademyGrad(fabber.getIsFabAcademyGrad())
				.fabAcademyGradYear(fabber.getFabAcademyGradYear())
				.cellPhoneNumber(fabber.getCellPhoneNumber())
				.isNomade(fabber.getIsNomade())
				.mainQuote(fabber.getMainQuote())
				.city(fabber.getCity())
				.country(fabber.getCountry())
				.weekGoal(fabber.getWeekGoal())
				.avatarUrl(fabber.getAvatarUrl())
				.labId(fabber.getLab() != null ? fabber.getLab().getId() : null)
				.labName(fabber.getLab() != null ? fabber.getLab().getName() : null)
				.generalScore(fabber.getFabberInfo().getScoreGeneral())
				.coordinatorScore(fabber.getFabberInfo().getScoreCoordinator())
				.collaboratorScore(fabber.getFabberInfo().getScoreCollaborator())
				.replicatorScore(fabber.getFabberInfo().getScoreReplicator())
				.authorities(fabber.getRoleFabbers().stream()
						.map(roleFabber -> roleFabber.getRole().getName())
						.toList())
				.build();
	}

	private void addNewFieldsToFabberDto(FabberDTO fabberDTO, List<GroupMember> groupMembers,
										 Optional<Integer> workshopsCount, Optional<Integer> eventsCount) {
		List<GroupLandingDto2> groupLandingDtos = groupMembers.stream().parallel()
				.map(groupMember -> {
					String groupImgUrl = StringUtils.hasText(groupMember.getGroup().getPhotoUrl())
							? "http://res.cloudinary.com/dymje6shc/image/upload/w_220,h_165,c_fit/"
							+ groupMember.getGroup().getPhotoUrl()
							: null;

					return GroupLandingDto2.builder()
							.id(groupMember.getGroup().getId())
							.name(groupMember.getGroup().getName())
							.score(groupStatsService.computeGroupScore(groupMember.getGroup()))
							.membersCount(groupMemberDAO.countDistinctByGroupId(groupMember.getGroup().getId()))
							.imgUrl(groupImgUrl)
							.build();
				})
				.toList();

		fabberDTO.setGroupsJoined(groupLandingDtos);
        workshopsCount.ifPresent(fabberDTO::setWorkshopsCount);
		eventsCount.ifPresent(fabberDTO::setEventsCount);
	}
}
