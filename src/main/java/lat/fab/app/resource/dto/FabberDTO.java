package lat.fab.app.resource.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class FabberDTO {

	private Integer idFabber;
	private String email;
//	private String password;
	private String name;
	private String firstName;
	private String lastName;
	private Boolean isFabAcademyGrad;
	private Integer fabAcademyGradYear;
	private String cellPhoneNumber;
	private Boolean isNomade;
	private String mainQuote;
	private String city;
	private String country;
	private String weekGoal;
	private String avatarUrl;
	private Integer labId;
	private String labName;
	// additional
	private Integer generalScore;
	private Integer coordinatorScore;
	private Integer collaboratorScore;
	private Integer replicatorScore;

	private List<String> authorities;

	// for new landing
	private List<GroupLandingDto> groupsJoined;
}
