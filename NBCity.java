package de.hka.projekt.objects.nextbike;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NBCity {
    @SerializedName("places")
    public List<NBPlace> Places;

    @SerializedName("name")
    public String name;

    public List<NBPlace> getPlaces() {
        return Places;
    }

    public String getName() {
        return name;
    }
}
