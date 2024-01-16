package de.hka.projekt.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WalkScoreApiClient {

    private static WalkScoreApiClient instance;

    private Retrofit retrofit;

    public static WalkScoreApiClient getInstance() {
        if (instance == null) {
            instance = new WalkScoreApiClient();
        }

        return instance;
    }

    public WalkScoreApiClient() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("https://api.walkscore.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    public WalkScoreApi getClient() {
        return this.retrofit.create(WalkScoreApi.class);
    }
}
