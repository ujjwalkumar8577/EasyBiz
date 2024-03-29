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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomerActivity extends AppCompatActivity {

    private String userType = "Staff";
    private boolean imageSet = false;
    private ArrayList<HashMap<String, String>> filtered = new ArrayList<>();
    private Uri filePath;
    private String[] areas;

    private ImageView backBtn, imageView;
    private EditText editTextSearch;
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
        editTextSearch = findViewById(R.id.editTextSearch);
        listviewCustomer = findViewById(R.id.listviewCustomer);
        loadingAnimation = findViewById(R.id.loadingAnimation);

        SharedPreferences details = getSharedPreferences("user", Activity.MODE_PRIVATE);
        userType = details.getString("type", "");
        areas = getResources().getStringArray(R.array.areas_array);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE}, 1000);
        }

        ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    int resultCode = result.getResultCode();
                    Intent data = result.getData();
                    if (resultCode == RESULT_OK && data != null && data.getData() != null) {
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
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        ActivityResultLauncher<Intent> captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    int resultCode = result.getResultCode();
                    Intent data = result.getData();
                    if (resultCode == RESULT_OK && data!=null) {
                        try {
                            Bundle extras = data.getExtras();
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            filePath = getImageUri(imageBitmap);
                            imageView.setImageBitmap(imageBitmap);
                            imageSet = true;
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        backBtn.setOnClickListener(view -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), DashboardActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadList(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
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
                int ind = getAreaIndex(area);
                userInput1.setText(filtered.get(i).get("name"));
                userInput2.setText(filtered.get(i).get("contact"));
                userInput3.setText(filtered.get(i).get("address"));
                dialogSpinnerArea.setSelection(ind);
                Glide.with(CustomerActivity.this).load(filtered.get(i).get("img"))
                        .placeholder(R.drawable.imageupload)
                        .into(imageView);

                imageView.setOnClickListener(view1 -> {
                    AlertDialog.Builder choose = new AlertDialog.Builder(this);
                    choose.setTitle("Add Image");
                    choose.setPositiveButton("Capture", (dialog, which) -> {
                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        captureImageLauncher.launch(captureIntent);
                    });
                    choose.setNeutralButton("Select", (dialog, which) -> {
                        Intent selectIntent = new Intent();
                        selectIntent.setType("image/*");
                        selectIntent.setAction(Intent.ACTION_GET_CONTENT);
                        pickImageLauncher.launch(selectIntent);
                    });
                    choose.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                    choose.create().show();
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
                delete.setPositiveButton("Yes", (dialog, which) -> {
                    dbref.child(filtered.get(i).get("custID")).removeValue();
                    Toast.makeText(CustomerActivity.this, filtered.get(i).get("name") + " removed", Toast.LENGTH_SHORT).show();
                });
                delete.setNegativeButton("No", (dialog, which) -> {

                });
                delete.create().show();
            }
            return false;
        });

        loadList("");
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

    private void loadList(CharSequence str) {
        loadingAnimation.setVisibility(View.VISIBLE);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                filtered = new ArrayList<>();
                try {
                    GenericTypeIndicator<HashMap<String, String>> ind = new GenericTypeIndicator<HashMap<String, String>>() {};
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, String> map = data.getValue(ind);
                        if(map.get("name").contains(str) || map.get("address").contains(str) || map.get("contact").contains(str) || map.get("area").contains(str))
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

    public Uri getImageUri(Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), image, "Title", null);
        return Uri.parse(path);
    }

    public int getAreaIndex(String area) {
        for(int i=0; i<areas.length; i++) {
            if(area.equals(areas[i]))
                return i;
        }
        return 0;
    }
}