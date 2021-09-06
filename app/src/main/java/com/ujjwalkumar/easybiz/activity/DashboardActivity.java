package com.ujjwalkumar.easybiz.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ujjwalkumar.easybiz.R;
import com.ujjwalkumar.easybiz.helper.MyLocation;

public class DashboardActivity extends AppCompatActivity {

    private TextView textviewUserName,textviewUserType;
    private ImageView myAccountBtn,feedbackBtn,aboutBtn;
    private CardView cardview1,cardview2,cardview3,cardview4,cardview5,cardview6,cardview7,cardview8;

    private SharedPreferences details;
    private final FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private final DatabaseReference dbref = fbdb.getReference("locations");
    private final Intent in = new Intent();
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        textviewUserName = findViewById(R.id.textviewUserName);
        textviewUserType = findViewById(R.id.textviewUserType);
        myAccountBtn = findViewById(R.id.myAccountBtn);
        feedbackBtn = findViewById(R.id.feedbackBtn);
        aboutBtn = findViewById(R.id.aboutBtn);
        cardview1 = findViewById(R.id.cardview1);
        cardview2 = findViewById(R.id.cardview2);
        cardview3 = findViewById(R.id.cardview3);
        cardview4 = findViewById(R.id.cardview4);
        cardview5 = findViewById(R.id.cardview5);
        cardview6 = findViewById(R.id.cardview6);
        cardview7 = findViewById(R.id.cardview7);
        cardview8 = findViewById(R.id.cardview8);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        details = getSharedPreferences("user", Activity.MODE_PRIVATE);
        textviewUserName.setText(details.getString("name", ""));
        textviewUserType.setText(details.getString("type", ""));

        myAccountBtn.setOnClickListener(view -> {
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), MyAccountActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        feedbackBtn.setOnClickListener(view -> {
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), FeedbackActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        aboutBtn.setOnClickListener(view -> {
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), AboutActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        cardview1.setOnClickListener(view -> {
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), AddEstimateActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        cardview2.setOnClickListener(view -> {
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), EstimateActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        cardview3.setOnClickListener(view -> {
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), AddOrderActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        cardview4.setOnClickListener(view -> {
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), OrderActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        cardview5.setOnClickListener(view -> {
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), AddCustomerActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        cardview6.setOnClickListener(view -> {
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), CustomerActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        cardview7.setOnClickListener(view -> {
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), CashCalculatorActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        cardview8.setOnClickListener(view -> {
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), ProductActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        //if(!details.getString("type", "").equals("Admin"))
            updateMyLocation();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exit = new AlertDialog.Builder(this);
        exit.setTitle("Exit");
        exit.setMessage("Do you want to exit?");
        exit.setPositiveButton("Yes", (_dialog, _which) -> finish());
        exit.setNegativeButton("No", (_dialog, _which) -> {

        });
        exit.create().show();
    }

    private void updateMyLocation() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("Location error","GPS not enabled");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        String uid = details.getString("uid", "");
                        String lat = Double.toString(location.getLatitude());
                        String lng = Double.toString(location.getLongitude());
                        String acc = Double.toString(location.getAccuracy());
                        long time = location.getTime();

                        if (!(lat.equals(details.getString("lat", ""))) || !(lng.equals(details.getString("lng", ""))) || !(acc.equals(details.getString("acc", "")))) {
                            details.edit().putString("lat", lat).apply();
                            details.edit().putString("lng", lng).apply();
                            details.edit().putString("acc", acc).apply();
                            MyLocation myLocation = new MyLocation(uid, lat, lng, acc, time);
                            dbref.child(uid).child(dbref.push().getKey()).setValue(myLocation);
                        }
                    }
                    else {
                        Log.d("Location error","Null location");
                    }
                });
    }
}