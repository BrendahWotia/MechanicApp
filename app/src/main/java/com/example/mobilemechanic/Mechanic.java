package com.example.mobilemechanic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Mechanic extends AppCompatActivity {
    EditText name, phone, location, email;
    Button uploadButton;
    ImageView mechImage;
    String nameMech, phoneMech, locationMech, emailMech;
    FirebaseDatabase database ;
    DatabaseReference mechanicReference ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic);


        database = FirebaseDatabase.getInstance();
        mechanicReference = database.getReference("Mechanics");
        name = findViewById(R.id.etName);
        phone = findViewById(R.id.etPhone);
        location = findViewById(R.id.etLocation);
        email = findViewById(R.id.etEmail);
        uploadButton = findViewById(R.id.uploadDBtn);
        mechImage = findViewById(R.id.imageViewMechanic);

        nameMech = name.getText().toString().trim();
        phoneMech = phone.getText().toString().trim();
        locationMech = location.getText().toString().trim();
        emailMech = email.getText().toString().trim();
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDetails();
            }
        });
    }

    private void uploadDetails() {

//        DatabaseReference MechanicRef = FirebaseDatabase.getInstance().getReference("Mechanics");
        String key = mechanicReference.getKey();
        MechanicModel  specificMech = new MechanicModel(nameMech, phoneMech, locationMech, emailMech, "https:image");
        mechanicReference.child(key).setValue(specificMech);


    }
}
