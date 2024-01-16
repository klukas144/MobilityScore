package de.hka.projekt.network;

import de.hka.projekt.objects.nextbike.NextbikeCoordResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NextbikeApi {
    @GET("maps/nextbike-live.json?")
    Call<NextbikeCoordResponse> loadBikesWithinRadius(@Query("lat") double latitude, @Query("lng") double longitude, @Query("distance") int radius);
}
