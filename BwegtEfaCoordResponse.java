package de.hka.projekt.objects;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BwegtEfaCoordResponse {

    @SerializedName("version")
    public String version;

    @SerializedName("locations")
    public List<Location> locations;

    public String getVersion() {
        return version;
    }

    public List<Location> getLocations() {
        return locations;
    }
}
