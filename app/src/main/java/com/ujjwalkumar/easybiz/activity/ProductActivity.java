package com.ujjwalkumar.easybiz.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ujjwalkumar.easybiz.R;
import com.ujjwalkumar.easybiz.adapter.ItemAdapter;
import com.ujjwalkumar.easybiz.helper.Item;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> filtered = new ArrayList<>();

    private ImageView backBtn,addItemBtn,shareItemsBtn;
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
        shareItemsBtn = findViewById(R.id.shareItemsBtn);
        listviewItem = findViewById(R.id.listviewItem);
        loadingAnimation = findViewById(R.id.loadingAnimation);

        backBtn.setOnClickListener(view -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), DashboardActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        addItemBtn.setOnClickListener(view -> {
            // get xml view
            LayoutInflater li = LayoutInflater.from(ProductActivity.this);
            View promptsView = li.inflate(R.layout.dialog_add_item, null);
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
                            (dialog, id) -> {
                                // get user input
                                String itemID = dbref.push().getKey();
                                String name = userInput1.getText().toString();
                                String price = userInput2.getText().toString();
                                String weight = userInput3.getText().toString();

                                Item item = new Item(itemID, name, price, weight);
                                dbref.child(itemID).setValue(item);
                                Toast.makeText(ProductActivity.this, "Adding " + name, Toast.LENGTH_SHORT).show();
                                loadList();
                            })
                    .setNegativeButton("Cancel",
                            (dialog, id) -> dialog.cancel());

            // create alert dialog and show it
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        });

        shareItemsBtn.setOnClickListener(view -> {
            Toast.makeText(ProductActivity.this, "Sharing catalogue coming soon!", Toast.LENGTH_SHORT).show();
            loadList();
        });

        listviewItem.setOnItemLongClickListener((adapterView, view, i, l) -> {
            SharedPreferences details = getSharedPreferences("user", Activity.MODE_PRIVATE);
            if(details.getString("type", "").equals("Admin"))
            {
                AlertDialog.Builder delete = new AlertDialog.Builder(ProductActivity.this);
                delete.setTitle("Delete");
                delete.setMessage("Do you want to delete " + filtered.get(i).get("name") + " ?");
                delete.setPositiveButton("Yes", (_dialog, _which) -> {
                    dbref.child(filtered.get(i).get("id")).removeValue();
                    Toast.makeText(ProductActivity.this, filtered.get(i).get("name") + " removed", Toast.LENGTH_SHORT).show();
                });
                delete.setNegativeButton("No", (_dialog, _which) -> {

                });
                delete.create().show();
            }
            return false;
        });

        loadList();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exit = new AlertDialog.Builder(this);
        exit.setTitle("Exit");
        exit.setMessage("Do you want to exit?");
        exit.setPositiveButton("Yes", (_dialog, _which) -> {
            Intent inf = new Intent();
            inf.setAction(Intent.ACTION_VIEW);
            inf.setClass(getApplicationContext(), DashboardActivity.class);
            inf.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(inf);
            finish();
        });
        exit.setNegativeButton("No", (_dialog, _which) -> {

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
                    listviewItem.setAdapter(new ItemAdapter(ProductActivity.this, filtered));
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
}