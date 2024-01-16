package de.hka.projekt.objects.bwegtefa;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BwegtEfaCoordResponse {

    @SerializedName("version")
    public String version;

    @SerializedName("locations")
    public List<BWLocation> BWLocations;

    public String getVersion() {
        return version;
    }

    public List<BWLocation> getLocations() {
        return BWLocations;
    }
}
