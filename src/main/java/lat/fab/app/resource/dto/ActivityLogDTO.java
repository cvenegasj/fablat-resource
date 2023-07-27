package lat.fab.app.resource.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityLogDTO {

	private Integer idActivityLog;
	private String level;
	private String type;
	private String visibility;
	private String creationDateTime;
	private Integer fabberId;
	private String fabberName;
	private String fabberFirstName;
	private String fabberLastName;
	private Integer groupId;
	private String groupName;
	private Integer subGroupId;
	private String subGroupName;
	// additional
	private LocalDateTime creationDateTimeRaw;


}
