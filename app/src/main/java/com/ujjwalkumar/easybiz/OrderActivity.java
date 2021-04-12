package com.ujjwalkumar.easybiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderActivity extends AppCompatActivity {

    private String curDate = "";
    private HashMap<String, String> mp = new HashMap<>();
    private ArrayList<HashMap<String, String>> filtered = new ArrayList<>();
    private ArrayList<HashMap<String, String>> cart = new ArrayList<>();

    private ImageView backBtn;
    private ListView listviewOrder;
    private DatePicker datepicker;

    private FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private DatabaseReference dbref = fbdb.getReference("orders");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        com.google.firebase.FirebaseApp.initializeApp(this);

        backBtn = findViewById(R.id.backBtn);
        listviewOrder = findViewById(R.id.listviewOrder);
        datepicker = findViewById(R.id.datepicker);
        curDate = getCurDate(datepicker.getYear(),datepicker.getMonth(),datepicker.getDayOfMonth());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1000);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent();
                in.setAction(Intent.ACTION_VIEW);
                in.setClass(getApplicationContext(),Dashboard.class);
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
                inf.setClass(getApplicationContext(), Dashboard.class);
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
            tmp = tmp + "0" + Integer.toString(m);
        else
            tmp = tmp + Integer.toString(m);
        tmp = tmp + Integer.toString(d);

        return tmp;
    }

    private void loadList() {
        dbref.child(curDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                filtered = new ArrayList<>();
                try {
                    GenericTypeIndicator<HashMap<String, String>> ind = new GenericTypeIndicator<HashMap<String, String>>() {
                    };
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, String> map = data.getValue(ind);
                        filtered.add(map);
                    }
                    listviewOrder.setAdapter(new OrderActivity.ListviewOrderAdapter(filtered));
                    ((BaseAdapter)listviewOrder.getAdapter()).notifyDataSetChanged();
                }
                catch (Exception e) {
                    Toast.makeText(OrderActivity.this, "An exception occurred", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OrderActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class ListviewOrderAdapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> data;

        public ListviewOrderAdapter(ArrayList<HashMap<String, String>> arr) {
            data = arr;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public HashMap<String, String> getItem(int index) {
            return data.get(index);
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = view;
            if (v == null) {
                v = inflater.inflate(R.layout.orders, null);
            }

            final TextView textview1 = (TextView) v.findViewById(R.id.textview1);
            final TextView textview2 = (TextView) v.findViewById(R.id.textview2);
            final TextView textview3 = (TextView) v.findViewById(R.id.textview3);
            final ImageView imageviewCall = (ImageView) v.findViewById(R.id.imageviewCall);
            final ImageView imageview1Dir = (ImageView) v.findViewById(R.id.imageviewDir);

            double lat = Double.parseDouble(filtered.get(position).get("lat").toString());
            double lng = Double.parseDouble(filtered.get(position).get("lng").toString());

            cart = new Gson().fromJson(filtered.get(position).get("cart"),new TypeToken<ArrayList<HashMap<String, String>>>() {
            }.getType());
            String tmpOrder = "";

            for(HashMap<String, String> map : cart) {
                tmpOrder = tmpOrder + map.get("qty") + " * " + map.get("name") + "\n";
            }

            textview1.setText(filtered.get(position).get("name").toString());
            textview2.setText(filtered.get(position).get("area").toString());
            textview3.setText(tmpOrder);

            imageviewCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    Intent inv = new Intent();
                    inv.setAction(Intent.ACTION_CALL);
                    inv.setData(Uri.parse("tel:".concat(filtered.get(position).get("contact").toString())));
                    startActivity(inv);
                }
            });
            imageview1Dir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    Intent inv = new Intent();
                    inv.setAction(Intent.ACTION_VIEW);
                    inv.setData(Uri.parse("google.navigation:q=".concat(String.valueOf(lat).concat(",".concat(String.valueOf(lng))))));
                    if(inv.resolveActivity(getPackageManager())!=null) {
                        startActivity(inv);
                    }
                    else
                    {
                        Toast.makeText(OrderActivity.this, "No app found for navigation", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return v;
        }
    }


}