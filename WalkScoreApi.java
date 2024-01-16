package de.hka.projekt.network;

import de.hka.projekt.objects.walkscore.WalkScoreCoordResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WalkScoreApi {

    @GET("score?format=json")
    Call<WalkScoreCoordResponse> loadWalkScore(@Query("address") String address, @Query("lat") double latitude, @Query("lon") double longitude, @Query("transit") int transit, @Query("bike") int bike, @Query("wsapikey") String wsapikey);

}
