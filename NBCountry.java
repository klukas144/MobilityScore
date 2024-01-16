package de.hka.projekt.objects.nextbike;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NBCountry {
    @SerializedName("cities")
    public List<NBCity> cities;

    public List<NBCity> getCities() {
        return cities;
    }
}
