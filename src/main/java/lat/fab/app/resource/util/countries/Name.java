package lat.fab.app.resource.util.countries;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@lombok.Data
public class Name {
    @lombok.Getter(onMethod_ = {@JsonProperty("common")})
    @lombok.Setter(onMethod_ = {@JsonProperty("common")})
    private String common;
    @lombok.Getter(onMethod_ = {@JsonProperty("official")})
    @lombok.Setter(onMethod_ = {@JsonProperty("official")})
    private String official;
    @lombok.Getter(onMethod_ = {@JsonProperty("nativeName")})
    @lombok.Setter(onMethod_ = {@JsonProperty("nativeName")})
    private Map<String, NativeName> nativeName;
}