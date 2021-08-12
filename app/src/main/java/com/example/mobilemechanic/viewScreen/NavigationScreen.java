package com.example.mobilemechanic.viewScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import com.example.mobilemechanic.Adapter.mechanicsAdapter;
import com.example.mobilemechanic.Model.MechanicModel;
import com.example.mobilemechanic.R;
import com.example.mobilemechanic.Util.GPSUtils;
import com.example.mobilemechanic.Util.locationUpdater;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.JsonObject;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class NavigationScreen extends AppCompatActivity {

    private MapboxMap homeMapBoxMap;
    private static final int LOCATION_PERMISSION = 5;
    private boolean isGPS = false;

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final int REQUEST_CODE_DESTINATION = 2;
    private CarmenFeature home;
    private CarmenFeature work;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String geoDestinationId = "destLayerId";
    private String symbolIconId = "symbolIconId";

    private Point origin, destination;
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private MapboxDirections client;
    private DirectionsRoute currentRoute;

    private String sourceLocation, destinationLocation;
    private MapView mapViewNavigation;
    DatabaseReference mDatabase;
    FirebaseStorage storage;
    ArrayList<MechanicModel> mechanicList;
    mechanicsAdapter adapter;
    private SymbolManager symbolManager;
    private Symbol symbol;
    private MarkerView markerView;
    private MarkerViewManager markerViewManager;
    //    private SymbolManager symbolManager;
    private List<Symbol> symbols = new ArrayList<>();
    private String mechanicLatitude, mechanicLongitude;


//    private lateinit var NotificationReference: DatabaseReference
//    private val TAG = "NotificationsFragment"
//    private lateinit var notificationsViewModel: sharedViewModel
//    private var binding: FragmentNotificationsBinding? = null
//    private var trackMapBoxMap: MapboxMap? = null
//    private var isGPS: Boolean = false

    // Variables needed to add the location engine
    private  LocationEngine  locationEngine;
    private Long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private Long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
//    private var sourceLat: Double? = null
//    private var sourceLong: Double? = null
//    private var destinationLat: Double = 0.0
//    private var destinationLong: Double? = null
//    private val symbolIconId = "symbolIconId"
//    private val geoSourceLayerId = "sourceLayerId"
//    private val geoDestinationId = "destLayerId"
//    private val ROUTE_LAYER_ID = "route-layer-id"
//    private val ROUTE_SOURCE_ID = "route-source-id"
//    private lateinit var client: MapboxDirections
//    private lateinit var currentRoute: DirectionsRoute
    private double userLat;
    private double userLong;

    // Variables needed to listen to location updates
    private locationUpdater callback = new locationUpdater(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.public_token));
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hid
        setContentView(R.layout.activity_navigation_screen);

        new GPSUtils(this).turnGPSOn(new GPSUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
//                 turn on GPS
                isGPS = isGPSEnable;
            }
        });

        if (getIntent().hasExtra("latitude") && getIntent().hasExtra("longitude")){
            mechanicLatitude = getIntent().getStringExtra("latitude");
            mechanicLongitude = getIntent().getStringExtra("longitude");
        }
        destination = Point.fromLngLat(Double.parseDouble(mechanicLongitude), Double.parseDouble(mechanicLatitude));
//        origin = Point.fromLngLat(34.5, 0.3);
        mapViewNavigation = (MapView) findViewById(R.id.mapViewNavigation);
        mapViewNavigation.onCreate(savedInstanceState);
        mapViewNavigation.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

                homeMapBoxMap = mapboxMap;
                homeMapBoxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        if (checkLocationPermission()) {
//                            Toast.makeText(NavigationScreen.this, "Permission are on...", Toast.LENGTH_SHORT).show();
                            enableLocationComponent(style);
                        } else {
                            Toast.makeText(NavigationScreen.this, "Requesting for permission", Toast.LENGTH_SHORT).show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_PERMISSION);
                            } else {
                                Toast.makeText(NavigationScreen.this, "Enable Location Manually", Toast.LENGTH_SHORT).show();
                            }
                        }
//                        addUserLocations();

                        style.addImage(symbolIconId, BitmapUtils.getBitmapFromDrawable(
                                getResources().getDrawable(R.drawable.marker_location)));


                        // Create an empty GeoJSON source using the empty feature collection
                        setUpSource(style);

                        setUpDestination(style);

                        setRouteLayer(style);

//                        if(locationUpdater.location != null){
//                            userLat = locationUpdater.location.getLatitude();
//                            userLong = locationUpdater.location.getLongitude();
//                        }
                        // Create an empty GeoJSON source using the empty feature collection
