package lat.fab.app.resource.util;

public class Constants {

	// Roles
	public static final String ROLE_ADMIN_GENERAL = "ROLE_ADMIN_GENERAL";
	public static final String ROLE_ADMIN_LAB = "ROLE_ADMIN_LAB";
	public static final String ROLE_USER = "ROLE_USER";
	
	// Activity levels
	public static final String ACTIVITY_LEVEL_GROUP = "GROUP";
	public static final String ACTIVITY_LEVEL_SUBGROUP = "SUBGROUP";
		
	// Activity types
	public static final String ACTIVITY_TYPE_ORIGIN = "ORIGIN";
	public static final String ACTIVITY_TYPE_USER_JOINED = "USER_JOINED";
	public static final String ACTIVITY_TYPE_USER_LEFT = "USER_LEFT";
	public static final String ACTIVITY_TYPE_USER_DELETED = "USER_DELETED";
	public static final String ACTIVITY_TYPE_SUBGROUP_CREATED = "SUBGROUP_CREATED";
	public static final String ACTIVITY_TYPE_SUBGROUP_DELETED = "SUBGROUP_DELETED";
	public static final String ACTIVITY_TYPE_WORKSHOP_CREATED = "WORKSHOP_CREATED";
	public static final String ACTIVITY_TYPE_WORKSHOP_DELETED = "WORKSHOP_DELETED";
	public static final String ACTIVITY_TYPE_COLOR_CHANGED = "COLOR_CHANGED";

	public static final String FABLABS_IO_API_URL = "https://api.fablabs.io/";
	
	// Activity visibilities
	public static final String ACTIVITY_VISIBILITY_EXTERNAL = "EXTERNAL"; // app-wide visibility 
	public static final String ACTIVITY_VISIBILITY_INTERNAL = "INTERNAL"; // group or subgroup internal visibility

	// public static final List<Country> countriesList = CountriesJsonParser.fromJsonFileToList();

	// public static final Map<String, Country> countriesMap = CountriesJsonParser.fromListToMap(countriesList);

	public static final String LIMA_ZONE_ID = "America/Lima";
}
