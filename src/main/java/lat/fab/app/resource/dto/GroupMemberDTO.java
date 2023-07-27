package lat.fab.app.resource.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupMemberDTO {

	private Integer idGroupMember;
	private String name;
	private String firstName;
	private String lastName;
	private String email;
	private Boolean isCoordinator;
	private Boolean notificationEnabled;
	private String creationDateTime;
	private Integer fabberId;
}
