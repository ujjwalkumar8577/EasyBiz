package com.ujjwalkumar.easybiz.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ujjwalkumar.easybiz.R;
import com.ujjwalkumar.easybiz.adapter.CustomerAdapter;
import com.ujjwalkumar.easybiz.helper.Customer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomerActivity extends AppCompatActivity {

    private String userType = "Staff";
    private boolean imageSet = false;
    private ArrayList<HashMap<String, String>> filtered = new ArrayList<>();
    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 33;
    private ImageView imageView;

    private ImageView backBtn;
    private ListView listviewCustomer;
    private LottieAnimationView loadingAnimation;

    private final FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private final DatabaseReference dbref = fbdb.getReference("customers");
    private final FirebaseStorage fbst = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        backBtn = findViewById(R.id.backBtn);
        listviewCustomer = findViewById(R.id.listviewCustomer);
        loadingAnimation = findViewById(R.id.loadingAnimation);

        SharedPreferences details = getSharedPreferences("user", Activity.MODE_PRIVATE);
        userType = details.getString("type", "");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1000);
        }

        backBtn.setOnClickListener(view -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), DashboardActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        listviewCustomer.setOnItemClickListener((adapterView, view, i, l) -> {
            if (userType.equals("Admin")) {
                LayoutInflater li = LayoutInflater.from(CustomerActivity.this);
                View promptsView = li.inflate(R.layout.dialog_edit_customer, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CustomerActivity.this);
                alertDialogBuilder.setView(promptsView);

                imageView = promptsView.findViewById(R.id.imageView);
                final EditText userInput1 = promptsView.findViewById(R.id.editTextDialogUserInput1);
                final EditText userInput2 = promptsView.findViewById(R.id.editTextDialogUserInput2);
                final EditText userInput3 = promptsView.findViewById(R.id.editTextDialogUserInput3);
                final Spinner dialogSpinnerArea = promptsView.findViewById(R.id.dialogSpinnerArea);

                String area = filtered.get(i).get("area");
                int ind = Integer.parseInt("0" + area.charAt(area.length() - 1));
                userInput1.setText(filtered.get(i).get("name"));
                userInput2.setText(filtered.get(i).get("contact"));
                userInput3.setText(filtered.get(i).get("address"));
                dialogSpinnerArea.setSelection(ind);
                Glide.with(CustomerActivity.this).load(filtered.get(i).get("img"))
                        .placeholder(R.drawable.imageupload)
                        .into(imageView);

                imageView.setOnClickListener(view1 -> {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
                });

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Update",
                                (dialog, id) -> {
                                    String custID = filtered.get(i).get("custID");
                                    String name = userInput1.getText().toString();
                                    String user = details.getString("name", "");
                                    String lat = filtered.get(i).get("lat");
                                    String lng = filtered.get(i).get("lng");
                                    String img = filtered.get(i).get("img");
                                    String area1 = dialogSpinnerArea.getSelectedItem().toString();
                                    String address = userInput3.getText().toString();
                                    String contact = userInput2.getText().toString();

                                    if (!name.equals("") && !contact.equals("") && !address.equals("") && dialogSpinnerArea.getSelectedItemPosition() != 0) {
                                        if (imageSet) {
                                            if (filePath != null) {
                                                // showing progressDialog while uploading
                                                ProgressDialog progressDialog = new ProgressDialog(CustomerActivity.this);
                                                progressDialog.setTitle("Uploading image ...");
                                                progressDialog.show();

                                                // uploading file and adding listeners on upload or failure of image
                                                StorageReference stref = fbst.getReference("customers").child(custID);
                                                stref.putFile(filePath)
                                                        .addOnSuccessListener(taskSnapshot -> {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(CustomerActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                                        })

                                                        .addOnFailureListener(e -> {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(CustomerActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                                                        })

                                                        .addOnProgressListener(taskSnapshot -> {
                                                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                                        })

                                                        .addOnCompleteListener(task -> {
                                                            if (task.isSuccessful()) {
                                                                stref.getDownloadUrl().addOnSuccessListener(uri -> {
                                                                    String downloadURL = uri.toString();
                                                                    Customer customer = new Customer(custID, name, user, lat, lng, downloadURL, area1, address, contact);
                                                                    dbref.child(custID).setValue(customer);
                                                                    Toast.makeText(CustomerActivity.this, "Customer updated successfully", Toast.LENGTH_SHORT).show();
                                                                });
                                                            }
                                                        });
                                            }
                                        } else {
                                            Customer customer = new Customer(custID, name, user, lat, lng, img, area1, address, contact);
                                            dbref.child(custID).setValue(customer);
                                            Toast.makeText(CustomerActivity.this, "Customer updated successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(CustomerActivity.this, "Empty Field", Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                (dialog, id) -> dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        listviewCustomer.setOnItemLongClickListener((adapterView, view, i, l) -> {
            if (userType.equals("Admin")) {
                AlertDialog.Builder delete = new AlertDialog.Builder(CustomerActivity.this);
                delete.setTitle("Delete");
                delete.setMessage("Do you want to delete " + filtered.get(i).get("name") + " ?");
                delete.setPositiveButton("Yes", (_dialog, _which) -> {
                    dbref.child(filtered.get(i).get("custID")).removeValue();
                    Toast.makeText(CustomerActivity.this, filtered.get(i).get("name") + " removed", Toast.LENGTH_SHORT).show();
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
                    listviewCustomer.setAdapter(new CustomerAdapter(CustomerActivity.this, filtered));
                    ((BaseAdapter) listviewCustomer.getAdapter()).notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(CustomerActivity.this, "An exception occurred", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CustomerActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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