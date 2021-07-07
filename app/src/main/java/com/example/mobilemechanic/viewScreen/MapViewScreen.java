package com.example.mobilemechanic.viewScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilemechanic.Adapter.mechanicsAdapter;
import com.example.mobilemechanic.Model.MechanicModel;
import com.example.mobilemechanic.R;
import com.example.mobilemechanic.Util.GPSUtils;
//import com.example.mobilemechanic.databinding.FragmentHomeBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.JsonObject;
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
import com.mapbox.mapboxsdk.camera.CameraPosition;
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
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
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

import static com.example.mobilemechanic.Util.GPSUtils.GPS_REQUEST;
import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class MapViewScreen extends AppCompatActivity {

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
    private MapView mapView;
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
//    private EditText sourceText, destinationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.public_token));
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_map_view_screen);
        mDatabase = FirebaseDatabase.getInstance().getReference("Mechanics");

        storage = FirebaseStorage.getInstance();

        mechanicList = new ArrayList<>();


        setTitle("Map List ");
        new GPSUtils(this).turnGPSOn(new GPSUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
//                 turn on GPS
                isGPS = isGPSEnable;
            }
        });

        adapter = new mechanicsAdapter(MapViewScreen.this, mechanicList);

        if (isNetworkConnected()) {
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    mechanicList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        MechanicModel model = postSnapshot.getValue(MechanicModel.class);
//                        model.setID(postSnapshot.getKey());

//                        if (Integer.valueOf(receivedProduct.getCapacity()) > 0) {

                        mechanicList.add(model);
//                        } else {
//                            StorageReference storeRef = storage.getReferenceFromUrl(receivedProduct.getImage());

//                            storeRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    mDatabase.child(receivedProduct.getID()).removeValue();
//                                }
//                            })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(Products_view.this, "Error in Deletion", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                        }
                    }

                    if (mechanicList.isEmpty()) {
//                        defaultView.setVisibility(View.VISIBLE);
//                        circleP_bar.setVisibility(View.INVISIBLE);
                    }

//                    Toast.makeText(MapViewScreen.this, "value : " + mechanicList.size(), Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
//                    circleP_bar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(MapViewScreen.this, "Ooops Something went wrong Try Later...", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MapViewScreen.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                    circleP_bar.setVisibility(View.INVISIBLE);
                }
            });
        } else {
//            circleP_bar.setVisibility(View.INVISIBLE);
//            defaultView.setVisibility(View.VISIBLE);
//            defaultView.setText(R.string.No_network);
            Toast.makeText(this, R.string.No_network, Toast.LENGTH_LONG).show();
        }
        mapView = (MapView) findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

                homeMapBoxMap = mapboxMap;
                homeMapBoxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        if (checkLocationPermission()) {
//                            Toast.makeText(MapViewScreen.this, "Permission are on...", Toast.LENGTH_SHORT).show();
                            enableLocationComponent(style);
                        } else {
                            Toast.makeText(MapViewScreen.this, "Requesting for permission", Toast.LENGTH_SHORT).show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_PERMISSION);
                            }
                            else{
                                Toast.makeText(MapViewScreen.this, "Enable Location Manually", Toast.LENGTH_SHORT).show();
                            }
                        }
                        style.addImage(symbolIconId, BitmapUtils.getBitmapFromDrawable(
                                getResources().getDrawable(R.drawable.ic_baseline_construction_24)));

//                        ArrayList<MechanicModel> mechanicModels = new ArrayList<>();
//
//                        mechanicModels.add( new MechanicModel("okello", "0712", "lop", "ed", "re", "df", "34.5", "0.3"));
//                        mechanicModels.add( new MechanicModel("otieno", "0712", "lop", "ed", "re", "df", "37.5", "0.4"));
//                        mechanicModels.add( new MechanicModel("enos", "0712", "lop", "ed", "re", "df", "24.5", "0.2"));

                        // Create an empty GeoJSON source using the empty feature collection
//                        setUpSource(style, mechanicList);

                        // Create symbol manager object.
                        SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style);

//                        symbolManager = new SymbolManager(mapView, mbMap, style);
                        symbolManager.setIconAllowOverlap(true);  //your choice t/f
                        symbolManager.setTextAllowOverlap(false);  //your choice t/f
// Add click listeners if desired.
                        symbolManager.addClickListener(new OnSymbolClickListener() {
                            @Override
                            public boolean onAnnotationClick(Symbol symbol) {
//                                Toast.makeText(MapViewScreen.this, "Section Clicked" + symbol.getLatLng(), Toast.LENGTH_SHORT).show();

                               MechanicModel currentMechanic = new MechanicModel();

                               for(MechanicModel mech : mechanicList){
                                   if(Double.parseDouble(mech.getLatitude()) == symbol.getLatLng().getLatitude()
                                    && Double.parseDouble(mech.getLongitude()) == symbol.getLatLng().getLongitude()){
                                       currentMechanic = mech;
                                   }
                               }

                                Intent passIntent = new Intent(MapViewScreen.this, MechanicDetails.class);
                                passIntent.putExtra("name", currentMechanic.getName());
                                passIntent.putExtra("location", currentMechanic.getLocation());
                                passIntent.putExtra("mail", currentMechanic.getEmail());
                                passIntent.putExtra("image", currentMechanic.getImageUrl());
                                passIntent.putExtra("phone", currentMechanic.getPhone());
                                passIntent.putExtra("speciality", currentMechanic.getSpeciality());
                                passIntent.putExtra("latitude", currentMechanic.getLatitude());
                                passIntent.putExtra("longitude", currentMechanic.getLongitude());

                                startActivity(passIntent);

                                return true;
                            }
                        });

