package com.ujjwalkumar.easybiz.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ujjwalkumar.easybiz.R;
import com.ujjwalkumar.easybiz.helper.Customer;

import java.io.IOException;
import java.util.UUID;

public class AddCustomerActivity extends AppCompatActivity {

    private String custID, name, user, lat, lng, img, area, address, contact;
    private String downloadURL = "";
    private boolean locationSet = false;
    private boolean imageSet = false;
    private double latitude = 0;
    private double longitude = 0;
    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 22;

    private ImageView backBtn, imageView;
    private EditText edittextName, edittextContact, edittextAddress;
    private Spinner spinnerArea;
    private MapView mapView;
    private Button addBtn, clearBtn;

    private LocationManager loc;
    private SharedPreferences details;
    private final FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private final DatabaseReference dbref = fbdb.getReference("customers");
    private final FirebaseStorage fbst = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        backBtn = findViewById(R.id.backBtn);
        addBtn = findViewById(R.id.addBtn);
        clearBtn = findViewById(R.id.clearBtn);
        edittextName = findViewById(R.id.edittextName);
        edittextContact = findViewById(R.id.edittextContact);
        edittextAddress = findViewById(R.id.edittextAddress);
        spinnerArea = findViewById(R.id.spinnerArea);
        imageView = findViewById(R.id.imageView);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        locationSet = false;
        imageSet = false;
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

        if (!loc.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS not enabled", Toast.LENGTH_SHORT).show();
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!imageSet) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent();
                in.setAction(Intent.ACTION_VIEW);
                in.setClass(getApplicationContext(), DashboardActivity.class);
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

                if (!name.equals("")) {
                    if (!contact.equals("")) {
                        if (!address.equals("")) {
                            if (locationSet) {
                                if (spinnerArea.getSelectedItemPosition() != 0) {

                                    if(imageSet) {
                                        uploadImage(filePath, UUID.randomUUID().toString());
                                    }
                                    else {
                                        custID = dbref.push().getKey();
                                        user = details.getString("name", "");
                                        lat = String.valueOf(latitude);
                                        lng = String.valueOf(longitude);
                                        img = downloadURL;
                                        area = spinnerArea.getSelectedItem().toString();

                                        Customer customer = new Customer(custID, name, user, lat, lng, img, area, address, contact);
                                        dbref.child(custID).setValue(customer);
                                        Toast.makeText(AddCustomerActivity.this, "Customer added successfully", Toast.LENGTH_SHORT).show();
                                        clear();
                                    }

                                } else {
                                    Toast.makeText(AddCustomerActivity.this, "Select area", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(AddCustomerActivity.this, "Location not updated", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AddCustomerActivity.this, "Address required", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddCustomerActivity.this, "Contact required", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddCustomerActivity.this, "Name required", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                if (ActivityCompat.checkSelfPermission(AddCustomerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddCustomerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddCustomerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
                    return;
                }

                googleMap.setMyLocationEnabled(true);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(25.0,81.0)));
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(18));

                Location currentLocation = googleMap.getMyLocation();
                if(currentLocation!=null)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude())));

                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        Location currentLocation = googleMap.getMyLocation();
                        if (currentLocation != null)
                        {
                            latitude = currentLocation.getLatitude();
                            longitude = currentLocation.getLongitude();
                            locationSet = true;
                            Toast.makeText(AddCustomerActivity.this, "Location updated", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(AddCustomerActivity.this, "Couldn't update location", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exit = new AlertDialog.Builder(this);
        exit.setTitle("Exit");
        exit.setMessage("Do you want to exit?");
        exit.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface _dialog, int _which) {
                Intent inf = new Intent();
                inf.setAction(Intent.ACTION_VIEW);
                inf.setClass(getApplicationContext(), DashboardActivity.class);
                inf.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(inf);
                finish();
            }
        });
        exit.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface _dialog, int _which) {

            }
        });
        exit.create().show();
    }

    // Required if using mapView
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    // Required if using mapView
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    // Required for selecting image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                imageSet = true;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void clear() {
        edittextName.setText("");
        edittextContact.setText("");
        edittextAddress.setText("");
        locationSet = false;
        imageSet = false;
        downloadURL = "";
        imageView.setImageResource(R.drawable.imageupload);
    }

    private void uploadImage(Uri filePath, String fileName) {
        if (filePath != null) {
            // showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading image ...");
            progressDialog.show();

            custID = dbref.push().getKey();
            // uploading file and adding listeners on upload or failure of image
            StorageReference stref = fbst.getReference("customers").child(custID);
            stref.putFile(filePath)
                .addOnSuccessListener(taskSnapshot -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddCustomerActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                })

                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddCustomerActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                })

                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int)progress + "%");
                })

                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {
                            stref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadURL = uri.toString();
                                    user = details.getString("name", "");
                                    lat = String.valueOf(latitude);
                                    lng = String.valueOf(longitude);
                                    img = downloadURL;
                                    area = spinnerArea.getSelectedItem().toString();

                                    Customer customer = new Customer(custID, name, user, lat, lng, img, area, address, contact);
                                    dbref.child(custID).setValue(customer);
                                    Toast.makeText(AddCustomerActivity.this, "Customer added successfully", Toast.LENGTH_SHORT).show();
                                    clear();
                                }
                            });
                        }
                    }
                });
        }
    }
}