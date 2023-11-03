package lat.fab.app.resource.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class GroupLandingDto2 {

    private Integer id;
    private String name;
    private String description;
    private Integer score;
    private List<FabberDTO> members;
    private Integer membersCount;
    private Integer workshopsCount;
    private Integer eventsCount;
    private String imgUrl;
}
