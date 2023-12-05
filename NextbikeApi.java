package de.hka.projekt.network;

import de.hka.projekt.objects.NextbikeCoordResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NextbikeApi {
    @GET("")
    Call<NextbikeCoordResponse> loadBikesWithinRadius(@Query("coord") String coordinateString, @Query("radius_1") int radius);
}
