package lat.fab.app.resource.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GroupLandingDto {

    private Integer id;
    private String name;
    private String avatarUrl;
}