//                        List<SymbolOptions> options = new ArrayList<>();
//                        for (int i = 0; i < 5; i++) {
//                            options.add(new SymbolOptions()
//                                    .withLatLng(new LatLng(0.3 + i,30.0 + i))
//                                    .withIconImage(symbolIconId)
////                                    set the below attributes according to your requirements
//                                    .withIconSize(1.5f)
//                                    .withIconOffset(new Float[] {0f,-0.5f})
////                                    .withZIndex(10)
//                                    .withTextField("test marker okello observe")
//                                    .withTextHaloColor("rgba(255, 255, 255, 9)")
//                                    .withTextColor("rgba(255, 152, 0, 100)")
//                                    .withTextSize(10.0f)
//                                    .withTextHaloWidth(5.0f)
//                                    .withTextJustify("center")
//                                    .withTextAnchor("top")
//                                    .withTextOffset(new Float[] {0f, 1.5f})
////                                    .setDraggable(false)
//                            );
//                        }

//                        symbols = symbolManager.create(options);



                        List<SymbolOptions> mechanicOptions = new ArrayList<>();
                        for (MechanicModel modelOption : mechanicList) {
                            mechanicOptions.add(new SymbolOptions()
                                            .withLatLng(new LatLng(Double.parseDouble(modelOption.getLatitude()),Double.parseDouble(modelOption.getLongitude())))
                                            .withIconImage(symbolIconId)
//                                    set the below attributes according to your requirements
                                            .withIconSize(1.5f)
                                            .withIconOffset(new Float[] {0f,-0.5f})
                                            .withTextField(modelOption.getSpeciality())
                                            .withTextHaloColor("rgba(255, 255, 255, 100)")
                        .withTextColor("rgba(255, 152, 0, 100)")
                        .withTextSize(10.0f)
                        .withTextJustify("center")
                                            .withTextHaloWidth(5.0f)
                                            .withTextAnchor("top")
                                            .withTextOffset(new Float[] {0f, 1.5f})
//                                    .setDraggable(false)
                            );
                        }

                        symbols = symbolManager.create(mechanicOptions);

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
    }


    private void setUpSource(Style style, ArrayList<MechanicModel> mechanicModels) {


        List<Feature> pointsList = new ArrayList<>();

        for (MechanicModel model : mechanicModels) {
            String name = model.getName();
            Feature feature = Feature.fromGeometry(Point.fromLngLat(Double.parseDouble(model.getLongitude()), Double.parseDouble(model.getLatitude())));
            pointsList.add(feature);
        }

            //        Add the marker sources to the map
            style.addSource(new GeoJsonSource(geojsonSourceLayerId,
                    FeatureCollection.fromFeatures(pointsList)
                    ));

        // Add the red marker icon SymbolLayer to the map
        style.addLayer(new SymbolLayer("layer-id",
                geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[]{0f, -8f})
        ));

    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
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
                    Toast.makeText(MapViewScreen.this, "No routes found, make sure you set the right user and access token.", Toast.LENGTH_SHORT).show();
                    return;

                } else if (response.body().routes().size() < 1) {
                    Toast.makeText(MapViewScreen.this, "No routes found", Toast.LENGTH_SHORT).show();
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

                Toast.makeText(MapViewScreen.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }

//        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
//
//            // Retrieve selected location's CarmenFeature
//            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);
////            Log.d(" Origin : ", "onActivityResult: " + selectedCarmenFeature.placeName());
//            sourceLocation = selectedCarmenFeature.placeName();
////            sourceText.setText(sourceLocation);
//
//            origin = Point.fromLngLat(((Point) selectedCarmenFeature.geometry()).longitude(), ((Point) selectedCarmenFeature.geometry()).latitude());
//            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
//            // Then retrieve and update the source designated for showing a selected location's symbol layer icon
//
//            if (homeMapBoxMap != null) {
//                Style placeStyle = homeMapBoxMap.getStyle();
//                if (placeStyle != null) {
//                    GeoJsonSource geoJsonSource = placeStyle.getSourceAs(geojsonSourceLayerId);
//                    if (geoJsonSource != null) {
//                        geoJsonSource.setGeoJson(FeatureCollection.fromFeatures(
//                                new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())}
//                        ));
//                    }
//
//                    // Move map camera to the selected location
//                    homeMapBoxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
//                            new CameraPosition.Builder()
//                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
//                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
//                                    .zoom(14)
//                                    .build()), 4000);
//
//
//                }
//            }
//        }
//
//        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_DESTINATION) {
//
//            // Retrieve selected location's CarmenFeature
//            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);
//
//            destinationLocation = selectedCarmenFeature.placeName();
////            destinationText.setText(destinationLocation);
////            Log.d(" Destination : ", "onActivityResult: " + selectedCarmenFeature.placeName());
//            destination = Point.fromLngLat(((Point) selectedCarmenFeature.geometry()).longitude(), ((Point) selectedCarmenFeature.geometry()).latitude());
//            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
//            // Then retrieve and update the source designated for showing a selected location's symbol layer icon
//
//            if (homeMapBoxMap != null) {
//                Style placeStyle = homeMapBoxMap.getStyle();
//                if (placeStyle != null) {
//                    GeoJsonSource geoJsonSource = placeStyle.getSourceAs(geoDestinationId);
//                    if (geoJsonSource != null) {
//                        geoJsonSource.setGeoJson(FeatureCollection.fromFeatures(
//                                new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())}
//                        ));
//                    }
//
//                    // Move map camera to the selected location
//                    homeMapBoxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
//                            new CameraPosition.Builder()
//                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
//                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
//                                    .zoom(14)
//                                    .build()), 4000);
//
//
//                }
//            }
//        }
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