package com.example.mobilemechanic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Driver extends AppCompatActivity {

    TextView defaultView;
    DatabaseReference mDatabase;
    FirebaseStorage storage;
    RecyclerView recyclerView;
    mechanicsAdapter adapter;
    LinearLayoutManager layoutManager;
    List<MechanicModel> mechanicList;
    List<MechanicModel> filteredProductsList;
    EditText searchText;
    String pPhone;
    ProgressBar circleP_bar;
    FirebaseAuth signOutmAuth;
    MechanicModel personalProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Mechanics List");

        defaultView = findViewById(R.id.defaultView);

        circleP_bar = findViewById(R.id.progressBarCircle);
        //        Obtaining reference to the firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference("Mechanics");

        storage = FirebaseStorage.getInstance();

        mechanicList = new ArrayList<>();
        filteredProductsList = new ArrayList<>();
//        nameList = new ArrayList<>();
        recyclerView = findViewById(R.id.driverRecycler);
        recyclerView.setHasFixedSize(true);

//       Initializing and Setting up of layout Manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

//        Initializing and Setting up of adapter
        adapter = new mechanicsAdapter(Driver.this, mechanicList);
        recyclerView.setAdapter(adapter);

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
                        defaultView.setVisibility(View.VISIBLE);
                        circleP_bar.setVisibility(View.INVISIBLE);
                    }

                    adapter.notifyDataSetChanged();
                    circleP_bar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(Driver.this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                    Toast.makeText(Driver.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    circleP_bar.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            circleP_bar.setVisibility(View.INVISIBLE);
            defaultView.setVisibility(View.VISIBLE);
            defaultView.setText(R.string.No_network);
            Toast.makeText(this, "Okello Check out", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.logOut:
//                logging out implementation
                Toast.makeText(this, "Log Out Implementation Coming Soon", Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);


        return true;
    }

}
