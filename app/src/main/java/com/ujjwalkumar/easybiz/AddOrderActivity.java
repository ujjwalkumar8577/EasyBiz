package com.ujjwalkumar.easybiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

    private ImageView backBtn;
    private AutoCompleteTextView autoCompleteName;
    private ListView listview;
    private TextView textviewamt;
    private Button addBtn,clearBtn;

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

            int qty = Integer.parseInt(textviewItemQty.getText().toString());

            textviewItemName.setText(filtered.get(position).get("name").toString());
            textviewItemPrice.setText(filtered.get(position).get("price").toString());
            textviewItemQty.setText(qty);



            return v;
        }
    }

}