package com.example.mobilemechanic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Mechanic extends AppCompatActivity {
    EditText name, phone, location, email;
    Uri image_uri;
    Button uploadButton, imageBtn;
    ImageView mechImage;
    String nameMech, phoneMech, locationMech, emailMech;
    FirebaseDatabase database ;
    DatabaseReference mechanicReference ;
    MechanicModel mechanic ;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ProgressBar mProgress;
    public static final int IMAGE_REQUEST = 1;
    private static final int PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("New Mechanic");

        storageReference = FirebaseStorage.getInstance().getReference("Mechanics");
        databaseReference = FirebaseDatabase.getInstance().getReference("Mechanics");
//        database = FirebaseDatabase.getInstance();
//        mechanicReference = database.getReference("Mechanics");
        name = findViewById(R.id.etName);
        phone = findViewById(R.id.etPhone);
        location = findViewById(R.id.etLocation);
        email = findViewById(R.id.etEmail);
        uploadButton = findViewById(R.id.uploadDBtn);
        mechImage = findViewById(R.id.imageViewMechanic);
        imageBtn = findViewById(R.id.imageDBtn);
        mProgress = findViewById(R.id.progressBar);


        mechanic = new MechanicModel();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mProgress.setVisibility(View.VISIBLE);
//                uploadDetails();
                receiveEntries();
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosingPhoto();
            }
        });
    }

    private void uploadDetails() {
        if (image_uri != null) {
            okelloModel();
        } else {
            Toast.makeText(this, "Empty Uri...Select an Image", Toast.LENGTH_SHORT).show();
        }
    }

    private void okelloModel() {
//        Toast.makeText(this, "Start okello....", Toast.LENGTH_LONG).show();

        StorageReference photoReference = storageReference.child(System.currentTimeMillis() + "."
                + getFileExtension(image_uri));

        UploadTask uploadTask = photoReference.putFile(image_uri);

        Toast.makeText(this, "UP " + uploadTask, Toast.LENGTH_SHORT).show();

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                // Handle unsuccessful uploads
                Toast.makeText(Mechanic.this, "Fail...okello", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(Mechanic.this, "Success...okello", Toast.LENGTH_LONG).show();

                if (taskSnapshot.getMetadata() != null) {
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Toast.makeText(Mechanic.this, "Proceed...", Toast.LENGTH_LONG).show();
                                String sImage = uri.toString();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgress.setProgress(0);
                                    }
                                }, 500);
                                Toast.makeText(Mechanic.this, "Upload Successful..." + sImage, Toast.LENGTH_SHORT).show();

                                mechanic = new MechanicModel(nameMech, phoneMech,locationMech, emailMech, sImage);
//                                product = new Products(sName, sPhone, sLocation, sImage, sPrice, sCapacity, sEmail);
                                String key = databaseReference.push().getKey();
//                                product.setID(key);
                                mechanic.setId(key);
                                databaseReference.child(key).setValue(mechanic);

                                Toast.makeText(Mechanic.this, "Success Key retention...", Toast.LENGTH_LONG).show();
                                backToProfile(key);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(Mechanic.this, "Database Fail...", Toast.LENGTH_SHORT).show();
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

    private void backToProfile(String key) {
//        Intent backIntent = new Intent(this, Products_view.class);
//        backIntent.putExtra("phone", product.getPhone());
//        backIntent.putExtra("name", product.getName());
//        backIntent.putExtra("location", product.getLocation());
//        backIntent.putExtra("price", product.getPrice());
//        backIntent.putExtra("capacity", product.getCapacity());
//        backIntent.putExtra("mail", product.getEmail());
//        backIntent.putExtra("image", product.getImage());
//        backIntent.putExtra("key", key);
//
//        startActivity(backIntent);
        Toast.makeText(this, "Upload Done Go to Profile", Toast.LENGTH_SHORT).show();
    }
//    private void uploadDetails() {
////        receiveEntries();
//
//
////        DatabaseReference MechanicRef = FirebaseDatabase.getInstance().getReference("Mechanics");
//        String key = mechanicReference.getKey();
//        MechanicModel  specificMech = new MechanicModel(nameMech, phoneMech, locationMech, emailMech, "https:image");
//        mechanicReference.child(key).setValue(specificMech);
//
//
//    }

    private void receiveEntries() {
        nameMech = name.getText().toString().trim();
        phoneMech = phone.getText().toString().trim();
        locationMech = location.getText().toString().trim();
        emailMech = email.getText().toString().trim();

        checkFields();
    }

    private void checkFields() {
            if (nameMech.isEmpty() || phoneMech.isEmpty() || locationMech.isEmpty() || emailMech.isEmpty() || image_uri == null) {
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
    }

}
