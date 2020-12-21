package com.example.mobilemechanic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button mechanic, driver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mechanic = findViewById(R.id.mechanicBtn);
        driver = findViewById(R.id.driverBtn);

        mechanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                To Mechanic Activity
                Intent mechanicIntent = new Intent(MainActivity.this, Mechanic.class);
                startActivity(mechanicIntent);
            }
        });

        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                To driver Activity
                Intent driverIntent = new Intent(MainActivity.this, Driver.class);
                startActivity(driverIntent);
            }
        });
    }
}
