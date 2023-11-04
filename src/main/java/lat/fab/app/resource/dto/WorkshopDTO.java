package lat.fab.app.resource.dto;

import lat.fab.app.resource.entities.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkshopDTO {

	private Integer idWorkshop;
	private EventType type;
	private Integer replicationNumber;
	private String name;
	private String description;
	private String startDate;
	private String startTime;
	private String endDate;
	private String endTime;
	private Integer startDateDay;
	private String startDateMonth;
	private String startDateFormatted;
	private String endDateFormatted;
	private String startDateTimeISO;
	private String endDateTimeISO;
	private String startDateTimeCalendar;
	private String endDateTimeCalendar;
	private Boolean isPaid;
	private BigDecimal price;
	private String currency;
	private String facebookUrl;
	private String ticketsUrl;
	private String creationDateTime;
	private Boolean enabled;
	private Integer locationId;
	private String locationAddress;
	private String locationCity;
	private String locationCountry;
	private String locationLatitude;
	private String locationLongitude;
	private String labName;
	private Integer subGroupId;
	private String subGroupName;
	private Integer groupId;
	private String groupName;
	private List<WorkshopTutorDTO> tutors;
	// additional
	private Boolean amITutor;
}
