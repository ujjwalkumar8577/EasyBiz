package com.ujjwalkumar.easybiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ujjwalkumar.easybiz.helper.Customer;
import com.ujjwalkumar.easybiz.util.GoogleMapController;

public class AddCustomerActivity extends AppCompatActivity {

    String custID,name,user,lat,lng,img,area,address,contact;
    boolean locationSet = false;
    private double latitude = 0;
    private double longitude = 0;

    private ImageView backBtn;
    private EditText edittextName,edittextContact,edittextAddress;
    private Spinner spinnerArea;
    private MapView mapView;
    private Button addBtn,clearBtn;

    private LocationManager loc;
    private GoogleMapController mapview1_controller;
    private SharedPreferences details;
    private FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private DatabaseReference dbref = fbdb.getReference("customers");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        com.google.firebase.FirebaseApp.initializeApp(this);

        backBtn = findViewById(R.id.backBtn);
        addBtn = findViewById(R.id.addBtn);
        clearBtn = findViewById(R.id.clearBtn);
        edittextName = findViewById(R.id.edittextName);
        edittextContact = findViewById(R.id.edittextContact);
        edittextAddress = findViewById(R.id.edittextAddress);
        spinnerArea = findViewById(R.id.spinnerArea);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        locationSet = false;
        details = getSharedPreferences("user", Activity.MODE_PRIVATE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        loc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent();
                in.setAction(Intent.ACTION_VIEW);
                in.setClass(getApplicationContext(), Dashboard.class);
                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(in);
                finish();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = edittextName.getText().toString();
                contact = edittextContact.getText().toString();
                address = edittextAddress.getText().toString();

                if(!name.equals(""))
                {
                    if(!contact.equals(""))
                    {
                        if(!address.equals(""))
                        {
                            if(locationSet)
                            {
                                if(spinnerArea.getSelectedItemPosition()!=0)
                                {
                                    custID = dbref.push().getKey();
                                    user = details.getString("name", "");
                                    lat = String.valueOf(latitude);
                                    lng = String.valueOf(longitude);
                                    img = "";
                                    area = spinnerArea.getSelectedItem().toString();

                                    Customer customer = new Customer(custID,name,user,lat,lng,img,area,address,contact);
                                    dbref.child(custID).setValue(customer);
                                }
                                else
                                {
                                    Toast.makeText(AddCustomerActivity.this, "Select area", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(AddCustomerActivity.this, "Location not updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(AddCustomerActivity.this, "Address required", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(AddCustomerActivity.this, "Contact required", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(AddCustomerActivity.this, "Name required", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edittextName.setText("");
                edittextContact.setText("");
                edittextAddress.setText("");
                spinnerArea.setSelection(0);
                locationSet = false;
            }
        });

        mapview1_controller = new GoogleMapController(mapView, new OnMapReadyCallback() {

            @Override
            public void onMapReady(final GoogleMap googleMap) {

                if (ActivityCompat.checkSelfPermission(AddCustomerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddCustomerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddCustomerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
                    return;
                }

                googleMap.setMyLocationEnabled(true);
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(25.00d, 81.00d))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                        .draggable(true));
                mapview1_controller.setGoogleMap(googleMap);
                mapview1_controller.moveCamera(25.00d, 81.00d);
                mapview1_controller.zoomTo(18);

                googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        LatLng current = marker.getPosition();
                        latitude = current.latitude;
                        longitude = current.longitude;
                        // imageview1.setImageResource(R.drawable.ic_gps_fixed_black);
                        locationSet = true;
                        Toast.makeText(AddCustomerActivity.this, "Location updated", Toast.LENGTH_SHORT).show();
                    }
                });

                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        Location currentLocation = googleMap.getMyLocation();
                        if (currentLocation != null)
                        {
                            latitude = currentLocation.getLatitude();
                            longitude = currentLocation.getLongitude();
                            // imageview1.setImageResource(R.drawable.ic_gps_fixed_black);
                            locationSet = true;
                            Toast.makeText(AddCustomerActivity.this, "Location updated", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            currentLocation = googleMap.getMyLocation();
                            Toast.makeText(AddCustomerActivity.this, "Couldn't update location", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
            }
        });


    }
}