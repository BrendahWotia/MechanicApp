package com.example.mobilemechanic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstScreen extends AppCompatActivity {
Button signIn, signOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        signIn = findViewById(R.id.signInBtn);
        signOut = findViewById(R.id.signUpBtn);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = new Intent(FirstScreen.this, com.example.mobilemechanic.signIn.class);
                startActivity(signInIntent);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signOutIntent = new Intent(FirstScreen.this, com.example.mobilemechanic.signUp.class);
                startActivity(signOutIntent);
            }
        });
    }
}
