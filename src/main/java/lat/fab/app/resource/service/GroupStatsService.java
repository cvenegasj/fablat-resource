package lat.fab.app.resource.service;

import lat.fab.app.resource.entities.Group;
import lat.fab.app.resource.repository.GroupMemberDAO;
import lat.fab.app.resource.repository.SubGroupDAO;
import lat.fab.app.resource.repository.WorkshopDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupStatsService {

    private final GroupMemberDAO groupMemberDAO;
    private final SubGroupDAO subGroupDAO;
    private final WorkshopDAO workshopDAO;

    public int computeGroupScore(Group group) {
        int membersCount = groupMemberDAO.countDistinctByGroupId(group.getId());
        int subgroupsCount = subGroupDAO.countDistinctByGroupId(group.getId());
        int eventsCount = workshopDAO.countDistinctBySubGroup_GroupId(group.getId());

        return (5 * membersCount) + (3 * subgroupsCount) + (eventsCount);
    }
}
