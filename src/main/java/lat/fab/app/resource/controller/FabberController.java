package lat.fab.app.resource.controller;

import lat.fab.app.resource.dto.FabberDTO;
import lat.fab.app.resource.entities.Fabber;
import lat.fab.app.resource.entities.FabberInfo;
import lat.fab.app.resource.entities.RoleFabber;
import lat.fab.app.resource.repository.*;
import lat.fab.app.resource.util.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

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

	@RequestMapping(value = "/createOrUpdateUser", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public FabberDTO createOrUpdateUser(@AuthenticationPrincipal JwtAuthenticationToken jwtAuthToken) {
		log.info("createOrUpdateUser(), principal.email: {}", jwtAuthToken.getTokenAttributes().get("email"));

        final Fabber retrievedUser = fabberDAO.findByEmail(
				(String) jwtAuthToken.getTokenAttributes().get("email"));

        if (retrievedUser != null) { // if user exists in database, update it
            retrievedUser.setName((String) jwtAuthToken.getTokenAttributes().get("name"));
            retrievedUser.setFirstName((String) jwtAuthToken.getTokenAttributes().get("given_name"));
            retrievedUser.setLastName((String) jwtAuthToken.getTokenAttributes().get("family_name"));
            retrievedUser.setAvatarUrl((String) jwtAuthToken.getTokenAttributes().get("picture"));

            Fabber persisted = fabberDAO.save(retrievedUser);
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
            roleFabber.setRole(roleDAO.findByName(Resources.ROLE_USER));
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
	
	@RequestMapping(value = "/me/general/{email}", method = RequestMethod.GET)
	public FabberDTO getMyGeneralInfo(@PathVariable String email) {
		// update the scores info 
		calculateAndUpdateScores(email);
		// user logged in with email as username
		Fabber fabber = fabberDAO.findByEmail(email);
		return convertToDTO(fabber);
	}
	
	private void calculateAndUpdateScores(String email) {
		Integer replicatorScore = workshopTutorDAO.countByFabberEmail(email);
		Integer collaboratorScore = groupMemberDAO.countByFabberEmailAndIsCoordinatorIs(email, false)
										+ subGroupMemberDAO.countByFabberEmailAndIsCoordinatorIs(email, false);
		Integer coordinatorScore = groupMemberDAO.countByFabberEmailAndIsCoordinatorIs(email, true)
										+ subGroupMemberDAO.countByFabberEmailAndIsCoordinatorIs(email, true);
		Integer generalScore = replicatorScore + collaboratorScore + coordinatorScore;
		
		Fabber fabber = fabberDAO.findByEmail(email);
		fabber.getFabberInfo().setScoreGeneral(generalScore);
		fabber.getFabberInfo().setScoreCoordinator(coordinatorScore);
		fabber.getFabberInfo().setScoreCollaborator(collaboratorScore);
		fabber.getFabberInfo().setScoreReplicator(replicatorScore);
		
		fabberDAO.save(fabber);
	}
		
	@RequestMapping(value = "/me/profile/{email}", method = RequestMethod.GET)
	public FabberDTO getMyProfile(@PathVariable String email) {
		Fabber fabber = fabberDAO.findByEmail(email);
		FabberDTO fabberDTO = convertToDTO(fabber);
		return fabberDTO;
	}
	
	@RequestMapping(value = "/{idFabber}", method = RequestMethod.GET)
    public FabberDTO findOne(@PathVariable("idFabber") Integer idFabber) {
        return convertToDTO(fabberDAO.findById(idFabber).get());
    }
	
	@RequestMapping(value = "/me/update/{email}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public FabberDTO updateMe(@PathVariable String email, @RequestBody FabberDTO fabberDTO) {
        Fabber fabber = fabberDAO.findByEmail(email);
        fabber.setFirstName(fabberDTO.getFirstName());
        fabber.setLastName(fabberDTO.getLastName());
        fabber.setIsFabAcademyGrad(fabberDTO.getIsFabAcademyGrad());
        fabber.setFabAcademyGradYear(fabberDTO.getIsFabAcademyGrad() ? fabberDTO.getFabAcademyGradYear() : null);
        fabber.setCity(fabberDTO.getCity());
        fabber.setCountry(fabberDTO.getCountry());
        fabber.setMainQuote(fabberDTO.getMainQuote());
        fabber.setWeekGoal(fabberDTO.getWeekGoal());
        		
		// lab
		if (fabberDTO.getLabId() != null) {
			fabber.setLab(labDAO.findById(fabberDTO.getLabId()).get());
			fabber.setIsNomade(false);
		} else {
			fabber.setLab(null);
			fabber.setIsNomade(true);
		}

		Fabber persisted = fabberDAO.save(fabber);
        return convertToDTO(persisted);
    }
	
	// ========== DTO conversion ==========
	private FabberDTO convertToDTO(Fabber fabber) {		
		FabberDTO fabberDTO = new FabberDTO();
		fabberDTO.setIdFabber(fabber.getId());
		fabberDTO.setEmail(fabber.getEmail());
		fabberDTO.setName(fabber.getName());
		fabberDTO.setFirstName(fabber.getFirstName());
		fabberDTO.setLastName(fabber.getLastName());
		fabberDTO.setIsFabAcademyGrad(fabber.getIsFabAcademyGrad());
		fabberDTO.setFabAcademyGradYear(fabber.getFabAcademyGradYear());
		fabberDTO.setCellPhoneNumber(fabber.getCellPhoneNumber());
		fabberDTO.setIsNomade(fabber.getIsNomade());
		fabberDTO.setMainQuote(fabber.getMainQuote());
		fabberDTO.setCity(fabber.getCity());
		fabberDTO.setCountry(fabber.getCountry());
		fabberDTO.setWeekGoal(fabber.getWeekGoal());
		fabberDTO.setAvatarUrl(fabber.getAvatarUrl());
		fabberDTO.setLabId(fabber.getLab() != null ? fabber.getLab().getId() : null);
		fabberDTO.setLabName(fabber.getLab() != null ? fabber.getLab().getName() : null);
		fabberDTO.setGeneralScore(fabber.getFabberInfo().getScoreGeneral());
		fabberDTO.setCoordinatorScore(fabber.getFabberInfo().getScoreCoordinator());
		fabberDTO.setCollaboratorScore(fabber.getFabberInfo().getScoreCollaborator());
		fabberDTO.setReplicatorScore(fabber.getFabberInfo().getScoreReplicator());
		fabberDTO.setAuthorities(fabber.getRoleFabbers().stream()
				.map(roleFabber -> roleFabber.getRole().getName())
				.collect(Collectors.toList()));

	    return fabberDTO;
	}
}
