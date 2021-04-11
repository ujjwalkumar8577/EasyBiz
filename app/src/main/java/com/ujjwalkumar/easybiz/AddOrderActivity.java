package com.ujjwalkumar.easybiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;

public class AddOrderActivity extends AppCompatActivity {

    private String name,custID;
    String[] customers;
    private HashMap<String, String> mp = new HashMap<>();
    private ArrayList<HashMap<String, String>> filtered = new ArrayList<>();
    private ArrayList<String> al = new ArrayList<>();

    private HashMap<String, String> tmp = new HashMap<>();
    private HashMap<String, String> seller = new HashMap<>();
    private HashMap<String, String> itm = new HashMap<>();
    private ArrayList<HashMap<String, String>> cart = new ArrayList<>();
    private double amt = 0;

    private ImageView backBtn;
    private AutoCompleteTextView autoCompleteName;
    private ListView listview;
    private TextView textviewamt;
    private Button addBtn,clearBtn;

    private AlertDialog.Builder exit;
    private FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private DatabaseReference dbref = fbdb.getReference("customers");
    private DatabaseReference dbref2 = fbdb.getReference("items");

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
        exit = new AlertDialog.Builder(this);

        loadCustomers();
        loadList();
        name = "";
        custID = "-1";

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

            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = "";
                custID = "-1";
                autoCompleteName.setText("");
                textviewamt.setText("0");
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

    private void loadCustomers() {

        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                al = new ArrayList<>();
                try {
                    GenericTypeIndicator<HashMap<String, String>> ind = new GenericTypeIndicator<HashMap<String, String>>() {
                    };
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, String> map = data.getValue(ind);
                        al.add(map.get("name"));
                    }

                    customers = new String[al.size()];
                    customers = al.toArray(customers);

                    //Creating the instance of ArrayAdapter containing list of language names
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_dropdown_item_1line,customers);

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
            public void onCancelled(DatabaseError _databaseError) {
            }
        });
    }


    private void loadList() {
        dbref2.addValueEventListener(new ValueEventListener() {
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

            final TextView textviewItemName = (TextView) v.findViewById(R.id.textviewItemName);
            final TextView textviewItemPrice = (TextView) v.findViewById(R.id.textviewItemPrice);
            final TextView textviewItemQty = (TextView) v.findViewById(R.id.textviewItemQty);
            final ImageView imageviewminus = (ImageView) v.findViewById(R.id.imageviewminus);
            final ImageView imageviewplus = (ImageView) v.findViewById(R.id.imageviewplus);

            int qty = Integer.parseInt(textviewItemQty.getText().toString());

            textviewItemName.setText(filtered.get(position).get("name").toString());
            textviewItemPrice.setText(filtered.get(position).get("price").toString());
            textviewItemQty.setText("0");

            imageviewplus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    itm = filtered.get(position);
                    textviewItemQty.setText(String.valueOf((long) (Double.parseDouble(textviewItemQty.getText().toString()) + 1)));
                    itm.put("qty", textviewItemQty.getText().toString());
                    amt = amt + Double.parseDouble(filtered.get((int) position).get("price").toString());
                    textviewamt.setText(textviewamt.getText().toString());
                    int t = 0;
                    for (int _repeat105 = 0; _repeat105 < (int) (cart.size()); _repeat105++) {
                        if (cart.get((int) t).get("id").toString().equals(filtered.get((int) position).get("id").toString())) {
                            cart.remove((int) (t));
                            break;
                        }
                        t++;
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
                        amt = amt - Double.parseDouble(filtered.get(position).get("price").toString());
                        textviewamt.setText(textviewamt.getText().toString());
                        int t = 0;
                        for (int _repeat150 = 0; _repeat150 < (int) (cart.size()); _repeat150++) {
                            if (cart.get((int) t).get("id").toString().equals(filtered.get((int) position).get("id").toString())) {
                                cart.remove((int) (t));
                                break;
                            }
                            t++;
                        }
                        if (Double.parseDouble(textviewItemQty.getText().toString()) > 0) {
                            cart.add(itm);
                        }
                    }
                }
            });

            return v;
        }
    }

}