//package com.example.mobilemechanic.home;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.location.LocationManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.BounceInterpolator;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//import androidx.lifecycle.Observer;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.example.mobilemechanic.Util.GPSUtils;
//import com.example.mobilemechanic.databinding.FragmentHomeBinding;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.bottomsheet.BottomSheetDialog;
//import com.google.gson.JsonObject;
//import com.mapbox.android.core.permissions.PermissionsListener;
//import com.mapbox.android.core.permissions.PermissionsManager;
//import com.mapbox.api.directions.v5.DirectionsCriteria;
//import com.mapbox.api.directions.v5.MapboxDirections;
//import com.mapbox.api.directions.v5.models.DirectionsResponse;
//import com.mapbox.api.directions.v5.models.DirectionsRoute;
//import com.mapbox.api.geocoding.v5.models.CarmenFeature;
//import com.mapbox.geojson.Feature;
//import com.mapbox.geojson.FeatureCollection;
//import com.mapbox.geojson.LineString;
//import com.mapbox.geojson.Point;
//import com.mapbox.mapboxsdk.Mapbox;
//import com.mapbox.mapboxsdk.camera.CameraPosition;
//import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
//import com.mapbox.mapboxsdk.geometry.LatLng;
//import com.mapbox.mapboxsdk.geometry.LatLngBounds;
//import com.mapbox.mapboxsdk.location.LocationComponent;
//import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
//import com.mapbox.mapboxsdk.location.LocationComponentOptions;
//import com.mapbox.mapboxsdk.location.modes.CameraMode;
//import com.mapbox.mapboxsdk.location.modes.RenderMode;
//import com.mapbox.mapboxsdk.maps.MapboxMap;
//import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
//import com.mapbox.mapboxsdk.maps.Style;
//import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
//import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
//import com.mapbox.mapboxsdk.style.layers.LineLayer;
//import com.mapbox.mapboxsdk.style.layers.Property;
//import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
//import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
//import com.mapbox.mapboxsdk.utils.BitmapUtils;
////com.example.mobilemechanic.home;
////        com.okellosoftwarez.letsmoveclient.ui.home;
////import com.okellosoftwarez.letsmoveclient.R;
//import com.example.mobilemechanic.R;
////import com.okellosoftwarez.letsmoveclient.Utils.GPSUtils;
////import com.okellosoftwarez.letsmoveclient.customModes.customAdapter;
////import com.okellosoftwarez.letsmoveclient.customModes.customDialog;
////import com.okellosoftwarez.letsmoveclient.databinding.FragmentHomeBinding;
////import com.okellosoftwarez.letsmoveclient.ui.dashboard.DashboardFragment;
////import com.okellosoftwarez.letsmoveclient.ui.sharedViewModel.homeDashboardViewModel;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//import static com.mapbox.core.constants.Constants.PRECISION_6;
//import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
////import static com.okellosoftwarez.letsmoveclient.Utils.GPSUtils.GPS_REQUEST;
//
//public class HomeFragment extends Fragment {
//
//    private HomeViewModel homeViewModel;
////    private BottomNavigationView bottomNavigationView;
//    //    private HomeViewModel homeViewModel;
//    private FragmentHomeBinding homeBinding;
//    private MapboxMap homeMapBoxMap;
//    private static final int LOCATION_PERMISSION = 5;
//    private boolean isGPS = false;
//
//    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
//    private static final int REQUEST_CODE_DESTINATION = 2;
//    private CarmenFeature home;
//    private CarmenFeature work;
//    private String geojsonSourceLayerId = "geojsonSourceLayerId";
//    private String geoDestinationId = "destLayerId";
//    private String symbolIconId = "symbolIconId";
//
//    private Point origin, destination;
//    private static final String ROUTE_LAYER_ID = "route-layer-id";
//    private static final String ROUTE_SOURCE_ID = "route-source-id";
//    private MapboxDirections client;
//    private DirectionsRoute currentRoute;
//
//    private String sourceLocation, destinationLocation;
//
//
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//
//        Mapbox.getInstance(getContext().getApplicationContext(), getString(R.string.public_token));
//
//        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);
//        View view = homeBinding.getRoot();
//
////        bottomNavigationView = getActivity().findViewById(R.id.nav_view);
//
////        Menu menu = bottomNavigationView.getMenu();
////        if (menu != null){
////            MenuItem item = menu.findItem(R.id.navigation_home);
////            item.setChecked(true);
////        }
//
//
//        new GPSUtils(getContext()).turnGPSOn(new GPSUtils.onGpsListener() {
//            @Override
//            public void gpsStatus(boolean isGPSEnable) {
////                 turn on GPS
//                isGPS = isGPSEnable;
//            }
//        });
//
//
//        homeBinding.homeMapView.onCreate(savedInstanceState);
//        homeBinding.homeMapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull MapboxMap mapboxMap) {
//
//                homeMapBoxMap = mapboxMap;
//                homeMapBoxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
//                    @Override
//                    public void onStyleLoaded(@NonNull Style style) {
//
//                        if (checkLocationPermission()) {
//                            Toast.makeText(getContext(), "Permission are on...", Toast.LENGTH_SHORT).show();
//                            enableLocationComponent(style);
//                        } else {
//                            Toast.makeText(getContext(), "Requesting for permission", Toast.LENGTH_SHORT).show();
//                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                    LOCATION_PERMISSION);
//                        }
//                        addUserLocations();
//
//                        homeBinding.destinationText.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Toast.makeText(getContext(), "Destination Clicked", Toast.LENGTH_SHORT).show();
//
//                                initDestinationPlaces();
//
//                            }
//                        });
//
//                        homeBinding.sourceText.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Toast.makeText(getContext(), "Source Clicked", Toast.LENGTH_SHORT).show();
//
//                                initSearchPlaces();
//
////                                if (origin != null && destination != null) {
////                                    boundView();
////                                }
//                            }
//                        });
//
//
//                        // Add the symbol layer icon to map for future use
//                        // Add the red marker icon image to the map
//                        style.addImage(symbolIconId, BitmapUtils.getBitmapFromDrawable(
//                                getResources().getDrawable(R.drawable.marker_location)));
//
//                        // Create an empty GeoJSON source using the empty feature collection
//                        setUpSource(style);
//
//                        setUpDestination(style);
//
//                        setRouteLayer(style);
////                        if (origin != null && destination != null){
////                            boundView();
////                        }
//
//                    }
//                });
//            }
//        });
//
////        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
//        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
//
//        return view;
//    }
//
//    private void setRouteLayer(Style style) {
//        //        Add the route sources to the map
//        style.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));
//
////        Add the route layer using lineLayer(This layer will display the directions route) to the map
//        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);
//        routeLayer.setProperties(
//                lineCap(Property.LINE_CAP_ROUND),
//                lineJoin(Property.LINE_JOIN_ROUND),
//                lineWidth(5f),
//                lineColor(Color.parseColor("#009688"))
//        );
//        style.addLayer(routeLayer);
//    }
//
//    private void boundView() {
//        LatLngBounds latLngBounds = new LatLngBounds.Builder()
//                .include(new LatLng(origin.latitude(), origin.longitude()))
//                .include(new LatLng(destination.latitude(), destination.longitude()))
//                .build();
//
//        homeMapBoxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 10));
//
//    }
//
//    @SuppressLint("MissingPermission")
//    private void enableLocationComponent(Style style) {
//
//        LocationComponentOptions locationComponentOptions =
//                LocationComponentOptions.builder(getContext())
//                        .pulseEnabled(true)
//                        .pulseColor(Color.parseColor("#86C0ED"))
//                        .foregroundTintColor(Color.BLUE)
//                        .pulseAlpha(.4f)
//                        .pulseInterpolator(new BounceInterpolator())
//                        .build();
//
//        LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
//                .builder(getContext(), style)
//                .locationComponentOptions(locationComponentOptions)
//                .build();
//
////            Get an instance of locationComponent
//        LocationComponent locationComponent = homeMapBoxMap.getLocationComponent();
//
////            Activate location component with options
//        locationComponent.activateLocationComponent(locationComponentActivationOptions);
//
////            Enable to make the component visible
//        locationComponent.setLocationComponentEnabled(true);
////                locationComponent.zoomWhileTracking(12, 1000);
//
////            set the component camera mode
//        locationComponent.setCameraMode(CameraMode.TRACKING);
//
////            set the component render mode
//        locationComponent.setRenderMode(RenderMode.COMPASS);
//    }
//
//
//    private void initSearchPlaces() {
//
//        Intent intent = new PlaceAutocomplete.IntentBuilder()
//                .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.public_token))
//                .placeOptions(PlaceOptions.builder()
//                        .backgroundColor(Color.parseColor("#EEEEEE"))
//                        .limit(10)
//                        .addInjectedFeature(home)
//                        .addInjectedFeature(work)
//                        .build(PlaceOptions.MODE_CARDS))
//                .build(getActivity());
//
//        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
//
//    }
//
//    private void initDestinationPlaces() {
//
//        Intent intent = new PlaceAutocomplete.IntentBuilder()
//                .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.public_token))
//                .placeOptions(PlaceOptions.builder()
//                        .backgroundColor(Color.parseColor("#EEEEEE"))
//                        .limit(10)
//                        .addInjectedFeature(home)
//                        .addInjectedFeature(work)
//                        .build(PlaceOptions.MODE_CARDS))
//                .build(getActivity());
//
//        startActivityForResult(intent, REQUEST_CODE_DESTINATION);
//
//    }
//
//    private void addUserLocations() {
//        home = CarmenFeature.builder().text("Mapbox SF Office")
//                .geometry(Point.fromLngLat(-122.3964485, 37.7912561))
//                .placeName("50 Beale St, San Francisco, CA")
//                .id("mapbox-sf")
//                .properties(new JsonObject())
//                .build();
//
//        work = CarmenFeature.builder().text("OKELLO DC Office")
//                .placeName("740 15th Street NW, Washington DC")
//                .geometry(Point.fromLngLat(-77.0338348, 38.899750))
//                .id("mapbox-dc")
//                .properties(new JsonObject())
//                .build();
//    }
//
//    private void setUpSource(Style style) {
//
//        if (origin == null) {
//            style.addSource(new GeoJsonSource(geojsonSourceLayerId));
//        } else {
//            //        Add the marker sources to the map
//            style.addSource(new GeoJsonSource(geojsonSourceLayerId,
//                    FeatureCollection.fromFeatures(new Feature[]{
//                            Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
//                    })));
//
//            if (destination != null) {
//                boundView();
//            }
//        }
//        // Add the red marker icon SymbolLayer to the map
//        style.addLayer(new SymbolLayer("layer-id",
//                geojsonSourceLayerId).withProperties(
//                iconImage(symbolIconId),
//                iconOffset(new Float[]{0f, -8f})
//        ));
//
//    }
//
//    private void setUpDestination(Style style) {
//
//        if (destination == null) {
//            style.addSource(new GeoJsonSource(geoDestinationId));
//        } else {
//            //        Add the marker sources to the map
//            style.addSource(new GeoJsonSource(geoDestinationId,
//                    FeatureCollection.fromFeatures(new Feature[]{
//                            Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude())),
//                    })));
//
//            if (origin != null) {
//                boundView();
//            }
//        }
//        // Add the red marker icon SymbolLayer to the map
//        style.addLayer(new SymbolLayer("layer-idDestination",
//                geoDestinationId).withProperties(
//                iconImage(symbolIconId),
//                iconOffset(new Float[]{0f, -8f})
//        ));
//
//    }
//
//    /**
//     * Make a request to the Mapbox Directions API. Once successful, pass the route to the
//     * route layer.
//     *
//     * @param mapboxMap   the Mapbox map object that the route will be drawn on
//     * @param origin      the starting point of the route
//     * @param destination the desired finish point of the route
//     */
//    private void getRoutes(MapboxMap mapboxMap, Point origin, Point destination) {
//        client = MapboxDirections.builder()
//                .origin(origin)
//                .destination(destination)
//                .overview(DirectionsCriteria.OVERVIEW_FULL)
//                .profile(DirectionsCriteria.PROFILE_DRIVING)
//                .accessToken(getString(R.string.public_token))
//                .build();
//
//        client.enqueueCall(new Callback<DirectionsResponse>() {
//
////            private customDialog dialog;
//
//            @Override
//            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
//                // You can get the generic HTTP info about the response
//                if (response.body() == null) {
//                    Toast.makeText(getActivity(), "No routes found, make sure you set the right user and access token.", Toast.LENGTH_SHORT).show();
//                    return;
//
//                } else if (response.body().routes().size() < 1) {
//                    Toast.makeText(getActivity(), "No routes found", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
////                Get the directions route
//                currentRoute = response.body().routes().get(0);
//
////                textView.setText("Distance : " + currentRoute.distance() + "            Time : " + currentRoute.duration() );
//                // Make a toast which displays the route's distance
////                Toast.makeText(getActivity(), "Total Distance : " + currentRoute.distance()
////                        + "   Estimated Time : " + currentRoute.duration(), Toast.LENGTH_LONG).show();
//
//                if (mapboxMap != null) {
//                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
//                        @Override
//                        public void onStyleLoaded(@NonNull Style style) {
//                            // Retrieve and update the source designated for showing the directions route
//                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);
//
////              Create a LineString with the directions route's geometry and
////              reset the GeoJSON source for the route LineLayer source
//                            if (source != null) {
//                                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
//                            }
//                        }
//                    });
//                }
//                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
//                bottomSheetDialog.setContentView(R.layout.home_sheet_dialog);
//                bottomSheetDialog.setCanceledOnTouchOutside(true);
//
//                TextView transportMode = bottomSheetDialog.findViewById(R.id.tvTransportMode);
//                EditText sourceText = bottomSheetDialog.findViewById(R.id.etSourceContact);
//                EditText destinationText = bottomSheetDialog.findViewById(R.id.etDestinationContact);
//                Button buttonContinue = bottomSheetDialog.findViewById(R.id.continueBtn);
//
////                transportMode.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View view) {
////                        AlertDialog.Builder vehicleDialog = new AlertDialog.Builder(getContext());
////                        vehicleDialog.setTitle("Modes of Transport");
////                        vehicleDialog.setMessage("Select the size and type of vehicle that is appropriate for your load ?");
//////                        vehicleDialog.setView();
////                        vehicleDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialogInterface, int i) {
////                                Toast.makeText(getContext(), "Okay Pressed", Toast.LENGTH_SHORT).show();
////                            }
////                        });
////
////                        vehicleDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialogInterface, int i) {
////                                Toast.makeText(getContext(), "Cancel pressed", Toast.LENGTH_SHORT).show();
////                            }
////                        });
////                        vehicleDialog.show();
////
////                    }
////                });
//
//                transportMode.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        customDialog dialog = new customDialog(getContext());
//                        dialog.show();
//
//                    }
//                });
//
//                buttonContinue.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (sourceText.getText().toString().trim() != null && destinationText.getText().toString().trim() != null) {
//
//                            homeViewModel.setDistance(currentRoute.distance());
//                            homeViewModel.setSourceLocationLongitude(origin.longitude());
//                            homeViewModel.setSourceLocationLatitude(origin.latitude());
//                            homeViewModel.setDestinationLocationLongitude(destination.longitude());
//                            homeViewModel.setDestinationLocationLatitude(destination.latitude());
//                            homeViewModel.setTime(currentRoute.distance(), customAdapter.transportType);
//                            homeViewModel.setMode(customAdapter.transportType);
//                            homeViewModel.setSourceContact(sourceText.getText().toString().trim());
//                            homeViewModel.setDestinationContact(destinationText.getText().toString().trim());
//                            homeViewModel.setDestinationLocation(destinationLocation);
//                            homeViewModel.setSourceLocation(sourceLocation);
//
//                            FragmentManager homeFragmentManager = getParentFragmentManager();
//                            FragmentTransaction homeFragmentTransaction = homeFragmentManager.beginTransaction();
//                            homeFragmentTransaction.replace(R.id.nav_host_fragment, DashboardFragment.class, null);
//                            homeFragmentTransaction.setReorderingAllowed(true);
//                            homeFragmentTransaction.addToBackStack("home");
//                            homeFragmentTransaction.commit();
//
//                            bottomSheetDialog.cancel();
//                        } else {
//                            Toast.makeText(getContext(), "The Contact People are Missing", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                });
//
//                bottomSheetDialog.show();
//
//            }
//
//            @Override
//            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
//
//                Toast.makeText(getActivity(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        homeBinding.homeMapView.onStart();
//    }
//
//    @Override
//    public void onResume() {
//
//        if (origin != null && destination != null) {
//            boundView();
//
//            getRoutes(homeMapBoxMap, origin, destination);
//
//        }
//        super.onResume();
//        homeBinding.homeMapView.onResume();
//    }
//
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        homeBinding.homeMapView.onPause();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        homeBinding.homeMapView.onStop();
//    }
//
//    @Override
//
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        homeBinding.homeMapView.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        homeBinding.homeMapView.onLowMemory();
//    }
//
//
//    @Override
//    public void onDestroyView() {
//        homeBinding.homeMapView.onDestroy();
//        homeBinding = null;
//        super.onDestroyView();
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == GPS_REQUEST) {
//                isGPS = true; // flag maintain before get location
//            }
//        }
//
//        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
//
//            // Retrieve selected location's CarmenFeature
//            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);
////            Log.d(" Origin : ", "onActivityResult: " + selectedCarmenFeature.placeName());
//            sourceLocation = selectedCarmenFeature.placeName();
//            homeBinding.sourceText.setText(sourceLocation);
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
//            homeBinding.destinationText.setText(destinationLocation);
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
//    }
//
//    public boolean checkLocationPermission() {
//        boolean isPermEnabled = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED);
//        return isPermEnabled;
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case LOCATION_PERMISSION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted, yay! Do the
//                    // location-related task you need to do.
//                    Toast.makeText(getContext(), "Permission are granted", Toast.LENGTH_SHORT).show();
//
//                    homeMapBoxMap.getStyle(new Style.OnStyleLoaded() {
//                        @Override
//                        public void onStyleLoaded(@NonNull Style style) {
//                            enableLocationComponent(style);
//                        }
//                    });
//
//                } else {
//
//                    Toast.makeText(getContext(), "Permission are denied", Toast.LENGTH_SHORT).show();
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//
//                }
//
//            }
//
//        }
//    }
//}