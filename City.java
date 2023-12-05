package de.hka.projekt.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class City {
    @SerializedName("countries")
    public List<Place> places;

    public List<Place> getCountries() {
        return places;
    }
}
