package de.hka.projekt.objects;

import com.google.gson.annotations.SerializedName;

public class Location {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("coord")
    private double[] coordinates;

    @SerializedName("productClasses")
    private int[] productClasses;

    @SerializedName("properties")
    private LocationProperties properties;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public int[] getProductClasses() {
        return productClasses;
    }

    public LocationProperties getProperties() {
        return properties;
    }
}