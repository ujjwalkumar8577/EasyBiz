package com.ujjwalkumar.easybiz.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.ujjwalkumar.easybiz.R;
import com.ujjwalkumar.easybiz.adapter.CartItemAdapter;
import com.ujjwalkumar.easybiz.helper.Cart;
import com.ujjwalkumar.easybiz.helper.CartItem;
import com.ujjwalkumar.easybiz.helper.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AddOrderActivity extends AppCompatActivity {

    String custID,orderID,name,user,lat,lng,area,address,contact,cartLmp;
    private String[] customers;                                                 // array of name of customers
    private ArrayList<HashMap<String, String>> filtered = new ArrayList<>();    // map of items with qty
    private ArrayList<HashMap<String, String>> clmp = new ArrayList<>();        // map of customers
    private ArrayList<String> al = new ArrayList<>();                           // name of customers
    private final ArrayList<CartItem> items = new ArrayList<>();                      // CartItem
    private Cart cart = new Cart();

    private ImageView backBtn;
    private AutoCompleteTextView autoCompleteName;
    private ListView listview;
    private TextView textviewamt;
    private Button addBtn,clearBtn;
    private LottieAnimationView loadingAnimation;

    private AlertDialog.Builder exit;
    private SharedPreferences details;
    private final FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private final DatabaseReference dbref = fbdb.getReference("customers");
    private final DatabaseReference dbref2 = fbdb.getReference("items");
    private final DatabaseReference dbref3 = fbdb.getReference("orders");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        backBtn = findViewById(R.id.backBtn);
        addBtn = findViewById(R.id.addBtn);
        clearBtn = findViewById(R.id.clearBtn);
        listview = findViewById(R.id.listview);
        autoCompleteName = findViewById(R.id.autoCompleteName);
        textviewamt = findViewById(R.id.textviewamt);
        loadingAnimation = findViewById(R.id.loadingAnimation);
        exit = new AlertDialog.Builder(this);
        details = getSharedPreferences("user", Activity.MODE_PRIVATE);

        clear();
        loadCustomers();
        loadList();

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
                HashMap<String, String> tmp = new HashMap<>();

                name = autoCompleteName.getText().toString();
                if(!name.equals("")) {
                    if(cart.getCartAmount()>0) {
                        for(HashMap<String, String> map : clmp) {
                            if(map.containsKey("name") && map.get("name").equals(name)) {
                                tmp = map;
                                custID = map.get("custID");
                            }
                        }

                        if(custID.equals("-1")) {
                            Toast.makeText(AddOrderActivity.this, "Select customer from list or add new customer", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            orderID = dbref3.push().getKey();
                            user = details.getString("name", "");
                            lat = tmp.get("lat");
                            lng = tmp.get("lng");
                            area = tmp.get("area");
                            address = tmp.get("address");
                            contact = tmp.get("contact");

                            ArrayList<HashMap<String, String>> alCart = new ArrayList<>();
                            for(HashMap<String, String> mp: filtered) {
                                if(Integer.parseInt(mp.get("qty"))>0)
                                    alCart.add(mp);
                            }
                            cartLmp = new Gson().toJson(alCart);

                            Order order = new Order(orderID,name,user,lat,lng,area,address,contact,cartLmp);
                            Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(Long.parseLong(order.getDelTime()));
                            String key = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());

                            dbref3.child(key).child(orderID).setValue(order);
                            Toast.makeText(AddOrderActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                            clear();
                        }
                    }
                    else {
                        Toast.makeText(AddOrderActivity.this, "Cart empty", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(AddOrderActivity.this, "Customer name required", Toast.LENGTH_SHORT).show();
                }

            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });
    }

    @Override
    public void onBackPressed() {
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

    private void clear() {
        cart = new Cart();
        name = "";
        custID = "-1";
        autoCompleteName.setText("");
        setAmount();
        loadList();
    }

    private void loadCustomers() {
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                al = new ArrayList<>();
                clmp = new ArrayList<>();
                try {
                    GenericTypeIndicator<HashMap<String, String>> ind = new GenericTypeIndicator<HashMap<String, String>>() {
                    };
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, String> map = data.getValue(ind);
                        clmp.add(map);
                        al.add(map.get("name"));
                    }

                    customers = new String[al.size()];
                    customers = al.toArray(customers);

                    //Creating the instance of ArrayAdapter containing list of customers
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, customers);

                    //Getting the instance of AutoCompleteTextView
                    autoCompleteName.setThreshold(2);                       //will start working from first character
                    autoCompleteName.setAdapter(adapter);                   //setting the adapter data into the AutoCompleteTextView

                    //Toast.makeText(AddOrderActivity.this, "Got customer list", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Toast.makeText(AddOrderActivity.this, "An exception occurred", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddOrderActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadList() {
        loadingAnimation.setVisibility(View.VISIBLE);
        dbref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                filtered = new ArrayList<>();
                try {
                    GenericTypeIndicator<HashMap<String, String>> ind = new GenericTypeIndicator<HashMap<String, String>>() {
                    };
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, String> map = data.getValue(ind);
                        CartItem tmp = new CartItem(map.get("id"), map.get("name"), map.get("price"), map.get("weight"), "0");
                        items.add(tmp);
//                        map.put("qty","0");
//                        filtered.add(map);
                    }
                    loadingAnimation.setVisibility(View.GONE);
                    listview.setAdapter(new CartItemAdapter(AddOrderActivity.this, items));
                    ((BaseAdapter)listview.getAdapter()).notifyDataSetChanged();
                }
                catch (Exception e) {
                    Toast.makeText(AddOrderActivity.this, "An exception occurred", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError _databaseError) {
            }
        });
    }

    void setAmount() {
        textviewamt.setText(String.valueOf(cart.getCartAmount()));
    }
}