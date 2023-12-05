package de.hka.projekt.network;

import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BwegtEfaApiClient {

    private static BwegtEfaApiClient instance;

    private Retrofit retrofit;

    public static BwegtEfaApiClient getInstance() {
        if (instance == null) {
            instance = new BwegtEfaApiClient();
        }

        return instance;
    }

    public BwegtEfaApiClient() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("https://www.bwegt.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public String createCoordinateString(double latitude, double longitude) {
        return String.format(Locale.ENGLISH, "%f:%f:WGS84[dd.ddddd]", longitude, latitude);
    }

    public BwegtEfaApi getClient() {
        return this.retrofit.create(BwegtEfaApi.class);
    }
}