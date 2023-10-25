package lat.fab.app.resource.util.countries;

import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.Data
public class Flags {
    @lombok.Getter(onMethod_ = {@JsonProperty("png")})
    @lombok.Setter(onMethod_ = {@JsonProperty("png")})
    private String png;
    @lombok.Getter(onMethod_ = {@JsonProperty("svg")})
    @lombok.Setter(onMethod_ = {@JsonProperty("svg")})
    private String svg;
    @lombok.Getter(onMethod_ = {@JsonProperty("alt")})
    @lombok.Setter(onMethod_ = {@JsonProperty("alt")})
    private String alt;
}