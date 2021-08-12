package com.ujjwalkumar.easybiz.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ujjwalkumar.easybiz.R;
import com.ujjwalkumar.easybiz.util.GoogleMapController;

import java.util.ArrayList;
import java.util.HashMap;

public class StaffActivity extends AppCompatActivity {

    private String uid,name;
    private ArrayList<HashMap<String, String>> filtered;

    private ImageView backBtn;
    private TextView textviewName,textviewLastSeen;
    private MapView mapView;

    private GoogleMapController mapviewController;
    private final FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private final DatabaseReference dbref = fbdb.getReference("locations");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        backBtn = findViewById(R.id.backBtn);
        textviewName = findViewById(R.id.textviewName);
        textviewLastSeen = findViewById(R.id.textviewLastseen);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        uid = getIntent().getStringExtra("uid");
        name = getIntent().getStringExtra("name");
        textviewName.setText(name);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent();
                in.setAction(Intent.ACTION_VIEW);
                in.setClass(getApplicationContext(), MyAccountActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(in);
                finish();
            }
        });

        mapviewController = new GoogleMapController(mapView, new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mapviewController.setGoogleMap(googleMap);

                dbref.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        filtered = new ArrayList<>();
                        try {
                            GenericTypeIndicator<HashMap<String, String>> ind = new GenericTypeIndicator<HashMap<String, String>>() {
                            };
                            for (DataSnapshot data : snapshot.getChildren()) {
                                HashMap<String, String> map = data.getValue(ind);
                                double lat = Double.parseDouble(map.get("lat"));
                                double lng = Double.parseDouble(map.get("lng"));
                                String time = map.get("time");           // using it as id or tag also

                                mapviewController.zoomTo(15);
                                mapviewController.moveCamera(lat,lng);
                                mapviewController.addMarker(time, lat, lng);
                                mapviewController.setMarkerInfo(time, time, "");
                                mapviewController.setMarkerIcon(time, R.drawable.ic_location_on_black);
                                textviewLastSeen.setText(time);
                                filtered.add(map);
                            }
                        }
                        catch (Exception e) {
                            Toast.makeText(StaffActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        if(filtered.size()==0)
                            textviewLastSeen.setText("Not Available");
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(StaffActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                inf.setClass(getApplicationContext(), MyAccountActivity.class);
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
}