package com.ujjwalkumar.easybiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.ujjwalkumar.easybiz.helper.Item;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> filtered = new ArrayList<>();

    private ImageView backBtn,addItemBtn,syncItemBtn;
    private ListView listviewItem;
    private LottieAnimationView loadingAnimation;
    
    private final FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private final DatabaseReference dbref = fbdb.getReference("items");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        backBtn = findViewById(R.id.backBtn);
        addItemBtn = findViewById(R.id.addItemBtn);
        syncItemBtn = findViewById(R.id.syncItemBtn);
        listviewItem = findViewById(R.id.listviewItem);
        loadingAnimation = findViewById(R.id.loadingAnimation);

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

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get xml view
                LayoutInflater li = LayoutInflater.from(ProductActivity.this);
                View promptsView = li.inflate(R.layout.add_item_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductActivity.this);
                alertDialogBuilder.setView(promptsView);

                // get user input
                final EditText userInput1 = promptsView.findViewById(R.id.editTextDialogUserInput1);
                final EditText userInput2 = promptsView.findViewById(R.id.editTextDialogUserInput2);
                final EditText userInput3 = promptsView.findViewById(R.id.editTextDialogUserInput3);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input
                                        String itemID = dbref.push().getKey();
                                        String name = userInput1.getText().toString();
                                        String price = userInput2.getText().toString();
                                        String weight = userInput3.getText().toString();

                                        Item item = new Item(itemID,name,price,weight);
                                        dbref.child(itemID).setValue(item);
                                        Toast.makeText(ProductActivity.this, "Adding "+name, Toast.LENGTH_SHORT).show();
                                        loadList();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog and show it
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        syncItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProductActivity.this, "Syncing items ...", Toast.LENGTH_SHORT).show();
                loadList();
            }
        });

        listviewItem.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences details = getSharedPreferences("user", Activity.MODE_PRIVATE);
                if(details.getString("type", "").equals("Admin"))
                {
                    AlertDialog.Builder delete = new AlertDialog.Builder(ProductActivity.this);
                    delete.setTitle("Delete");
                    delete.setMessage("Do you want to delete " + filtered.get(i).get("name") + " ?");
                    delete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {
                            dbref.child(filtered.get(i).get("id")).removeValue();
                            Toast.makeText(ProductActivity.this, filtered.get(i).get("name") + " removed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    delete.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {

                        }
                    });
                    delete.create().show();
                }
                return false;
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

    private void loadList() {
        loadingAnimation.setVisibility(View.VISIBLE);
        dbref.addValueEventListener(new ValueEventListener() {
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
                    loadingAnimation.setVisibility(View.GONE);
                    listviewItem.setAdapter(new ListviewItemAdapter1(filtered));
                    ((BaseAdapter)listviewItem.getAdapter()).notifyDataSetChanged();
                }
                catch (Exception e) {
                    Toast.makeText(ProductActivity.this, "An exception occurred", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProductActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class ListviewItemAdapter1 extends BaseAdapter {
        ArrayList<HashMap<String, String>> data;

        public ListviewItemAdapter1(ArrayList<HashMap<String, String>> arr) {
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
                v = inflater.inflate(R.layout.items1, null);
            }

            final TextView textViewItemName = v.findViewById(R.id.textViewItemName);
            final TextView textViewItemPrice = v.findViewById(R.id.textViewItemPrice);
            final TextView textViewItemWeight = v.findViewById(R.id.textViewItemWeight);

            textViewItemName.setText(filtered.get(position).get("name"));
            textViewItemPrice.setText(filtered.get(position).get("price"));
            textViewItemWeight.setText(filtered.get(position).get("weight"));

            return v;
        }
    }
}