package lat.fab.app.resource.service;

import lat.fab.app.resource.entities.EventType;
import lat.fab.app.resource.entities.Fabber;
import lat.fab.app.resource.repository.FabberDAO;
import lat.fab.app.resource.repository.GroupMemberDAO;
import lat.fab.app.resource.repository.SubGroupMemberDAO;
import lat.fab.app.resource.repository.WorkshopTutorDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserStatsService {

    private final FabberDAO fabberDAO;
    private final GroupMemberDAO groupMemberDAO;
    private final SubGroupMemberDAO subGroupMemberDAO;
    private final WorkshopTutorDAO workshopTutorDAO;


    public Fabber updateFabberScores(Fabber user) {
        int coordinatorScore = computeFabberCoordinatorScore(user.getEmail());
        int collaboratorScore = computeFabberCollaboratorScore(user.getEmail());
        int replicatorScore = computeFabberReplicatorScore(user.getEmail());
        int generalScore = coordinatorScore + collaboratorScore + replicatorScore;

        user.getFabberInfo().setScoreGeneral(generalScore);
        user.getFabberInfo().setScoreCoordinator(coordinatorScore);
        user.getFabberInfo().setScoreCollaborator(collaboratorScore);
        user.getFabberInfo().setScoreReplicator(replicatorScore);

        return fabberDAO.save(user);
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
        int workshopsAsTutor = workshopTutorDAO.countBySubGroupMember_GroupMember_FabberEmail_AndWorkshopTypeIsIn(
                email, List.of(EventType.WORKSHOP, EventType.CONFERENCE, EventType.OTHER));

        return workshopsAsTutor;
    }

}
