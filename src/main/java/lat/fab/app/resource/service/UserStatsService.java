package lat.fab.app.resource.service;

import lat.fab.app.resource.entities.EventType;
import lat.fab.app.resource.entities.Fabber;
import lat.fab.app.resource.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserStatsService {

    private final FabberDAO fabberDAO;
    private final GroupMemberDAO groupMemberDAO;
    private final SubGroupMemberDAO subGroupMemberDAO;
    private final WorkshopTutorDAO workshopTutorDAO;
    private final SubGroupDAO subGroupDAO;
    private final WorkshopDAO workshopDAO;

    public Fabber updateFabberScores(Fabber user) {
        int coordinatorScore = computeFabberCoordinatorScore(user.getEmail());
        int collaboratorScore = computeFabberCollaboratorScore(user.getEmail());
        int replicatorScore = computeFabberReplicatorScore(user.getEmail());
        int generalScore = computeFabberGeneralScore(user.getEmail());

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

    /**
     * G = set of groups as coordinator
     * SG = set of subgroups of all groups in G
     * EG = set of events of all subgroups in SG
     * S = set of subgroups as coordinator
     * ES = set of events of all subgroups in S
     * E = set of events as creator
     *
     * @param email
     * @return general score
     */
    private int computeFabberGeneralScore(String email) {
        int G = groupMemberDAO.countByFabberEmailAndIsCoordinatorIs(email, true);
        int SG = subGroupDAO.countDistinctByGroup_GroupMembers_Fabber_EmailAndGroup_GroupMembers_IsCoordinator(email, true);
        int EG = workshopDAO.countDistinctBySubGroup_Group_GroupMembers_FabberEmailAndSubGroup_Group_GroupMembers_IsCoordinator(email, true);

        int S = subGroupMemberDAO.countByGroupMember_Fabber_EmailAndIsCoordinatorIs(email, true);
        int ES = workshopDAO.countDistinctBySubGroup_SubGroupMembers_GroupMember_Fabber_EmailAndSubGroup_SubGroupMembers_IsCoordinator(email, true);
        int E = workshopTutorDAO.countBySubGroupMember_GroupMember_FabberEmail_AndWorkshopTypeIsIn(
                email, List.of(EventType.WORKSHOP, EventType.CONFERENCE, EventType.OTHER));

        return (1000 * G) + (50 * SG) + (2 * EG) + (150 * S) + (8 * ES) + (30 * E);
    }
}
