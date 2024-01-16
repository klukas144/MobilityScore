package de.hka.projekt.objects.walkscore;

import com.google.gson.annotations.SerializedName;

public class WalkScoreCoordResponse {

    @SerializedName("walkscore")
    public int walkScore;

    @SerializedName("transit")
    public WSTransit transit;

    @SerializedName("bike")
    public WSBike bike;

    public int getWalkScore() {return walkScore;}

    public WSTransit getTransit() {return transit;}

    public WSBike getBike() {return bike;}
}
