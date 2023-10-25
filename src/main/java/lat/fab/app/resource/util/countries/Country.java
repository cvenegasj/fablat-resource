package lat.fab.app.resource.util.countries;

import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.Data
public class Country {
    @lombok.Getter(onMethod_ = {@JsonProperty("flags")})
    @lombok.Setter(onMethod_ = {@JsonProperty("flags")})
    private Flags flags;
    @lombok.Getter(onMethod_ = {@JsonProperty("name")})
    @lombok.Setter(onMethod_ = {@JsonProperty("name")})
    private Name name;
    @lombok.Getter(onMethod_ = {@JsonProperty("cca3")})
    @lombok.Setter(onMethod_ = {@JsonProperty("cca3")})
    private String cca3;
}