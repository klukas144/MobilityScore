package de.hka.projekt.objects.bwegtefa;

import com.google.gson.annotations.SerializedName;

public class BWLocation {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("coord")
    private double[] coordinates;

    @SerializedName("productClasses")
    private int[] productClasses;

    @SerializedName("properties")
    private BWLocationProperties properties;

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

    public BWLocationProperties getProperties() {
        return properties;
    }
}