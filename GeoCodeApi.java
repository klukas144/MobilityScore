package de.hka.projekt.network;

import de.hka.projekt.objects.geocode.GeoCodeCoordResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeoCodeApi  {

    @GET("reverse?")
    Call<GeoCodeCoordResponse> loadAddress(@Query("lat") double latitude, @Query("lon") double longitude, @Query("api_key") String gcapikey);

}
