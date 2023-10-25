package lat.fab.app.resource.util.countries;

import com.fasterxml.jackson.annotation.*;

@lombok.Data
public class NativeName {
    @lombok.Getter(onMethod_ = {@JsonProperty("official")})
    @lombok.Setter(onMethod_ = {@JsonProperty("official")})
    private String official;
    @lombok.Getter(onMethod_ = {@JsonProperty("common")})
    @lombok.Setter(onMethod_ = {@JsonProperty("common")})
    private String common;
}