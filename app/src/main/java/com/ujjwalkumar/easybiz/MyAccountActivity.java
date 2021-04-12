package com.ujjwalkumar.easybiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ujjwalkumar.easybiz.helper.User;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAccountActivity extends AppCompatActivity {

    private String name,email,password,uid,type,number;
    private HashMap<String, String> mp = new HashMap<>();
    private ArrayList<HashMap<String, String>> filtered = new ArrayList<>();

    private ImageView backBtn,addStaffBtn;
    private TextView textviewUid,textviewName,textviewEmail,textviewNumber,textviewType;
    private Spinner dialogSpinnerRole;
    private LinearLayout logoutBtn;
    private CardView createStaffView;
    private ListView listviewStaff;

    private FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private DatabaseReference dbref = fbdb.getReference("users");
    private SharedPreferences details;
    private FirebaseAuth auth;
    private OnCompleteListener<AuthResult> auth_create_user_listener;
    private OnCompleteListener<AuthResult> auth_sign_in_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        com.google.firebase.FirebaseApp.initializeApp(this);

        backBtn = findViewById(R.id.backBtn);
        addStaffBtn = findViewById(R.id.addStaffBtn);
        textviewUid = findViewById(R.id.textviewUid);
        textviewName = findViewById(R.id.textviewName);
        textviewEmail = findViewById(R.id.textviewEmail);
        textviewNumber = findViewById(R.id.textviewNumber);
        textviewType = findViewById(R.id.textviewType);
        logoutBtn = findViewById(R.id.logoutBtn);
        dialogSpinnerRole = findViewById(R.id.dialogSpinnerRole);
        createStaffView = findViewById(R.id.createStaffView);
        listviewStaff = findViewById(R.id.listviewStaff);
        auth = FirebaseAuth.getInstance();
        details = getSharedPreferences("user", Activity.MODE_PRIVATE);

        textviewUid.setText(details.getString("uid", ""));
        textviewName.setText(details.getString("name", ""));
        textviewEmail.setText(details.getString("email", ""));
        textviewNumber.setText(details.getString("number", ""));
        textviewType.setText(details.getString("type", ""));
        if (details.getString("type", "").equals("Admin")) {
            createStaffView.setVisibility(View.VISIBLE);
        } else {
            createStaffView.setVisibility(View.GONE);
        }

        loadList();

        auth_create_user_listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> _param1) {
                final boolean _success = _param1.isSuccessful();
                final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
                if (_success) {
                    Toast.makeText(MyAccountActivity.this, "New user created successfully", Toast.LENGTH_SHORT).show();
                    uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    User user = new User(name,email,password,uid,type,number);
                    dbref.child(uid).setValue(user);
                    FirebaseAuth.getInstance().signOut();
                    auth.signInWithEmailAndPassword(details.getString("email", ""), details.getString("password", "")).addOnCompleteListener(MyAccountActivity.this, auth_sign_in_listener);
                    loadList();
                } else {
                    Toast.makeText(MyAccountActivity.this, _errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        };

        auth_sign_in_listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> param1) {
                final boolean success = param1.isSuccessful();
                final String errorMessage = param1.getException() != null ? param1.getException().getMessage() : "";
            }
        };

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

        addStaffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get xml view
                LayoutInflater li = LayoutInflater.from(MyAccountActivity.this);
                View promptsView = li.inflate(R.layout.add_staff_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyAccountActivity.this);
                alertDialogBuilder.setView(promptsView);

                // get user input
                final EditText userInput1 = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput1);
                final EditText userInput2 = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput2);
                final EditText userInput3 = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput3);
                final EditText userInput4 = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput4);
                final Spinner dialogSpinnerRole = (Spinner) promptsView.findViewById(R.id.dialogSpinnerRole);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input
                                        uid = "uidNotSet";
                                        name = userInput1.getText().toString();
                                        email = userInput2.getText().toString();
                                        password = userInput3.getText().toString();
                                        number = userInput4.getText().toString();
                                        type = dialogSpinnerRole.getSelectedItem().toString();

                                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(MyAccountActivity.this, auth_create_user_listener);
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

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                // Rate.edit().putString("preset", "").commit();
                details.edit().putString("uid", "").commit();
                details.edit().putString("name", "").commit();
                details.edit().putString("email", "").commit();
                details.edit().putString("password", "").commit();
                details.edit().putString("type", "").commit();
                details.edit().putString("number", "").commit();
                finish();
            }
        });

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
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                filtered = new ArrayList<>();
                try {
                    GenericTypeIndicator<HashMap<String, String>> ind = new GenericTypeIndicator<HashMap<String, String>>() {
                    };
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, String> map = data.getValue(ind);
                        if(map.containsKey("type")&&(!map.get("type").equals("Admin")))
                            filtered.add(map);
                    }
                    listviewStaff.setAdapter(new MyAccountActivity.ListviewStaffAdapter(filtered));
                    ((BaseAdapter)listviewStaff.getAdapter()).notifyDataSetChanged();
                }
                catch (Exception e) {
                    Toast.makeText(MyAccountActivity.this, "An exception occurred", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError _databaseError) {
            }
        });
    }

    public class ListviewStaffAdapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> data;

        public ListviewStaffAdapter(ArrayList<HashMap<String, String>> arr) {
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
                v = inflater.inflate(R.layout.staffs, null);
            }

            final TextView textview1 = (TextView) v.findViewById(R.id.textview1);
            final TextView textview2 = (TextView) v.findViewById(R.id.textview2);
            final ImageView imageviewCall = (ImageView) v.findViewById(R.id.imageviewCall);
            final ImageView imageview1Dir = (ImageView) v.findViewById(R.id.imageviewDir);

            textview1.setText(filtered.get(position).get("name").toString());
            textview2.setText(filtered.get(position).get("number").toString());

            imageviewCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    Intent inv = new Intent();
                    inv.setAction(Intent.ACTION_CALL);
                    inv.setData(Uri.parse("tel:".concat(filtered.get(position).get("number").toString())));
                    startActivity(inv);
                }
            });
            imageview1Dir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    Intent in = new Intent();
                    in.setAction(Intent.ACTION_VIEW);
                    in.setClass(getApplicationContext(), StaffActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(in);
                    finish();
                }
            });

            return v;
        }
    }

}