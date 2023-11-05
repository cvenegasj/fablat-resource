package lat.fab.app.resource.util.fablabs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.IOException;

public enum ActivityStatus {
    ACTIVE, CLOSED, CORONA, EMPTY, PLANNED;

    @JsonValue
    public String toValue() {
        switch (this) {
            case ACTIVE: return "active";
            case CLOSED: return "closed";
            case CORONA: return "corona";
            case EMPTY: return "";
            case PLANNED: return "planned";
        }
        return null;
    }

    @JsonCreator
    public static ActivityStatus forValue(String value) throws IOException {
        if (value.equals("active")) return ACTIVE;
        if (value.equals("closed")) return CLOSED;
        if (value.equals("corona")) return CORONA;
        if (value.equals("")) return EMPTY;
        if (value.equals("planned")) return PLANNED;
        throw new IOException("Cannot deserialize ActivityStatus");
    }
}
