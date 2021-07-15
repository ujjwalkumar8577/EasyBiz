package com.ujjwalkumar.easybiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ujjwalkumar.easybiz.helper.Customer;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerActivity extends AppCompatActivity {

    private String userType = "Staff";
    private ArrayList<HashMap<String, String>> filtered = new ArrayList<>();

    private ImageView backBtn;
    private ListView listviewCustomer;
    private LottieAnimationView loadingAnimation;

    private FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private DatabaseReference dbref = fbdb.getReference("customers");

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

        listviewCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(userType.equals("Admin")) {
                    // get xml view
                    LayoutInflater li = LayoutInflater.from(CustomerActivity.this);
                    View promptsView = li.inflate(R.layout.edit_customer_dialog, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CustomerActivity.this);
                    alertDialogBuilder.setView(promptsView);

                    // get user input
                    final ImageView imageView = (ImageView) promptsView.findViewById(R.id.imageView);
                    final EditText userInput1 = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput1);
                    final EditText userInput2 = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput2);
                    final EditText userInput3 = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput3);
                    final Spinner dialogSpinnerArea = (Spinner) promptsView.findViewById(R.id.dialogSpinnerArea);

                    String area = filtered.get(i).get("area");
                    int ind = Integer.parseInt("0" + area.charAt(area.length()-1));
                    userInput1.setText(filtered.get(i).get("name"));
                    userInput2.setText(filtered.get(i).get("contact"));
                    userInput3.setText(filtered.get(i).get("address"));
                    dialogSpinnerArea.setSelection(ind);
                    Glide.with(CustomerActivity.this).load(filtered.get(i).get("img"))
                            .placeholder(R.drawable.imageupload)
                            .into(imageView);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Update",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            // get user input
//                                            uid = "uidNotSet";
//                                            name = userInput1.getText().toString();
//                                            email = userInput2.getText().toString();
//                                            password = userInput3.getText().toString();
//                                            number = userInput4.getText().toString();
//                                            type = dialogSpinnerRole.getSelectedItem().toString();
//
//                                            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(MyAccountActivity.this, auth_create_user_listener);
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
            }
        });

        listviewCustomer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(userType.equals("Admin"))
                {
                    AlertDialog.Builder delete = new AlertDialog.Builder(CustomerActivity.this);
                    delete.setTitle("Delete");
                    delete.setMessage("Do you want to delete " + filtered.get(i).get("name") + " ?");
                    delete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {
                            dbref.child(filtered.get(i).get("custID")).removeValue();
                            Toast.makeText(CustomerActivity.this, filtered.get(i).get("name") + " removed", Toast.LENGTH_SHORT).show();
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
                    listviewCustomer.setAdapter(new CustomerActivity.ListviewCustomerAdapter(filtered));
                    ((BaseAdapter)listviewCustomer.getAdapter()).notifyDataSetChanged();
                }
                catch (Exception e) {
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

    public class ListviewCustomerAdapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> data;

        public ListviewCustomerAdapter(ArrayList<HashMap<String, String>> arr) {
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
                v = inflater.inflate(R.layout.customers, null);
            }

            final TextView textview1 = (TextView) v.findViewById(R.id.textview1);
            final TextView textview2 = (TextView) v.findViewById(R.id.textview2);
            final TextView textview3 = (TextView) v.findViewById(R.id.textview3);
            final ImageView imageviewCall = (ImageView) v.findViewById(R.id.imageviewCall);
            final ImageView imageview1Dir = (ImageView) v.findViewById(R.id.imageviewDir);

            double lat = Double.parseDouble(filtered.get(position).get("lat"));
            double lng = Double.parseDouble(filtered.get(position).get("lng"));

            textview1.setText(filtered.get(position).get("name"));
            textview2.setText(filtered.get(position).get("contact"));
            textview3.setText(filtered.get(position).get("area"));

            imageviewCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    Intent inv = new Intent();
                    inv.setAction(Intent.ACTION_CALL);
                    inv.setData(Uri.parse("tel:".concat(filtered.get(position).get("contact"))));
                    startActivity(inv);
                }
            });
            imageview1Dir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    Intent inv = new Intent();
                    inv.setAction(Intent.ACTION_VIEW);
                    inv.setData(Uri.parse("google.navigation:q=".concat(String.valueOf(lat).concat(",".concat(String.valueOf(lng))))));
                    if(inv.resolveActivity(getPackageManager())!=null) {
                        startActivity(inv);
                    }
                    else
                    {
                        Toast.makeText(CustomerActivity.this, "No app found for navigation", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return v;
        }
    }

}