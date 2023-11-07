package lat.fab.app.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkshopTutorDTO {

	private Integer idWorkshopTutor;
	private String name;
	private String firstName;
	private String lastName;
	private String email;
	private Integer fabberId;
	private String fabberAvatarUrl;
}
