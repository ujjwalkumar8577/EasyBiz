package com.ujjwalkumar.easybiz.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ujjwalkumar.easybiz.R;
import com.ujjwalkumar.easybiz.adapter.ItemAdapter;
import com.ujjwalkumar.easybiz.helper.Customer;
import com.ujjwalkumar.easybiz.helper.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ProductActivity extends AppCompatActivity {

    private boolean imageSet = false;
    private ArrayList<HashMap<String, String>> filtered = new ArrayList<>();

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 39;
    private ImageView imageView;

    private ImageView backBtn,addItemBtn,shareItemsBtn;
    private ListView listviewItem;
    private LottieAnimationView loadingAnimation;
    
    private final FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private final DatabaseReference dbref = fbdb.getReference("items");
    private final FirebaseStorage fbst = FirebaseStorage.getInstance();

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
            LayoutInflater li = LayoutInflater.from(ProductActivity.this);
            View promptsView = li.inflate(R.layout.dialog_add_item, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductActivity.this);
            alertDialogBuilder.setView(promptsView);

            imageView = promptsView.findViewById(R.id.imageView);
            final EditText userInput1 = promptsView.findViewById(R.id.editTextDialogUserInput1);
            final EditText userInput2 = promptsView.findViewById(R.id.editTextDialogUserInput2);
            final EditText userInput3 = promptsView.findViewById(R.id.editTextDialogUserInput3);

            imageView.setOnClickListener(view1 -> {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
            });

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Add",
                            (dialog, id) -> {
                                String itemID = dbref.push().getKey();
                                String name = userInput1.getText().toString();
                                String price = userInput2.getText().toString();
                                String weight = userInput3.getText().toString();

                                if (imageSet) {
                                    if (filePath != null) {
                                        // showing progressDialog while uploading
                                        ProgressDialog progressDialog = new ProgressDialog(ProductActivity.this);
                                        progressDialog.setTitle("Uploading image ...");
                                        progressDialog.show();

                                        // uploading file and adding listeners on upload or failure of image
                                        StorageReference stref = fbst.getReference("items").child(itemID);
                                        stref.putFile(filePath)
                                                .addOnSuccessListener(taskSnapshot -> {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(ProductActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                                })

                                                .addOnFailureListener(e -> {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(ProductActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                                                })

                                                .addOnProgressListener(taskSnapshot -> {
                                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                                })

                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        stref.getDownloadUrl().addOnSuccessListener(uri -> {
                                                            String downloadURL = uri.toString();
                                                            Item item = new Item(itemID, name, price, weight, downloadURL);
                                                            dbref.child(itemID).setValue(item);
                                                            Toast.makeText(ProductActivity.this, "Adding " + name, Toast.LENGTH_SHORT).show();
                                                            loadList();
                                                        });
                                                    }
                                                });
                                    }
                                } else {
                                    Item item = new Item(itemID, name, price, weight, "No image uploaded");
                                    dbref.child(itemID).setValue(item);
                                    Toast.makeText(ProductActivity.this, "Adding " + name, Toast.LENGTH_SHORT).show();
                                    loadList();
                                }
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
                delete.setPositiveButton("Yes", (dialog, which) -> {
                    dbref.child(filtered.get(i).get("id")).removeValue();
                    Toast.makeText(ProductActivity.this, filtered.get(i).get("name") + " removed", Toast.LENGTH_SHORT).show();
                });
                delete.setNegativeButton("No", (dialog, which) -> {

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

    private void loadList() {
        loadingAnimation.setVisibility(View.VISIBLE);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                filtered = new ArrayList<>();
                try {
                    GenericTypeIndicator<HashMap<String, String>> ind = new GenericTypeIndicator<HashMap<String, String>>() {};
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                imageSet = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}