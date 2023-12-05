package de.hka.projekt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import de.hka.projekt.network.BwegtEfaApiClient;
import de.hka.projekt.objects.BwegtEfaCoordResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private TextView txtStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //was halt alles gemacht wird, wenn man die Activity öffnet
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        txtStop = this.findViewById(R.id.txtStop);


        ImageButton btn_home = this.findViewById(R.id.btnHome);
        btn_home.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        });

        XYTileSource mapServer = new XYTileSource(
                "MapName",
                8,
                20,
                256,
                ".png",
                new String[]{"https://tileserver.svprod01.app/styles/default/"}
        );

        String authorizationString = this.getMapServerAuthorizationString(
                "ws2223@hka",
                "LeevwBfDi#2027"
        );

        Configuration
                .getInstance()
                .getAdditionalHttpRequestProperties()
                .put("Authorization", authorizationString);


        this.mapView = this.findViewById(R.id.mapView);
        this.mapView.setTileSource(mapServer);

        GeoPoint startPoint = new GeoPoint(49.4734517, 8.6215506);

        IMapController mapController = mapView.getController();
        mapController.setCenter(startPoint);
        mapController.setZoom(16.0);

        this.mapView.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) { //wenn man die Karte verschiebt werden die entsprechend neuen nächsten Haltestellen geladen
                loadClosestStops(mapView.getMapCenter().getLatitude(), mapView.getMapCenter().getLongitude());
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) { //selbiges wie oben bloß bei Zoomen
                loadClosestStops(mapView.getMapCenter().getLatitude(), mapView.getMapCenter().getLongitude());
                return false;
            }
        });
    }

    @Override
    protected void onResume() { //wenn die Activity weiterhin/wieder im Vordergrund ist
        super.onResume();

        String[] permissions = new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() { //wenn berechtigungen vorhanden, dann auch auf Standort zugreifen
                initLocationListener();
                Log.d("MapActivity", "onGranted");
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) { //wenn nicht, dann meckern
                super.onDenied(context, deniedPermissions);
                Log.d("MapActivity", "onDenied");
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void initLocationListener() { //Standort Verfolgung initialisieren
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) { //wenn Standort geändert, dann Karte neu zentrieren
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                GeoPoint startPoint = new GeoPoint(latitude, longitude);

                IMapController mapController = mapView.getController();
                mapController.setCenter(startPoint);
            }
        };

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }
    private String getMapServerAuthorizationString(String username, String password) //einfach nur die Autorisierung für den MapServer
    {
        String authorizationString = String.format("%s:%s", username, password);
        return "Basic " + Base64.encodeToString(authorizationString.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
    }

    private void loadClosestStops(double latitude, double longitude) { //näheste Haltestellen laden
        Call<BwegtEfaCoordResponse> efaCall = BwegtEfaApiClient
                .getInstance()
                .getClient()
                .loadStopsWithinRadius(
                        BwegtEfaApiClient
                                .getInstance()
                                .createCoordinateString(
                                        latitude,
                                        longitude
                                ),
                        2000
                );

        efaCall.enqueue(new Callback<BwegtEfaCoordResponse>() {
            @Override
            public void onResponse(Call<BwegtEfaCoordResponse> call, Response<BwegtEfaCoordResponse> response) { //falls wirklich Haltestellen kommen, dann ausgeben
                BwegtEfaCoordResponse efaCoordResponse = response.body();
                List<de.hka.projekt.objects.Location> locationList = efaCoordResponse.getLocations();
                txtStop.setText("Nächste Haltestelle:\n" + locationList.get(0).getName() + " (" + Math.round(locationList.get(0).getProperties().getDistance()) + "m)");
                Log.d("MapActivity", String.format("Response %d Locations", locationList.size()));

                for(de.hka.projekt.objects.Location loc : locationList) {
                    Log.d("MapActivity", String.format("Location: %s", loc.getName()));
                }

            }

            @Override
            public void onFailure(Call<BwegtEfaCoordResponse> call, Throwable t) { //wenn nicht, dann eben nicht
                Log.d("MapActivity", "Failure");
            }
        });
    }
}