//                        setUpSource(style, mechanicList);


                    }
                });

            }
        });

    }


    private void setRouteLayer(Style style) {
        //        Add the route sources to the map
        style.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

//        Add the route layer using lineLayer(This layer will display the directions route) to the map
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#009688"))
        );
        style.addLayer(routeLayer);
    }

    private void boundView() {
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(new LatLng(origin.latitude(), origin.longitude()))
                .include(new LatLng(destination.latitude(), destination.longitude()))
                .build();

        homeMapBoxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 10));

    }

    @SuppressLint("MissingPermission")
    private void enableLocationComponent(Style style) {

        LocationComponentOptions locationComponentOptions =
                LocationComponentOptions.builder(this)
                        .pulseEnabled(true)
                        .pulseColor(Color.parseColor("#86C0ED"))
                        .foregroundTintColor(Color.BLUE)
                        .pulseAlpha(.4f)
                        .pulseInterpolator(new BounceInterpolator())
                        .build();

        LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
                .builder(this, style)
                .locationComponentOptions(locationComponentOptions)
                .build();

//            Get an instance of locationComponent
        LocationComponent locationComponent = homeMapBoxMap.getLocationComponent();

//            Activate location component with options
        locationComponent.activateLocationComponent(locationComponentActivationOptions);

//            Enable to make the component visible
        locationComponent.setLocationComponentEnabled(true);
//                locationComponent.zoomWhileTracking(12, 1000);

//            set the component camera mode
        locationComponent.setCameraMode(CameraMode.TRACKING);

//            set the component render mode
        locationComponent.setRenderMode(RenderMode.COMPASS);

        initLocationEngine();
    }

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == GPSUtils.GPS_REQUEST) {
//                isGPS = true
//            }
//        }
//    }


    private void setUpSource(Style style) {
        if(locationUpdater.location != null){
            userLat = locationUpdater.location.getLatitude();
            userLong = locationUpdater.location.getLongitude();
            origin = Point.fromLngLat(userLong, userLat);
        }
        if (origin == null) {
            style.addSource(new GeoJsonSource(geojsonSourceLayerId));
        } else {
            //        Add the marker sources to the map
            style.addSource(new GeoJsonSource(geojsonSourceLayerId,
                    FeatureCollection.fromFeatures(new Feature[]{
                            Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
                    })));

            if (destination != null) {
                boundView();
                getRoutes(homeMapBoxMap, origin, destination);
            }
        }
        // Add the red marker icon SymbolLayer to the map
        style.addLayer(new SymbolLayer("layer-id",
                geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[]{0f, -8f})
        ));

//        if(locationUpdater.location != null){
//            userLat = locationUpdater.location.getLatitude();
//            userLong = locationUpdater.location.getLongitude();
//        }

    }

    private void setUpDestination(Style style) {

        if (destination == null) {
            style.addSource(new GeoJsonSource(geoDestinationId));
        } else {
            //        Add the marker sources to the map
            style.addSource(new GeoJsonSource(geoDestinationId,
                    FeatureCollection.fromFeatures(new Feature[]{
                            Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude())),
                    })));

            if (origin != null) {
                boundView();
                getRoutes(homeMapBoxMap, origin, destination);
            }
        }
        // Add the red marker icon SymbolLayer to the map
        style.addLayer(new SymbolLayer("layer-idDestination",
                geoDestinationId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[]{0f, -8f})
        ));

    }

    /**
     * Make a request to the Mapbox Directions API. Once successful, pass the route to the
     * route layer.
     *
     * @param mapboxMap   the Mapbox map object that the route will be drawn on
     * @param origin      the starting point of the route
     * @param destination the desired finish point of the route
     */
    private void getRoutes(MapboxMap mapboxMap, Point origin, Point destination) {
        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.public_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {

            //            private customDialog dialog;
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                if (response.body() == null) {
                    Toast.makeText(NavigationScreen.this, "No routes found, make sure you set the right user and access token.", Toast.LENGTH_SHORT).show();
                    return;

                } else if (response.body().routes().size() < 1) {
                    Toast.makeText(NavigationScreen.this, "No routes found", Toast.LENGTH_SHORT).show();
                    return;
                }

//                Get the directions route
                currentRoute = response.body().routes().get(0);

//                textView.setText("Distance : " + currentRoute.distance() + "            Time : " + currentRoute.duration() );
                // Make a toast which displays the route's distance
//                Toast.makeText(getActivity(), "Total Distance : " + currentRoute.distance()
//                        + "   Estimated Time : " + currentRoute.duration(), Toast.LENGTH_LONG).show();
                if (mapboxMap != null) {
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            // Retrieve and update the source designated for showing the directions route
                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

//              Create a LineString with the directions route's geometry and
//              reset the GeoJSON source for the route LineLayer source
                            if (source != null) {
                                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                            }
                        }
                    });
                }


            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                Toast.makeText(NavigationScreen.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initLocationEngine();
        mapViewNavigation.onStart();
    }

    @Override
    protected void onResume() {
        if (origin != null && destination != null && homeMapBoxMap != null) {
            boundView();

            getRoutes(homeMapBoxMap, origin, destination);

        }
        initLocationEngine();
        if(locationUpdater.location != null){
            userLat = locationUpdater.location.getLatitude();
            userLong = locationUpdater.location.getLongitude();
        }
        super.onResume();
        mapViewNavigation.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapViewNavigation.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapViewNavigation.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapViewNavigation.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapViewNavigation.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        mapViewNavigation.onDestroy();
    }

    public boolean checkLocationPermission() {
        boolean isPermEnabled = (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
        return isPermEnabled;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    Toast.makeText(this, "Permission are granted", Toast.LENGTH_SHORT).show();

                    homeMapBoxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            enableLocationComponent(style);
                        }
                    });

                } else {

                    Toast.makeText(this, "Permission are denied", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }

            }

        }
    }
}