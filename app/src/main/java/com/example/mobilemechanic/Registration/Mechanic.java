package com.example.mobilemechanic.Registration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mobilemechanic.Model.MechanicModel;
import com.example.mobilemechanic.R;
import com.example.mobilemechanic.viewScreen.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonObject;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.squareup.picasso.Picasso;

public class Mechanic extends AppCompatActivity {
    EditText name, phone, location, email;
    Uri image_uri;
    Button uploadButton ;
    ImageView mechImage;
    String nameMech, phoneMech, locationMech, emailMech, spec;
    String mechLatitude;
    String mechLongitude;
    FirebaseDatabase database ;
    DatabaseReference mechanicReference ;
    MechanicModel mechanic ;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ProgressBar mProgress;
    public static final int IMAGE_REQUEST = 1;
    private static final int PERMISSION_CODE = 1000;
    private AutoCompleteTextView autoCompleteTextView;
    private CarmenFeature home;
    private CarmenFeature work;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this,getString(R.string.public_token));
        setContentView(R.layout.activity_mechanic);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("New Mechanic");

//        Drop down list of mechanics
        String [] specification = new String[] {"Service Technicians", "Diagnostic Technicians", "Brake and Transmission Technicians", "Body Repair Technicians",
        "Vehicle Refinishers", "Vehicle Inspectors", "General"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown, specification);

        autoCompleteTextView = findViewById(R.id.specList);
        autoCompleteTextView.setAdapter(adapter);

        storageReference = FirebaseStorage.getInstance().getReference("Mechanics");
        databaseReference = FirebaseDatabase.getInstance().getReference("Mechanics");
//        database = FirebaseDatabase.getInstance();
//        mechanicReference = database.getReference("Mechanics");
        name = findViewById(R.id.etMechUserName);
        phone = findViewById(R.id.etMechPhone);
        location = findViewById(R.id.etMechLocation);
        email = findViewById(R.id.etMechMail);
        uploadButton = findViewById(R.id.uploadDBtn);
        mechImage = findViewById(R.id.imageViewMechanic);
//        imageBtn = findViewById(R.id.imageDBtn);
        mProgress = findViewById(R.id.progressBar);


        mechanic = new MechanicModel();

        addUserLocations();
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(Mechanic.this, "Location Clicked", Toast.LENGTH_SHORT).show();

                initSearchPlaces();
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mProgress.setVisibility(View.VISIBLE);
//                uploadDetails();
//                Toast.makeText(Mechanic.this, "Value : " + autoCompleteTextView.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                receiveEntries();
            }
        });

//        imageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                choosingPhoto();
//            }
//        });
    }

    private void initSearchPlaces() {

        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.public_token))
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .limit(10)
                        .addInjectedFeature(home)
                        .addInjectedFeature(work)
                        .build(PlaceOptions.MODE_CARDS))
                .build(Mechanic.this);

        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);

    }

    private void addUserLocations() {
        home = CarmenFeature.builder().text("Brendah's Home")
                .geometry(Point.fromLngLat(34.5, 0.3))
                .placeName("50 Beale St, San Francisco, CA")
                .id("mapbox-sf")
                .properties(new JsonObject())
                .build();

        work = CarmenFeature.builder().text("Brendah's DC Office")
                .placeName("740 15th Street NW, Washington DC")
                .geometry(Point.fromLngLat(-77.0338348, 38.899750))
                .id("mapbox-dc")
                .properties(new JsonObject())
                .build();
    }
    private void uploadDetails() {
        if (image_uri != null) {
            mProgress.setVisibility(View.VISIBLE);
            okelloModel();
        } else {
            Toast.makeText(this, "Select an Image", Toast.LENGTH_SHORT).show();
        }
    }

    private void okelloModel() {
//        Toast.makeText(this, "Start okello....", Toast.LENGTH_LONG).show();

        StorageReference photoReference = storageReference.child(System.currentTimeMillis() + "."
                + getFileExtension(image_uri));

        UploadTask uploadTask = photoReference.putFile(image_uri);

//        Toast.makeText(this, "UP " + uploadTask, Toast.LENGTH_SHORT).show();

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                // Handle unsuccessful uploads
                Toast.makeText(Mechanic.this, "Ooops Something went wrong Try Later", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

//                Toast.makeText(Mechanic.this, "Ooops Something went wrong Try Later", Toast.LENGTH_LONG).show();

                if (taskSnapshot.getMetadata() != null) {
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
//                                Toast.makeText(Mechanic.this, "Proceed...", Toast.LENGTH_LONG).show();
                                String sImage = uri.toString();

                                mProgress.setVisibility(View.VISIBLE);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgress.setProgress(0);
                                    }
                                }, 500);
                                Toast.makeText(Mechanic.this, "Upload Successful..." + sImage, Toast.LENGTH_SHORT).show();

                                mechanic = new MechanicModel(nameMech, phoneMech,locationMech, emailMech, sImage, spec, mechLongitude, mechLatitude);
                                String key = databaseReference.push().getKey();
                                mechanic.setId(key);
                                databaseReference.child(key).setValue(mechanic);

