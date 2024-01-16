package de.hka.projekt.objects.geocode;

import com.google.gson.annotations.SerializedName;

public class GeoCodeCoordResponse {

    @SerializedName("display_name")
    public String address;

    public String getAddress() {return address;}
}
