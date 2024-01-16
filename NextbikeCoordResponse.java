package de.hka.projekt.objects.nextbike;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NextbikeCoordResponse {
    @SerializedName("countries")
    public List<NBCountry> countries;

    public List<NBCountry> getCountries() {
        return countries;
    }
}
