package com.ujjwalkumar.easybiz.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ujjwalkumar.easybiz.R;
import com.ujjwalkumar.easybiz.adapter.EstimateAdapter;
import com.ujjwalkumar.easybiz.helper.Order;

import java.util.ArrayList;
import java.util.HashMap;

public class EstimateActivity extends AppCompatActivity {

    private String userType = "Staff";
    private String curDate = "";
    private final String key = "";
    private ArrayList<HashMap<String, String>> items = new ArrayList<>();
    private ArrayList<HashMap<String, String>> filtered = new ArrayList<>();
    private final ArrayList<HashMap<String, String>> cart = new ArrayList<>();

    private ImageView backBtn;
    private ListView listviewEstimate;
    private DatePicker datepicker;
    private LottieAnimationView loadingAnimation;

    private SharedPreferences details;
    private final FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private final DatabaseReference dbref = fbdb.getReference("estimates");
    private final DatabaseReference dbref2 = fbdb.getReference("items");

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }

        backBtn = findViewById(R.id.backBtn);
        listviewEstimate = findViewById(R.id.listviewEstimate);
        datepicker = findViewById(R.id.datepicker);
        loadingAnimation = findViewById(R.id.loadingAnimation);
        details = getSharedPreferences("user", Activity.MODE_PRIVATE);
        curDate = getCurDate(datepicker.getYear(),datepicker.getMonth(),datepicker.getDayOfMonth());
        userType = details.getString("type", "");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1000);
        }

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

        datepicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                curDate = getCurDate(i,i1,i2);
                loadList();
            }
        });

        loadItems();
        loadList();
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

    private String getCurDate(int y, int m, int d) {
        String tmp = Integer.toString(y);
        m = m+1;
        if(m/10==0)
            tmp = tmp + "0" + m;
        else
            tmp = tmp + m;
        if(d/10==0)
            tmp = tmp + "0" + d;
        else
            tmp = tmp + d;

        return tmp;
    }

    private void loadItems() {
        dbref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items = new ArrayList<>();
                try {
                    GenericTypeIndicator<HashMap<String, String>> ind = new GenericTypeIndicator<HashMap<String, String>>() {
                    };
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, String> map = data.getValue(ind);
                        items.add(map);
                    }
                }
                catch (Exception e) {
                    Toast.makeText(EstimateActivity.this, "Failed to load items", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EstimateActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadList() {
        loadingAnimation.setVisibility(View.VISIBLE);
        dbref.child(curDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                filtered = new ArrayList<>();
                try {
                    GenericTypeIndicator<HashMap<String, String>> ind = new GenericTypeIndicator<HashMap<String, String>>() {
                    };
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, String> map = data.getValue(ind);
                        if(map.containsKey("status")&&map.get("status").equals(Order.STATUS_PENDING))
                            filtered.add(map);
                    }
                    loadingAnimation.setVisibility(View.GONE);
                    listviewEstimate.setAdapter(new EstimateAdapter(EstimateActivity.this, userType, curDate, filtered));
                    ((BaseAdapter)listviewEstimate.getAdapter()).notifyDataSetChanged();
                }
                catch (Exception e) {
                    Toast.makeText(EstimateActivity.this, "An exception occurred", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EstimateActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}