package lat.fab.app.resource.util.fablabs;

import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.Data
public class Link {
    @lombok.Getter(onMethod_ = {@JsonProperty("id")})
    @lombok.Setter(onMethod_ = {@JsonProperty("id")})
    private long id;
    @lombok.Getter(onMethod_ = {@JsonProperty("url")})
    @lombok.Setter(onMethod_ = {@JsonProperty("url")})
    private String url;
}
