package lat.fab.app.resource.util.fablabs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.IOException;

public enum Capability {
    CIRCUIT_PRODUCTION, CNC_MILLING, LASER, PRECISION_MILLING, THREE_D_PRINTING, VINYL_CUTTING;

    @JsonValue
    public String toValue() {
        switch (this) {
            case CIRCUIT_PRODUCTION: return "circuit_production";
            case CNC_MILLING: return "cnc_milling";
            case LASER: return "laser";
            case PRECISION_MILLING: return "precision_milling";
            case THREE_D_PRINTING: return "three_d_printing";
            case VINYL_CUTTING: return "vinyl_cutting";
        }
        return null;
    }

    @JsonCreator
    public static Capability forValue(String value) throws IOException {
        if (value.equals("circuit_production")) return CIRCUIT_PRODUCTION;
        if (value.equals("cnc_milling")) return CNC_MILLING;
        if (value.equals("laser")) return LASER;
        if (value.equals("precision_milling")) return PRECISION_MILLING;
        if (value.equals("three_d_printing")) return THREE_D_PRINTING;
        if (value.equals("vinyl_cutting")) return VINYL_CUTTING;
        throw new IOException("Cannot deserialize Capability");
    }
}
