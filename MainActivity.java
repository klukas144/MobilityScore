package de.hka.projekt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import org.osmdroid.views.overlay.Marker;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import de.hka.projekt.network.BwegtEfaApiClient;
import de.hka.projekt.network.GeoCodeApiClient;
import de.hka.projekt.network.NextbikeApiClient;
import de.hka.projekt.network.WalkScoreApiClient;
import de.hka.projekt.objects.bwegtefa.BWLocation;
import de.hka.projekt.objects.bwegtefa.BwegtEfaCoordResponse;
import de.hka.projekt.objects.geocode.GeoCodeCoordResponse;
import de.hka.projekt.objects.nextbike.NBCity;
import de.hka.projekt.objects.nextbike.NBCountry;
import de.hka.projekt.objects.nextbike.NextbikeCoordResponse;
import de.hka.projekt.objects.nextbike.NBPlace;
import de.hka.projekt.objects.walkscore.WalkScoreCoordResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Lukas Heupel
 * @author Simon Schreckenberg
 * @author Alexander Beck
 * @author Cosimo Schostok
 * @author Kilian Langlinderer
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private String closestStop;
    private double stopDist = 0.0;
    private String closestBike;
    private double bikeDist = 0.0;
    private int[] productClasses = {};
    private int highestClass = Integer.MAX_VALUE;
    private String highestProductClass = " ";
    private String address;
    private int walkScore = 0;
    private double locationLat = 0.0;
    private double locationLon = 0.0;
    private Marker posMarker;
    private int performanceZoom;
    private int performanceScroll;
    private List<Marker> stopMarkerList = new ArrayList<>();
    private List<Marker> bikeMarkerList = new ArrayList<>();
    private double[] scoreList = new double[10];
    private String[] nameList = new String[10];
    private String[] reqStrings = new String[7];
    private RelativeLayout showScore;
    private int correct;
    double[] coords = new double[2];
    public static final int PREFERENCE_ACTIVITY_REQUEST_CODE = 1;
    private Button btn_score;
    private boolean reqsSet = false;
    private int highestClassPoints;


    /**
     * Karte laden und deren Einstellungen anpassen; Layout definieren; Views einbinden;
     * Buttons mit Aktionen versehen
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView txtScoreMessage = this.findViewById(R.id.scoreMessage);
        btn_score = this.findViewById(R.id.btnScore);
        ImageButton btn_position = this.findViewById(R.id.btnPosition);
        ImageButton btn_settings = this.findViewById(R.id.btnSettings);
        showScore = this.findViewById(R.id.showScorel1);
        Button btn_showScore = this.findViewById(R.id.btnShowScore);
        for (int i = 0; i < reqStrings.length; i++) {
            reqStrings[i] = "-1";
        }

        btn_score.setOnClickListener(view -> {
            if (correct == 0) {
                bikeDist = 0.0;
                stopDist = 0.0;
                calculateScore(locationLat, locationLon);
                IMapController stopMap = mapView.getController();
                GeoPoint fakePoint = new GeoPoint(locationLat, locationLon);
                stopMap.animateTo(fakePoint);
                if (mapView.getZoomLevelDouble() < 20.0) {
                    stopMap.setZoom(Math.round(mapView.getZoomLevelDouble()) + 1.0);
                }
                correct = 1;
                Toast.makeText(
                        getApplicationContext(),
                        "Bitte nochmal versuchen",
                        Toast.LENGTH_LONG).show();
            } else {
                showScore.setVisibility(View.VISIBLE);
                calculateScore(locationLat, locationLon);
                IMapController stopMap = mapView.getController();
                GeoPoint fakePoint = new GeoPoint(locationLat, locationLon);
                mapView.setScrollableAreaLimitLatitude(locationLat, locationLat, 0);
                mapView.setScrollableAreaLimitLongitude(locationLon, locationLon, 0);
                double zoomLevel = mapView.getZoomLevelDouble();
                mapView.setMinZoomLevel(zoomLevel);
                mapView.setMaxZoomLevel(zoomLevel);
                mapView.setMultiTouchControls(false);
                stopMap.animateTo(fakePoint);
                if (mapView.getZoomLevelDouble() < 20.0) {
                    stopMap.setZoom(Math.round(mapView.getZoomLevelDouble()) + 1.0);
                }
                txtScoreMessage.setText("Score wurde berechnet");
                correct = 0;
            }
        });

        btn_position.setOnClickListener(view -> {
            centerOnPosition();
        });

        btn_settings.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PreferenceActivity.class);
            intent.putExtra("reqList", reqStrings);
            intent.putExtra("status", true);
            startActivityForResult(intent, PREFERENCE_ACTIVITY_REQUEST_CODE);
        });

        btn_showScore.setOnClickListener(view -> {
            scoreList[0] = (double) walkScore;
            scoreList[1] = stopDist;
            scoreList[2] = bikeDist;
            scoreList[3] = (double) highestClassPoints;
            nameList[1] = closestStop;
            nameList[2] = closestBike;
            nameList[3] = highestProductClass;
            coords[0] = mapView.getMapCenter().getLatitude();
            coords[1] = mapView.getMapCenter().getLongitude();
            Intent intent = new Intent(this, ScoreActivity.class);
            intent.putExtra("scoreList", scoreList);
            intent.putExtra("nameList", nameList);
            intent.putExtra("coords", coords);
            intent.putExtra("reqs", reqStrings);
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
        GeoPoint startPoint = new GeoPoint(49.00955, 8.403218);
        posMarker = new Marker(mapView);
        IMapController mapController = mapView.getController();
        mapController.setCenter(startPoint);
        mapController.setZoom(16.0);
        mapView.setMinZoomLevel(12.0);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(false);
        locationLat = mapView.getMapCenter().getLatitude();
        locationLon = mapView.getMapCenter().getLongitude();
        performanceZoom = 100;
        performanceScroll = 50;
        correct = 0;

        this.mapView.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                if (performanceScroll >= 50) {
                    loadClosestStops(mapView.getMapCenter().getLatitude(), mapView.getMapCenter().getLongitude());
                    loadClosestBikes(mapView.getMapCenter().getLatitude(), mapView.getMapCenter().getLongitude());
                    performanceScroll = 0;
                } else {
                    performanceScroll++;
                }
                GeoPoint startPoint = new GeoPoint(locationLat, locationLon);
                posMarker.remove(mapView);
                posMarker.setPosition(startPoint);
                posMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                posMarker.setTitle("Dein aktueller Standort");
                posMarker.setIcon(getResources().getDrawable(R.drawable.baseline_person_pin_circle_24_red, getTheme()));
                mapView.getOverlays().add(posMarker);
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                if (performanceZoom >= 100) {
                    loadClosestStops(mapView.getMapCenter().getLatitude(), mapView.getMapCenter().getLongitude());
                    loadClosestBikes(mapView.getMapCenter().getLatitude(), mapView.getMapCenter().getLongitude());
                    performanceZoom = 0;
                } else {
                    performanceZoom++;
                }
                GeoPoint startPoint = new GeoPoint(locationLat, locationLon);
                posMarker.remove(mapView);
                posMarker.setPosition(startPoint);
                posMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                posMarker.setTitle("Dein aktueller Standort");
                posMarker.setIcon(getResources().getDrawable(R.drawable.baseline_person_pin_circle_24_red, getTheme()));
                mapView.getOverlays().add(posMarker);
                return false;
            }

        });

        String[] permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                initLocationListener();
                Log.d("MapActivity", "onGranted");
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                Log.d("MapActivity", "onDenied");
            }
        });

        if (!reqsSet) {
            reqsSet = true;
            Intent intent = new Intent(MainActivity.this, PreferenceActivity.class);
            intent.putExtra("reqList", reqStrings);
            intent.putExtra("status", false);
            startActivityForResult(intent, PREFERENCE_ACTIVITY_REQUEST_CODE);
        }

    }


    /**
     * Mobilitätspräferenzen/-voraussetzungen in der MainActivity speichern
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PREFERENCE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d("Database", "entries deleted");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            String[] word = data.getStringArrayExtra("saved");
            reqStrings = word;
            Toast.makeText(
                    getApplicationContext(),
                    "Voraussetzungen gespeichert",
                    Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Anhand von übergebenen Koordinaten notwendige Daten für den Score laden
     *
     * @param lat Breitengrad des Standorts
     * @param lon Längengrad des Standorts
     */
    protected void calculateScore(double lat, double lon) {
        loadAddressOf(lat, lon);
        loadClosestStops(lat, lon);
        loadClosestStop(lat, lon);
        loadClosestBikes(lat, lon);
        loadClosestBike(lat, lon);
        loadWalkScoreOf(lat, lon);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * zentriert die Karte auf den aktuellen Standort
     */
    public void centerOnPosition() {
        if (locationLat != 0.0 && locationLon != 0.0) {
            GeoPoint startPoint = new GeoPoint(locationLat, locationLon);
            IMapController mapController = mapView.getController();
            mapController.animateTo(startPoint);
            if (mapView.getZoomLevelDouble() < 20.0) {
                mapController.setZoom(Math.round(mapView.getZoomLevelDouble()) + 1.0);
            }
            performanceScroll = 50;
            performanceZoom = 100;
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Standort nicht gefunden",
                    Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Höchste Produktklasse einer Haltestelle herausfinden und entsprechend Punkte vergeben
     *
     * @param classes Produktklassen einer Haltestelle
     */
    protected void setHighestProductClass(int[] classes) {
        int val = Integer.MAX_VALUE;
        int[] order = {12, 16, 15, 14, 13, 0, 1, 2, 3, 4, 7, 6, 5, 8, 9, 17, 18, 10, 19, 11};
        for (int i = 0; i < classes.length; i++) {
            for (int j = 0; j < order.length; j++) {
                if (classes[i] == order[j] && j < val) {
                    val = j;
                    break;
                }
            }
        }
        highestClass = order[val];
        switch (order[val]) {
            case 0:
                highestProductClass = "Zug";
                highestClassPoints = 7;
                break;
            case 1:
                highestProductClass = "S-Bahn";
                highestClassPoints = 6;
                break;
            case 2:
                highestProductClass = "U-Bahn";
                highestClassPoints = 5;
                break;
            case 3:
                highestProductClass = "Stadtbahn";
                highestClassPoints = 5;
                break;
            case 4:
                highestProductClass = "Straßen-/Trambahn";
                highestClassPoints = 4;
                break;
            case 5:
                highestProductClass = "Stadtbus";
                highestClassPoints = 3;
                break;
            case 6:
                highestProductClass = "Regionalbus";
                highestClassPoints = 3;
                break;
            case 7:
                highestProductClass = "Schnellbus";
                highestClassPoints = 4;
                break;
            case 8:
                highestProductClass = "Seil-/Zahnradbahn";
                highestClassPoints = 2;
                break;
            case 9:
                highestProductClass = "Schiff";
                highestClassPoints = 2;
                break;
            case 10:
                highestProductClass = "AST (flächengebunden)";
                highestClassPoints = 1;
                break;
            case 11:
                highestProductClass = "Sonstige";
                highestClassPoints = 1;
                break;
            case 12:
                highestProductClass = "Flugzeug";
                break;
            case 13:
                highestProductClass = "Zug (Nahverkehr)";
                highestClassPoints = 7;
                break;
            case 14:
                highestProductClass = "Zug (Fernverkehr)";
                highestClassPoints = 8;
                break;
            case 15:
                highestProductClass = "Zug Fernv m Zuschlag";
                highestClassPoints = 8;
                break;
            case 16:
                highestProductClass = "Zug Fernv m spez Fpr";
                highestClassPoints = 8;
                break;
            case 17:
                highestProductClass = "SEV Schienenersatzverkehr";
                highestClassPoints = 2;
                break;
            case 18:
                highestProductClass = "Zug Shuttle";
                highestClassPoints = 2;
                break;
            case 19:
                highestProductClass = "Bürgerbus";
                highestClassPoints = 1;
                break;
            default:
                highestProductClass = "Default";
                break;
        }
    }


    /**
     * Karteneinstellungen zurücksetzen, nochmals Standortberechtigung abfragen
     */
    @Override
    protected void onResume() {
        super.onResume();
        String[] permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        showScore.setVisibility(View.GONE);
        mapView.resetScrollableAreaLimitLatitude();
        mapView.resetScrollableAreaLimitLongitude();
        mapView.setMinZoomLevel(12.0);
        mapView.setMaxZoomLevel(20.0);
        mapView.setMultiTouchControls(true);
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


    /**
     * LocationListener initialisieren; bei Standortänderung Marker an neuen Standort setzen, alten Marker löschen
     */
    @SuppressLint("MissingPermission")
    private void initLocationListener() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                locationLat = location.getLatitude();
                locationLon = location.getLongitude();
                GeoPoint startPoint = new GeoPoint(locationLat, locationLon);
                posMarker.remove(mapView);
                posMarker.setPosition(startPoint);
                posMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                posMarker.setTitle("Dein aktueller Standort");
                posMarker.setIcon(getResources().getDrawable(R.drawable.baseline_person_pin_circle_24_red, getTheme()));
                mapView.getOverlays().add(posMarker);
                IMapController mapController = mapView.getController();
                GeoPoint fakePoint = new GeoPoint(mapView.getMapCenter().getLatitude(), mapView.getMapCenter().getLongitude() + 0.000001);
                mapController.setCenter(fakePoint);
                fakePoint.setLongitude(mapView.getMapCenter().getLongitude() - 0.000001);
                mapController.setCenter(fakePoint);
            }
        };
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
    }


    /**
     * Setzt String für die Autorisierung des Kartenservers zusammen
     */
    private String getMapServerAuthorizationString(String username, String password) {
        String authorizationString = String.format("%s:%s", username, password);
        return "Basic " + Base64.encodeToString(authorizationString.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
    }


    /**
     * NextBike API Call machen, an vorhandene Fahrräder/Stationen im Umkreis des Standorts einen Marker setzen
     *
     * @param latitude  Breitengrad des Standorts
     * @param longitude Längengrad des Standorts
     */
    private void loadClosestBikes(double latitude, double longitude) {
        Call<NextbikeCoordResponse> nextBikeCall = NextbikeApiClient
                .getInstance().getClient()
                .loadBikesWithinRadius(latitude, longitude, 1000);
        nextBikeCall.enqueue(new Callback<NextbikeCoordResponse>() {
            @Override
            public void onResponse(Call<NextbikeCoordResponse> call, Response<NextbikeCoordResponse> response) { //falls wirklich Haltestellen kommen, dann ausgeben
                NextbikeCoordResponse nextbikeCoordResponse = response.body();
                Log.d("MapActivity", nextbikeCoordResponse.toString());
                boolean a = false;
                for (NBCountry country : nextbikeCoordResponse.getCountries()) {
                    for (NBCity city : country.getCities()) {
                        for (NBPlace plc : city.getPlaces()) {
                            String title;

                            if (plc.isBike()) {
                                title = plc.getName();
                            } else if (plc.getBikes() == 0) {
                                title = (city.getName() + " " + plc.getName() + "\r\n" + plc.getBikes() + " verfügbare Räder");
                            } else if (plc.getBikes() == 1) {
                                title = (city.getName() + " " + plc.getName() + "\r\n" + plc.getBikes() + " verfügbares Rad");
                            } else {
                                title = (city.getName() + " " + plc.getName() + "\r\n" + plc.getBikes() + " verfügbare Räder");
                            }

                            a = true;
                            Log.d("MapActivity", String.format("Place: %s", plc.getName()));
                            GeoPoint bikePoint = new GeoPoint(plc.getLat(), plc.getLng());
                            boolean alreadyThere = false;
                            if (plc.isBike()) {
                                for (Marker m : bikeMarkerList) {
                                    if (m.getTitle().equals(title)) {
                                        if (m.getPosition().getLongitude() == bikePoint.getLongitude() && m.getPosition().getLatitude() == bikePoint.getLatitude()) {
                                            alreadyThere = true;
                                            Log.d("MapActivity", "Bike/Place already there: " + m.getTitle());
                                        } else {
                                            bikeMarkerList.remove(m);
                                            Log.d("MapActivity", "Bike changed position: " + m.getTitle());
                                        }
                                        break;
                                    }
                                }
                            } else {
                                for (Marker m : bikeMarkerList) {
                                    if (m.getPosition().getLatitude() == bikePoint.getLatitude() && m.getPosition().getLongitude() == bikePoint.getLongitude()) {
                                        if (plc.getBikes() == 1) {
                                            m.setTitle(title);
                                        } else {
                                            m.setTitle(title);
                                        }

                                        alreadyThere = true;
                                    }
                                    break;
                                }
                            }

                            if (!alreadyThere) {
                                Marker bikeMarker = new Marker(mapView);
                                bikeMarker.setPosition(bikePoint);
                                bikeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);

                                Drawable d;
                                if (plc.isBike()) {
                                    d = getDrawable(R.drawable.baseline_directions_bike_24);
                                } else if (plc.getBikes() == 0) {
                                    d = getDrawable(R.mipmap.bike_spot_empty_round);
                                } else if (plc.getBikes() == 1) {
                                    d = getDrawable(R.mipmap.bike_spot_full_round);
                                } else {
                                    d = getDrawable(R.mipmap.bike_spot_full_round);
                                }
                                bikeMarker.setIcon(d);
                                bikeMarker.setTitle(title);
                                mapView.getOverlays().add(bikeMarker);
                                bikeMarkerList.add(bikeMarker);
                                Log.d("MapActivity", "Bike/Place added: " + bikeMarker.getTitle());
                            }
                        }
                    }
                }
                if (!a) {
                    Log.d("MapActivity", "keine Fahrräder gefunden");
                }
                IMapController mapController = mapView.getController();
                GeoPoint fakePoint = new GeoPoint(mapView.getMapCenter().getLatitude(), mapView.getMapCenter().getLongitude() + 0.000001);
                mapController.setCenter(fakePoint);
                fakePoint.setLongitude(mapView.getMapCenter().getLongitude() - 0.000001);
                mapController.setCenter(fakePoint);
            }

            @Override
            public void onFailure(Call<NextbikeCoordResponse> call, Throwable t) { //wenn nicht, dann eben nicht
                Log.d("MapActivity", "Nextbike API Failure");
            }
        });
    }


    /**
     * NextBike API Call machen, für nächstes Fahrrad die Distanz speichern
     *
     * @param latitude  Breitengrad des Standorts
     * @param longitude Längengrad des Standorts
     */
    private void loadClosestBike(double latitude, double longitude) {
        Call<NextbikeCoordResponse> nextBikeCall = NextbikeApiClient
                .getInstance().getClient()
                .loadBikesWithinRadius(latitude, longitude, 1000);
        nextBikeCall.enqueue(new Callback<NextbikeCoordResponse>() {
            @Override
            public void onResponse(Call<NextbikeCoordResponse> call, Response<NextbikeCoordResponse> response) { //falls wirklich Haltestellen kommen, dann ausgeben
                NextbikeCoordResponse nextbikeCoordResponse = response.body();
                Log.d("MapActivity", nextbikeCoordResponse.toString());
                boolean a = false;
                for (NBCountry country : nextbikeCoordResponse.getCountries()) {
                    for (NBCity city : country.getCities()) {
                        for (NBPlace plc : city.getPlaces()) {
                            if (!(plc.isSpot() && plc.getBikes() == 0) && !a) {
                                a = true;
                                closestBike = plc.getName();
                                bikeDist = Math.round(plc.getDist());
                                break;
                            }
                            Log.d("MapActivity", String.format("Place: %s", plc.getName()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<NextbikeCoordResponse> call, Throwable t) { //wenn nicht, dann eben nicht
                Log.d("MapActivity", "Nextbike API Failure");
            }
        });
    }


    /**
     * WalkScore API Call machen, WalkScore des Standorts speichern
     *
     * @param latitude  Breitengrad des Standorts
     * @param longitude Längengrad des Standorts
     */
    private void loadWalkScoreOf(double latitude, double longitude) { //WalkScore laden
        Call<WalkScoreCoordResponse> walkScoreCall = WalkScoreApiClient
                .getInstance().getClient()
                .loadWalkScore(address, latitude, longitude, 1, 1, "bba036063fd80a930b27964e38c76e6e");
        walkScoreCall.enqueue(new Callback<WalkScoreCoordResponse>() {
            @Override
            public void onResponse(Call<WalkScoreCoordResponse> call, Response<WalkScoreCoordResponse> response) { //falls wirklich Haltestellen kommen, dann ausgeben
                WalkScoreCoordResponse walkScoreCoordResponse = response.body();
                Log.d("MapActivity", walkScoreCoordResponse.toString());
                walkScore = walkScoreCoordResponse.getWalkScore();
            }

            @Override
            public void onFailure(Call<WalkScoreCoordResponse> call, Throwable t) { //wenn nicht, dann eben nicht
                Log.d("MapActivity", "WalkScore API Failure");
            }
        });
    }


    /**
     * GeoCode API Call machen, Adresse des Standorts speichern
     *
     * @param latitude  Breitengrad des Standorts
     * @param longitude Längengrad des Standorts
     */
    private void loadAddressOf(double latitude, double longitude) { //Adresse laden
        Call<GeoCodeCoordResponse> geoCodeCall = GeoCodeApiClient
                .getInstance().getClient()
                .loadAddress(latitude, longitude, "658c357e8a966999083179nzg1f18cf");
        geoCodeCall.enqueue(new Callback<GeoCodeCoordResponse>() {
            @Override
            public void onResponse(Call<GeoCodeCoordResponse> call, Response<GeoCodeCoordResponse> response) { //falls wirklich Haltestellen kommen, dann ausgeben
                GeoCodeCoordResponse geoCodeCoordResponse = response.body();
                Log.d("MapActivity", geoCodeCoordResponse.toString());
                address = geoCodeCoordResponse.getAddress();
            }

            @Override
            public void onFailure(Call<GeoCodeCoordResponse> call, Throwable t) { //wenn nicht, dann eben nicht
                Log.d("MapActivity", "GeoCode API Failure");
            }
        });
    }


    /**
     * Bwegt EFA API Call machen, an vorhandene Haltestellen im Umkreis des Standorts einen Marker setzen
     *
     * @param latitude  Breitengrad des Standorts
     * @param longitude Längengrad des Standorts
     */
    private void loadClosestStops(double latitude, double longitude) {
        Call<BwegtEfaCoordResponse> efaCall = BwegtEfaApiClient
                .getInstance().getClient()
                .loadStopsWithinRadius(
                        BwegtEfaApiClient
                                .getInstance()
                                .createCoordinateString(latitude, longitude), 2000);
        efaCall.enqueue(new Callback<BwegtEfaCoordResponse>() {
            @Override
            public void onResponse(Call<BwegtEfaCoordResponse> call, Response<BwegtEfaCoordResponse> response) { //falls wirklich Haltestellen kommen, dann ausgeben
                BwegtEfaCoordResponse efaCoordResponse = response.body();
                Log.d("MapActivity", String.format("Response %d Locations", efaCoordResponse.getLocations().size()));
                boolean a = false;
                for (BWLocation loc : efaCoordResponse.getLocations()) {
                    Log.d("MapActivity", String.format("Location: %s", loc.getProperties().getStopNameWithPlace()));
                    a = true;
                    double[] stopCoord = loc.getCoordinates();
                    GeoPoint stopPoint = new GeoPoint(stopCoord[0], stopCoord[1]);
                    boolean alreadyThere = false;
                    for (Marker m : stopMarkerList) {
                        if (m.getTitle().equals(loc.getProperties().getStopNameWithPlace()) && m.getPosition().equals(stopPoint)) {
                            alreadyThere = true;
                            Log.d("MapActivity", "Stop already there: " + m.getTitle());
                            break;
                        }
                    }
                    if (!alreadyThere) {
                        Marker stopMarker = new Marker(mapView);
                        stopMarker.setPosition(stopPoint);
                        stopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                        stopMarker.setTitle(loc.getProperties().getStopNameWithPlace());
                        stopMarker.setIcon(getResources().getDrawable(R.drawable.stop_icon, getTheme()));
                        mapView.getOverlays().add(stopMarker);
                        stopMarkerList.add(stopMarker);
                        Log.d("MapActivity", "Stop added: " + stopMarker.getTitle());
                    }
                }
                if (!a) {
                    Log.d("MapActivity", "keine Haltestellen gefunden");
                }
                IMapController mapController = mapView.getController();
                GeoPoint fakePoint = new GeoPoint(mapView.getMapCenter().getLatitude(), mapView.getMapCenter().getLongitude() + 0.000001);
                mapController.setCenter(fakePoint);
                fakePoint.setLongitude(mapView.getMapCenter().getLongitude() - 0.000001);
                mapController.setCenter(fakePoint);
            }

            @Override
            public void onFailure(Call<BwegtEfaCoordResponse> call, Throwable t) { //wenn nicht, dann eben nicht
                Log.d("MapActivity", "Bwegt API Failure");
            }
        });
    }


    /**
     * Bwegt EFA API Call machen, für nächste Haltestelle Distanz und Produktklassen speichern
     *
     * @param latitude  Breitengrad des Standorts
     * @param longitude Längengrad des Standorts
     */
    private void loadClosestStop(double latitude, double longitude) { //näheste Haltestellen laden
        Call<BwegtEfaCoordResponse> efaCall = BwegtEfaApiClient
                .getInstance().getClient()
                .loadStopsWithinRadius(
                        BwegtEfaApiClient
                                .getInstance()
                                .createCoordinateString(latitude, longitude), 2000);
        efaCall.enqueue(new Callback<BwegtEfaCoordResponse>() {
            @Override
            public void onResponse(Call<BwegtEfaCoordResponse> call, Response<BwegtEfaCoordResponse> response) { //falls wirklich Haltestellen kommen, dann ausgeben
                BwegtEfaCoordResponse efaCoordResponse = response.body();
                Log.d("MapActivity", String.format("Response %d Locations", efaCoordResponse.getLocations().size()));
                boolean a = false;
                for (BWLocation loc : efaCoordResponse.getLocations()) {
                    Log.d("MapActivity", String.format("Closest Location: %s", loc.getName()));
                    if (!a) {
                        closestStop = loc.getProperties().getStopNameWithPlace();
                        stopDist = loc.getProperties().getDistance();
                        a = true;
                        productClasses = loc.getProductClasses();
                        setHighestProductClass(productClasses);
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<BwegtEfaCoordResponse> call, Throwable t) { //wenn nicht, dann eben nicht
                Log.d("MapActivity", "Bwegt API Failure");
            }
        });
    }

}