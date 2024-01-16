package de.hka.projekt.network;

import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NextbikeApiClient {

    private static NextbikeApiClient instance;

    private Retrofit retrofit;

    public static NextbikeApiClient getInstance() {
        if (instance == null) {
            instance = new NextbikeApiClient();
        }

        return instance;
    }

    public NextbikeApiClient() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("https://api.nextbike.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    public NextbikeApi getClient() {
        return this.retrofit.create(NextbikeApi.class);
    }
}