//                                Toast.makeText(Mechanic.this, "Success Key retention...", Toast.LENGTH_LONG).show();
                                mProgress.setVisibility(View.INVISIBLE);
                                backToProfile(nameMech, phoneMech,locationMech, emailMech, sImage, spec);
                                name.setText("");
                                location.setText("");
                                phone.setText("");
                                email.setText("");
                                Picasso.get().load("null").placeholder(R.drawable.ic_image_black_24dp).into(mechImage);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mProgress.setVisibility(View.INVISIBLE);
                                Toast.makeText(Mechanic.this, "Ooops Something went wrong Try Later", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                mProgress.setProgress((int) progress);
            }
        });

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = mime.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }

    private void backToProfile(String nameMech, String phoneMech, String locationMech, String emailMech, String imageMech, String special) {
        Intent backIntent = new Intent(this, Profile.class);
        backIntent.putExtra("phone", phoneMech);
        backIntent.putExtra("name", nameMech);
        backIntent.putExtra("location", locationMech);
//        backIntent.putExtra("price", pro;
//        backIntent.putExtra("capacity", product.getCapacity());
        backIntent.putExtra("mail", emailMech);
        backIntent.putExtra("image", imageMech);
        backIntent.putExtra("speciality", special);
//        backIntent.putExtra("key", key);
//        Toast.makeText(this, "Successful Upload of Details..", Toast.LENGTH_SHORT).show();
//
        startActivity(backIntent);
//        Toast.makeText(this, "Upload Done Go to Profile", Toast.LENGTH_SHORT).show();
    }

    private void receiveEntries() {
        nameMech = name.getText().toString().trim();
        phoneMech = phone.getText().toString().trim();
        locationMech = location.getText().toString().trim();
        emailMech = email.getText().toString().trim();
        spec = autoCompleteTextView.getText().toString().trim();

        checkFields();
    }

    private void checkFields() {
            if (nameMech.isEmpty() || phoneMech.isEmpty() || locationMech.isEmpty() || emailMech.isEmpty() || image_uri == null
            || spec.isEmpty()) {
                Toast.makeText(this, "Missing Fields...", Toast.LENGTH_SHORT).show();
            } else {

                if ((phoneMech.length()) < 10) {
                    Toast.makeText(this, "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    if (Integer.valueOf(emailMech) ) {
//                        Toast.makeText(this, "Please Enter a Valid Capacity", Toast.LENGTH_SHORT).show();
                    }
                else {

                        if (isNetworkConnected()) {
                            uploadDetails();
                        }

                        else {
                            Toast.makeText(Mechanic.this, R.string.No_network, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

    private boolean isNetworkConnected() {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();

    }

    private void choosingPhoto() {
        Intent chooseIntent = new Intent();
        chooseIntent.setType("image/*");
        chooseIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(chooseIntent, IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_REQUEST) {

                image_uri = data.getData();
                Picasso.get().load(image_uri).into(mechImage);

            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            // Retrieve selected location's CarmenFeature
//           if (data != null){
               CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);
//            Log.d(" Origin : ", "onActivityResult: " + selectedCarmenFeature.placeName());
               locationMech = selectedCarmenFeature.placeName();
               location.setText(locationMech);
               mechLongitude = Double.toString(((Point) selectedCarmenFeature.geometry()).longitude());
               mechLatitude = Double.toString(((Point) selectedCarmenFeature.geometry()).latitude());
//            homeBinding.sourceText.setText(sourceLocation);

//           }
//            origin = Point.fromLngLat(((Point) selectedCarmenFeature.geometry()).longitude(), ((Point) selectedCarmenFeature.geometry()).latitude());
            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
            // Then retrieve and update the source designated for showing a selected location's symbol layer icon



        }
                }

    public void selectPhoto(View view) {
        choosingPhoto();
    }
}

