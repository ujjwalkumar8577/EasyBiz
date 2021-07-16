package com.ujjwalkumar.easybiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.ujjwalkumar.easybiz.helper.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AddOrderActivity extends AppCompatActivity {

    String custID,orderID,name,user,lat,lng,area,address,contact,cartLmp;
    private String[] customers;
    private ArrayList<HashMap<String, String>> filtered = new ArrayList<>();
    private ArrayList<HashMap<String, String>> clmp = new ArrayList<>();
    private ArrayList<String> al = new ArrayList<>();
    private HashMap<String, String> itm = new HashMap<>();
    private ArrayList<HashMap<String, String>> cart = new ArrayList<>();
    private double amt = 0;

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
                in.setClass(getApplicationContext(),Dashboard.class);
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
                    if(amt!=0) {
                        for(HashMap<String, String> map : clmp) {
                            if(map.containsKey("name")&&map.get("name").equals(name)) {
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
                            cartLmp = new Gson().toJson(cart);

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

    private void clear() {
        cart = new ArrayList<>();
        amt = 0;
        name = "";
        custID = "-1";
        autoCompleteName.setText("");
        setAmount(amt);
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
                        map.put("qty","0");
                        filtered.add(map);
                    }
                    loadingAnimation.setVisibility(View.GONE);
                    listview.setAdapter(new AddOrderActivity.ListviewItemAdapter3(filtered));
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

    public class ListviewItemAdapter3 extends BaseAdapter {
        ArrayList<HashMap<String, String>> data;

        public ListviewItemAdapter3(ArrayList<HashMap<String, String>> arr) {
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
                v = inflater.inflate(R.layout.items3, null);
            }

            final TextView textviewItemName = v.findViewById(R.id.textviewItemName);
            final TextView textviewItemPrice = v.findViewById(R.id.textviewItemPrice);
            final EditText textviewItemQty = v.findViewById(R.id.textviewItemQty);
            final ImageView imageviewminus = v.findViewById(R.id.imageviewminus);
            final ImageView imageviewplus = v.findViewById(R.id.imageviewplus);

            textviewItemName.setText(filtered.get(position).get("name"));
            textviewItemPrice.setText(filtered.get(position).get("price"));
            textviewItemQty.setText(filtered.get(position).get("qty"));

            imageviewplus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    itm = filtered.get(position);
                    textviewItemQty.setText(String.valueOf((long) (Double.parseDouble(textviewItemQty.getText().toString()) + 1)));
                    itm.put("qty", textviewItemQty.getText().toString());
                    amt = amt + Double.parseDouble(filtered.get(position).get("price"));
                    setAmount(amt);
                    for (int i = 0; i < cart.size(); i++) {
                        if (cart.get(i).get("id").equals(filtered.get(position).get("id"))) {
                            cart.remove(i);
                            break;
                        }
                    }
                    cart.add(itm);
                }
            });

            imageviewminus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    if (Double.parseDouble(textviewItemQty.getText().toString()) > 0) {
                        itm = filtered.get(position);
                        textviewItemQty.setText(String.valueOf((long) (Double.parseDouble(textviewItemQty.getText().toString()) - 1)));
                        itm.put("qty", textviewItemQty.getText().toString());
                        amt = amt - Double.parseDouble(filtered.get(position).get("price"));
                        setAmount(amt);
                        for (int i = 0; i < cart.size(); i++) {
                            if (cart.get(i).get("id").equals(filtered.get(position).get("id"))) {
                                cart.remove(i);
                                break;
                            }
                        }
                        if (Double.parseDouble(textviewItemQty.getText().toString()) > 0) {
                            cart.add(itm);
                        }
                    }
                }
            });

            textviewItemQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    itm = filtered.get(position);
                    if(!charSequence.equals("")) {
                        int q = Integer.parseInt(charSequence.toString());
                        if (q>0) {
                            itm.put("qty", Integer.toString(q));
                            filtered.set(position, itm);
                        }
                        else {
                            itm.put("qty", Integer.toString(0));
                            filtered.set(position, itm);
                        }
                    }
                    else {
                        itm.put("qty", Integer.toString(0));
                        filtered.set(position, itm);
                    }

                    setAmount(calculateAmount());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            return v;
        }
    }

    double calculateAmount() {
        double qty, price;
        amt = 0.0;
        cart = new ArrayList<>();
        for (int i = 0; i < filtered.size(); i++) {
            if(filtered.get(i).get("qty")!=null && filtered.get(i).get("price")!=null) {
                qty = Double.parseDouble(filtered.get(i).get("qty"));
                price = Double.parseDouble(filtered.get(i).get("price"));
                if (qty>0) {
                    amt += qty * price;
                    cart.add(filtered.get(i));
                }
            }
        }
        return  amt;
    }

    void setAmount(double amount) {
        textviewamt.setText(String.valueOf(amount));
    }
}