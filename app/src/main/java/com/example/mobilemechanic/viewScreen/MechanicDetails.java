package com.example.mobilemechanic.viewScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidstudy.daraja.Daraja;
import com.androidstudy.daraja.DarajaListener;
import com.androidstudy.daraja.model.AccessToken;
import com.androidstudy.daraja.model.LNMExpress;
import com.androidstudy.daraja.model.LNMResult;
import com.androidstudy.daraja.util.TransactionType;
import com.example.mobilemechanic.R;
import com.squareup.picasso.Picasso;

public class MechanicDetails extends AppCompatActivity {
    private static final int CALL_PERMISSION = 30;
    String mechName, mechLocation, mechMail, mechImage, mechPhone, mechSpeciality, mechLatitude, mechLongitude;
    TextView tv_name, tv_location, tv_mail, tv_phone, tv_speciality;
    ImageView mechanicImage;
    private Daraja daraja;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Mechanic Details");

        tv_name = findViewById(R.id.tv_detailMechName);
        tv_location = findViewById(R.id.tv_detailMechLocation);
        tv_speciality = findViewById(R.id.tv_detailMechSpeciality);
        tv_mail = findViewById(R.id.tv_detailEmail);
        tv_phone = findViewById(R.id.tv_detailMechPhone);
        mechanicImage = findViewById(R.id.detailMechImage);
//        tv_latitude = findViewById(R.id.tv_latitude);
//        tv_longitude = findViewById(R.id.tv_longitude);

        receiveIntents();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
        phoneNumber = pref.getString("phone", null);
        //For Sandbox Mode
        daraja = Daraja.with("I4T05zcfAXKYRiunkGv5ZRslqKm5zNoP", "I4Sufz8fLAZxT67M", new DarajaListener<AccessToken>() {
            @Override
            public void onResult(@NonNull AccessToken accessToken) {
                Log.i(MechanicDetails.this.getClass().getSimpleName(), accessToken.getAccess_token());
                Toast.makeText(MechanicDetails.this, "AccessToken : " + accessToken.getAccess_token(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Log.e(MechanicDetails.this.getClass().getSimpleName(), error);
            }
        });
    }

    private void receiveIntents() {
        if (getIntent().hasExtra("name") && getIntent().hasExtra("location") && getIntent().hasExtra("mail")
                && getIntent().hasExtra("image") && getIntent().hasExtra("phone") && getIntent().hasExtra("speciality")) {

            mechName = getIntent().getStringExtra("name");
            mechLocation = getIntent().getStringExtra("location");
            mechMail = getIntent().getStringExtra("mail");
            mechImage = getIntent().getStringExtra("image");
            mechPhone = getIntent().getStringExtra("phone");
            mechSpeciality = getIntent().getStringExtra("speciality");
            mechLatitude = getIntent().getStringExtra("latitude");
            mechLongitude = getIntent().getStringExtra("longitude");

            provision(mechName, mechLocation, mechMail, mechImage, mechPhone, mechSpeciality, mechLatitude, mechLongitude);
        }
    }

    private void provision(String mechName, String mechLocation, String mechMail, String mechImage, String mechPhone,
                           String speciality, String mechLatitude, String mechLongitude) {
        tv_name.setText(mechName);
        tv_location.setText(mechLocation);
        tv_mail.setText(mechMail);
        tv_phone.setText(mechPhone);
        tv_speciality.setText(speciality);
//        tv_longitude.setText(mechLongitude);
//        tv_latitude.setText(mechLatitude);

        Picasso.get().load(mechImage).placeholder(R.drawable.ic_image_black_24dp)
                .into(mechanicImage);

//        Toast.makeText(this, "Url : " + mechImage, Toast.LENGTH_SHORT).show();
    }

    public void mailing(View view) {

        Intent mailIntent = new Intent(Intent.ACTION_SEND);
        mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {mechMail});
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, "REQUESTING FOR MECHANIC ASSISTANCE");
        mailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(mailIntent, "Choose Mail Client"));

    }

    public void direction(View view){
        Intent passIntent = new Intent(MechanicDetails.this, NavigationScreen.class);
//        passIntent.putExtra("name", currentMechanic.getName());
//        passIntent.putExtra("location", currentMechanic.getLocation());
//        passIntent.putExtra("mail", currentMechanic.getEmail());
//        passIntent.putExtra("image", currentMechanic.getImageUrl());
//        passIntent.putExtra("phone", currentMechanic.getPhone());
//        passIntent.putExtra("speciality", currentMechanic.getSpeciality());
        passIntent.putExtra("latitude", mechLatitude);
        passIntent.putExtra("longitude", mechLongitude);

        startActivity(passIntent);
    }

    public void payment(View view){
       numberConfirmationDialog();
    }


    private void initiateStkPush() {
        LNMExpress lnmExpress = new LNMExpress(
                "174379",
                "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919",
                TransactionType.CustomerPayBillOnline,
                "1",
                phoneNumber,
                "174379",
                phoneNumber,
                "https://okellomarket.000webhostapp.com/api/mcash",
                "Mechanic Payment",
                "Services Payment"
        );

        daraja.requestMPESAExpress(lnmExpress, new DarajaListener<LNMResult>() {
            @Override
            public void onResult(@NonNull LNMResult lnmResult) {
                Log.i(MechanicDetails.this.getClass().getSimpleName(), lnmResult.ResponseDescription);
//                Toast.makeText(MechanicDetails.this, "Result:" + lnmResult.CheckoutRequestID
//                        + "  Code : " + lnmResult.ResponseDescription, Toast.LENGTH_SHORT).show();

//                paymentToFarmer();
            }

            @Override
            public void onError(String error) {
                Log.i(MechanicDetails.this.getClass().getSimpleName(), error);
            }
        });
    }

    public void phoning(View view) {
        if (Build.VERSION.SDK_INT >= 23){
            if (checkedPermission()){
//                Permission Already Granted
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:" + mechPhone));
                startActivity(phoneIntent);
            }
            else {
                requestPermission();
            }
        }
        if (checkedPermission()){
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            phoneIntent.setData(Uri.parse("tel:" + mechPhone));
            startActivity(phoneIntent);
        }
    }

    private void numberConfirmationDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(Html.fromHtml("Confirmation"));
        alert.setMessage(Html.fromHtml("Confirm this is Your M pesa Number : "));

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);
        input.setText(phoneNumber);

        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//                value = input.getText().toString();

                new AsyncTask<Void, Void, String>(){

                    @Override
                    protected String doInBackground(Void... voids) {
                        initiateStkPush();
                        return null;
                    }
                }.execute();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled. uncomment
//                ratingDialog();
            }
        });

        alert.show();
    }



    private boolean checkedPermission() {
        int callPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);

        return callPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MechanicDetails.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CALL_PERMISSION :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    Toast.makeText(this, "Permission For Calling has been Accepted...", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Permission For Calling has been Denied...", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
