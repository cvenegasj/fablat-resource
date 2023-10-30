package lat.fab.app.resource.controller;

import lat.fab.app.resource.dto.FabberDTO;
import lat.fab.app.resource.dto.GroupLandingDto2;
import lat.fab.app.resource.entities.Fabber;
import lat.fab.app.resource.entities.FabberInfo;
import lat.fab.app.resource.entities.GroupMember;
import lat.fab.app.resource.entities.RoleFabber;
import lat.fab.app.resource.repository.*;
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
	private final LabDAO labDAO;
	private final GroupMemberDAO groupMemberDAO;
	private final SubGroupMemberDAO subGroupMemberDAO;
	private final WorkshopTutorDAO workshopTutorDAO;
	private final RoleDAO roleDAO;

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
            return convertToDTO(persisted);
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
	
	@GetMapping("/me/general/{email}")
	@ResponseStatus(HttpStatus.OK)
	public FabberDTO getMyGeneralInfo(@PathVariable String email) {
		// update the scores info 
		calculateAndUpdateScores(email);
		// user logged in with email as username
		Fabber fabber = fabberDAO.findByEmail(email).get();
		return convertToDTO(fabber);
	}
	
	private void calculateAndUpdateScores(String email) {
		Integer replicatorScore = workshopTutorDAO.countBySubGroupMember_GroupMember_FabberEmail(email);
		Integer collaboratorScore = groupMemberDAO.countByFabberEmailAndIsCoordinatorIs(email, false)
										+ subGroupMemberDAO.countByGroupMember_Fabber_EmailAndIsCoordinatorIs(email, false);
		Integer coordinatorScore = groupMemberDAO.countByFabberEmailAndIsCoordinatorIs(email, true)
										+ subGroupMemberDAO.countByGroupMember_Fabber_EmailAndIsCoordinatorIs(email, true);
		Integer generalScore = replicatorScore + collaboratorScore + coordinatorScore;
		
		Fabber fabber = fabberDAO.findByEmail(email).get();
		fabber.getFabberInfo().setScoreGeneral(generalScore);
		fabber.getFabberInfo().setScoreCoordinator(coordinatorScore);
		fabber.getFabberInfo().setScoreCollaborator(collaboratorScore);
		fabber.getFabberInfo().setScoreReplicator(replicatorScore);
		
		fabberDAO.save(fabber);
	}
		
	@GetMapping("/me/profile/{email}")
	@ResponseStatus(HttpStatus.OK)
	public FabberDTO getMyProfile(@PathVariable String email) {
		Fabber fabber = fabberDAO.findByEmail(email).get();
		FabberDTO fabberDTO = convertToDTO(fabber);
		return fabberDTO;
	}
	
	@GetMapping(value = "/{idFabber}")
	@ResponseStatus(HttpStatus.OK)
    public FabberDTO findOneLandind(@PathVariable("idFabber") Integer idFabber) {
        return fabberDAO.findById(idFabber)
				.map(fabber -> {
					FabberDTO dto = this.convertToDTO(fabber);
					addNewFieldsToFabberDto(dto, groupMemberDAO.findAllByFabberId(fabber.getId()));
					return dto;
				}).orElse(null);
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
					addNewFieldsToFabberDto(dto, groupMemberDAO.findAllByFabberId(fabber.getId()));
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
					FabberDTO dto = this.convertToDTO(fabber);
					addNewFieldsToFabberDto(dto, groupMemberDAO.findAllByFabberId(fabber.getId()));
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
					addNewFieldsToFabberDto(dto, groupMemberDAO.findAllByFabberId(fabber.getId()));
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

	// TODO: remove later
	@GetMapping("/count")
	@ResponseStatus(HttpStatus.OK)
	public long countAll() {
		return fabberDAO.count();
	}

	@GetMapping("/set-country-all")
	@ResponseStatus(HttpStatus.OK)
	public void setCountryForAll() {
		fabberDAO.findAll()
				.forEach(fabber -> {
					fabber.setCountry("PER");
					fabberDAO.save(fabber);
				});
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

	private void addNewFieldsToFabberDto(FabberDTO fabberDTO, List<GroupMember> groupMembers) {
		List<GroupLandingDto2> groupLandingDtos = groupMembers.stream()
				.map(groupMember -> {
					String groupImgUrl = StringUtils.hasText(groupMember.getGroup().getPhotoUrl())
							? "http://res.cloudinary.com/dymje6shc/image/upload/w_220,h_165,c_fit/"
							+ groupMember.getGroup().getPhotoUrl()
							: null;

					return GroupLandingDto2.builder()
							.id(groupMember.getGroup().getId())
							.name(groupMember.getGroup().getName())
							.imgUrl(groupImgUrl)
							.build();
				})
				.toList();
		fabberDTO.setGroupsJoined(groupLandingDtos);
	}
}
