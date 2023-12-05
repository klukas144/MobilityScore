package de.hka.projekt.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NextbikeCoordResponse {
    @SerializedName("countries")
    public List<Country> countries;

    public List<Country> getCountries() {
        return countries;
    }
}
