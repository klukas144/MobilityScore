package de.hka.projekt.objects.nextbike;

import com.google.gson.annotations.SerializedName;

public class NBPlace {

    @SerializedName("uid")
    public int uid;

    @SerializedName("lat")
    public double lat;

    @SerializedName("lng")
    public double lng;

    @SerializedName("bike")
    public boolean bike;

    @SerializedName("spot")
    public boolean spot;

    @SerializedName("name")
    public String name;

    @SerializedName("dist")
    public double dist;

    @SerializedName("bikes")
    public int bikes;



    public int getUid() {
        return uid;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public boolean isBike() {
        return bike;
    }

    public boolean isSpot() {
        return spot;
    }

    public String getName() {
        return name;
    }

    public double getDist() {return dist;}

    public int getBikes() {return bikes;}
}
