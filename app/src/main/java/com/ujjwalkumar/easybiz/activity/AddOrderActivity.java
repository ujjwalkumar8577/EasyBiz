package com.ujjwalkumar.easybiz.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;
import com.ujjwalkumar.easybiz.R;
import com.ujjwalkumar.easybiz.helper.Product;
import com.ujjwalkumar.easybiz.helper.CartItem;
import com.ujjwalkumar.easybiz.helper.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddOrderActivity extends AppCompatActivity {

    String custID, orderID, name, user, lat, lng, area, address, contact, cartLmp;
    private String[] customers;                                                 // array of name of customers
    private ArrayList<HashMap<String, String>> clmp = new ArrayList<>();        // map of customers
    private ArrayList<String> al = new ArrayList<>();                           // name of customers
    private ArrayList<CartItem> items = new ArrayList<>();                      // map of items
    private Cart cart;

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

        cart = TinyCartHelper.getCart();
        clear();
        loadCustomers();
        loadItems();

        backBtn.setOnClickListener(view -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), DashboardActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        addBtn.setOnClickListener(view -> {
            HashMap<String, String> tmp = new HashMap<>();
            name = autoCompleteName.getText().toString();
            if(!name.equals("")) {
                if(!cart.isCartEmpty()) {
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
                        for(Map.Entry<Item, Integer> entry: cart.getAllItemsWithQty().entrySet()) {
                            HashMap<String, String> mp = new HashMap<>();
                            mp.put("name", entry.getKey().getItemName());
                            mp.put("qty", entry.getValue().toString());
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

        });

        clearBtn.setOnClickListener(view -> clear());
    }

    @Override
    public void onBackPressed() {
        exit.setTitle("Exit");
        exit.setMessage("Do you want to exit?");
        exit.setPositiveButton("Yes", (dialog, which) -> {
            Intent inf = new Intent();
            inf.setAction(Intent.ACTION_VIEW);
            inf.setClass(getApplicationContext(), DashboardActivity.class);
            inf.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(inf);
            finish();
        });
        exit.setNegativeButton("No", (dialog, which) -> {

        });
        exit.create().show();
    }

    private void clear() {
        cart.clearCart();
        name = "";
        custID = "-1";
        autoCompleteName.setText("");
        setAmount();
        loadItems();
    }

    void setAmount() {
        textviewamt.setText(String.format("%.2f", cart.getTotalPrice().doubleValue()));
    }

    private void loadCustomers() {
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                al = new ArrayList<>();
                clmp = new ArrayList<>();
                try {
                    GenericTypeIndicator<HashMap<String, String>> ind = new GenericTypeIndicator<HashMap<String, String>>() {};
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, String> map = data.getValue(ind);
                        clmp.add(map);
                        al.add(map.get("name"));
                    }

                    customers = new String[al.size()];
                    customers = al.toArray(customers);

                    //Creating the instance of ArrayAdapter containing list of customers
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_select_customer, customers);

                    //Getting the instance of AutoCompleteTextView
                    autoCompleteName.setThreshold(2);                       //will start working from first character
                    autoCompleteName.setAdapter(adapter);                   //setting the adapter data into the AutoCompleteTextView

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

    private void loadItems() {
        loadingAnimation.setVisibility(View.VISIBLE);
        dbref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items = new ArrayList<>();
                try {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Product item = data.getValue(Product.class);
                        CartItem tmp = new CartItem(item.getItemID(), item.getName(), Double.parseDouble(item.getPrice()), Double.parseDouble(item.getWeight()));
                        items.add(tmp);
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
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    class CartItemAdapter extends ArrayAdapter<CartItem> {

        public CartItemAdapter(Context context, ArrayList<CartItem> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            CartItem item = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_items2, parent, false);
            }

            TextView textviewItemName = convertView.findViewById(R.id.textviewItemName);
            TextView textviewItemPrice = convertView.findViewById(R.id.textviewItemPrice);
            EditText textviewItemQty = convertView.findViewById(R.id.textviewItemQty);
            ImageView imageviewminus = convertView.findViewById(R.id.imageviewminus);
            ImageView imageviewplus = convertView.findViewById(R.id.imageviewplus);

            textviewItemName.setText(item.getName());
            textviewItemPrice.setText(String.valueOf(item.getPrice()));
            int q;
            try {
                q = cart.getItemQty(item);
            }
            catch (Exception e) {
                q = 0;
            }
            textviewItemQty.setText(String.valueOf(q));

            imageviewplus.setOnClickListener(view -> {
                int qty;
                try {
                    qty = cart.getItemQty(item)+1;
                }
                catch (Exception e) {
                    qty = 1;
                }

                if(qty==1)
                    cart.addItem(item, 1);
                else
                    cart.updateItem(item, qty);
                textviewItemQty.setText(String.valueOf(qty));
                setAmount();
            });

            imageviewminus.setOnClickListener(view -> {
                int qty;
                try {
                    qty = cart.getItemQty(item)-1;
                }
                catch (Exception e) {
                    qty = 0;
                }

                if(qty==0) {
                    try {
                        cart.removeItem(item);
                    } catch (Exception ignored) {
                    }
                }
                else
                    cart.updateItem(item, qty);
                textviewItemQty.setText(String.valueOf(qty));
                setAmount();
            });

            textviewItemQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
//                    if(textviewItemQty.hasFocus()) {
//                        String str = editable.toString();
//                        int qty;
//                        try {
//                            qty = Integer.parseInt(str);
//                        } catch (Exception e) {
//                            qty = 0;
//                        }
//
//                        if(qty==0) {
//                            try {
//                                cart.removeItem(item);
//                            } catch (Exception ignored) { }
//                        }
//                        else if(qty==1) {
//                            try {
//                                cart.addItem(item, 1);
//                            }
//                            catch (Exception ignored) { }
//                        }
//                        else
//                            cart.updateItem(item, qty);
//                        textviewItemQty.setText(String.valueOf(qty));
//                        setAmount();
//                    }
                }
            });

            return convertView;
        }
    }
}