package lat.fab.app.resource.util.fablabs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.IOException;

public enum KindName {
    FAB_LAB, MINI_FAB_LAB, MOBILE;

    @JsonValue
    public String toValue() {
        switch (this) {
            case FAB_LAB: return "fab_lab";
            case MINI_FAB_LAB: return "mini_fab_lab";
            case MOBILE: return "mobile";
        }
        return null;
    }

    @JsonCreator
    public static KindName forValue(String value) throws IOException {
        if (value.equals("fab_lab")) return FAB_LAB;
        if (value.equals("mini_fab_lab")) return MINI_FAB_LAB;
        if (value.equals("mobile")) return MOBILE;
        throw new IOException("Cannot deserialize KindName");
    }
}
