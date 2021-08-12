package com.example.mobilemechanic.viewScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.mobilemechanic.R;
import com.example.mobilemechanic.bridge.FirstScreen;

public class Welcome extends AppCompatActivity {
    Button welcomeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hid
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
// e the title
        getSupportActionBar().hide();
        setContentView(R.layout.activity_welcome);

        welcomeBtn = findViewById(R.id.welcomeBtn);

        welcomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent welcomeIntent = new Intent(Welcome.this, FirstScreen.class);
                startActivity(welcomeIntent);
            }
        });
    }
}