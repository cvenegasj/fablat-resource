package lat.fab.app.resource.util.fablabs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@lombok.Data
public class Fablab {
    @lombok.Getter(onMethod_ = {@JsonProperty("id")})
    @lombok.Setter(onMethod_ = {@JsonProperty("id")})
    private long id;
    @lombok.Getter(onMethod_ = {@JsonProperty("name")})
    @lombok.Setter(onMethod_ = {@JsonProperty("name")})
    private String name;
    @lombok.Getter(onMethod_ = {@JsonProperty("kind_name")})
    @lombok.Setter(onMethod_ = {@JsonProperty("kind_name")})
    private KindName kindName;
    @lombok.Getter(onMethod_ = {@JsonProperty("parent_id")})
    @lombok.Setter(onMethod_ = {@JsonProperty("parent_id")})
    private Long parentID;
    @lombok.Getter(onMethod_ = {@JsonProperty("blurb")})
    @lombok.Setter(onMethod_ = {@JsonProperty("blurb")})
    private String blurb;
    @lombok.Getter(onMethod_ = {@JsonProperty("description")})
    @lombok.Setter(onMethod_ = {@JsonProperty("description")})
    private String description;
    @lombok.Getter(onMethod_ = {@JsonProperty("slug")})
    @lombok.Setter(onMethod_ = {@JsonProperty("slug")})
    private String slug;
    @lombok.Getter(onMethod_ = {@JsonProperty("avatar_url")})
    @lombok.Setter(onMethod_ = {@JsonProperty("avatar_url")})
    private String avatarURL;
    @lombok.Getter(onMethod_ = {@JsonProperty("header_url")})
    @lombok.Setter(onMethod_ = {@JsonProperty("header_url")})
    private String headerURL;
    @lombok.Getter(onMethod_ = {@JsonProperty("address_1")})
    @lombok.Setter(onMethod_ = {@JsonProperty("address_1")})
    private String address1;
    @lombok.Getter(onMethod_ = {@JsonProperty("address_2")})
    @lombok.Setter(onMethod_ = {@JsonProperty("address_2")})
    private String address2;
    @lombok.Getter(onMethod_ = {@JsonProperty("city")})
    @lombok.Setter(onMethod_ = {@JsonProperty("city")})
    private String city;
    @lombok.Getter(onMethod_ = {@JsonProperty("county")})
    @lombok.Setter(onMethod_ = {@JsonProperty("county")})
    private String county;
    @lombok.Getter(onMethod_ = {@JsonProperty("postal_code")})
    @lombok.Setter(onMethod_ = {@JsonProperty("postal_code")})
    private String postalCode;
    @lombok.Getter(onMethod_ = {@JsonProperty("country_code")})
    @lombok.Setter(onMethod_ = {@JsonProperty("country_code")})
    private String countryCode;
    @lombok.Getter(onMethod_ = {@JsonProperty("latitude")})
    @lombok.Setter(onMethod_ = {@JsonProperty("latitude")})
    private Double latitude;
    @lombok.Getter(onMethod_ = {@JsonProperty("longitude")})
    @lombok.Setter(onMethod_ = {@JsonProperty("longitude")})
    private Double longitude;
    @lombok.Getter(onMethod_ = {@JsonProperty("address_notes")})
    @lombok.Setter(onMethod_ = {@JsonProperty("address_notes")})
    private String addressNotes;
    @lombok.Getter(onMethod_ = {@JsonProperty("phone")})
    @lombok.Setter(onMethod_ = {@JsonProperty("phone")})
    private String phone;
    @lombok.Getter(onMethod_ = {@JsonProperty("email")})
    @lombok.Setter(onMethod_ = {@JsonProperty("email")})
    private String email;
    @lombok.Getter(onMethod_ = {@JsonProperty("capabilities")})
    @lombok.Setter(onMethod_ = {@JsonProperty("capabilities")})
    private List<Capability> capabilities;
    @lombok.Getter(onMethod_ = {@JsonProperty("activity_status")})
    @lombok.Setter(onMethod_ = {@JsonProperty("activity_status")})
    private ActivityStatus activityStatus;
    @lombok.Getter(onMethod_ = {@JsonProperty("links")})
    @lombok.Setter(onMethod_ = {@JsonProperty("links")})
    private List<Link> links;
}
