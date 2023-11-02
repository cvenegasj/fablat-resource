package lat.fab.app.resource.service;

import lat.fab.app.resource.entities.Fabber;
import lat.fab.app.resource.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserStatsService {

    private final FabberDAO fabberDAO;
    private final GroupDAO groupDAO;
    private final GroupMemberDAO groupMemberDAO;
    private final SubGroupMemberDAO subGroupMemberDAO;
    private final WorkshopTutorDAO workshopTutorDAO;
    private final FabLabsIoRepository fabLabsIoRepository;


    public Mono<Map<String, String>> getGeneralStatsLanding() {

        return fabLabsIoRepository.count()
                .map(labsCount -> Map.of(
                        "usersCount", String.valueOf(fabberDAO.count()),
                        "groupsCount", String.valueOf(groupDAO.count()),
                        "labsCount", String.valueOf(labsCount)));
    }

    public Fabber updateFabberScores(Fabber user) {
        int generalScore = computeFabberGeneralScore(user.getEmail());
        int coordinatorScore = computeFabberCoordinatorScore(user.getEmail());
        int collaboratorScore = computeFabberCollaboratorScore(user.getEmail());
        int replicatorScore = computeFabberReplicatorScore(user.getEmail());

        user.getFabberInfo().setScoreGeneral(generalScore);
        user.getFabberInfo().setScoreCoordinator(coordinatorScore);
        user.getFabberInfo().setScoreCollaborator(collaboratorScore);
        user.getFabberInfo().setScoreReplicator(replicatorScore);

        return fabberDAO.save(user);
    }

    private int computeFabberGeneralScore(String email) {
        return computeFabberCoordinatorScore(email)
                + computeFabberCollaboratorScore(email)
                + computeFabberReplicatorScore(email);
    }

    private int computeFabberCoordinatorScore(String email) {
        int groupsAsCoordinator = groupMemberDAO.countByFabberEmailAndIsCoordinatorIs(email, true);
        int subgroupsAsCoordinator =
                subGroupMemberDAO.countByGroupMember_Fabber_EmailAndIsCoordinatorIs(email, true);

       return groupsAsCoordinator + subgroupsAsCoordinator;
    }

    private int computeFabberCollaboratorScore(String email) {
        int groupsAsMember = groupMemberDAO.countByFabberEmailAndIsCoordinatorIs(email, false);
        int subgroupsAsMember =
                subGroupMemberDAO.countByGroupMember_Fabber_EmailAndIsCoordinatorIs(email, false);

        return groupsAsMember + subgroupsAsMember;
    }

    private int computeFabberReplicatorScore(String email) {
        int workshopsAsTutor = workshopTutorDAO.countBySubGroupMember_GroupMember_FabberEmail(email);

        return workshopsAsTutor;
    }

}
