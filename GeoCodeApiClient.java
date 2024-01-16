package de.hka.projekt.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeoCodeApiClient {

    private static GeoCodeApiClient instance;

    private Retrofit retrofit;

    public static GeoCodeApiClient getInstance() {
        if (instance == null) {
            instance = new GeoCodeApiClient();
        }

        return instance;
    }

    public GeoCodeApiClient() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("https://geocode.maps.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    public GeoCodeApi getClient() {
        return this.retrofit.create(GeoCodeApi.class);
    }
}
