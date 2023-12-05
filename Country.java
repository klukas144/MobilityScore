package de.hka.projekt.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Country {
    @SerializedName("countries")
    public List<City> cities;

    public List<City> getCities() {
        return cities;
    }
}
