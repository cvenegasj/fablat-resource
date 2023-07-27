package lat.fab.app.resource.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupDTO {

	private Integer idGroup;
	private String name;
	private String description;
	private String reunionDay;
	private String reunionTime;
	private String mainUrl;
	private String secondaryUrl;
	private String symbioUrl;
	private String photoUrl;
	private String creationDateTime;
	private List<SubGroupDTO> subGroups;
	private List<GroupMemberDTO> members;
	// additional
	private Integer membersCount;
	private Integer subGroupsCount;
	private Boolean amIMember;
	private Boolean amICoordinator;
}
