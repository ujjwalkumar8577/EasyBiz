package com.ujjwalkumar.easybiz.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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
import com.ujjwalkumar.easybiz.R;
import com.ujjwalkumar.easybiz.adapter.StaffAdapter;
import com.ujjwalkumar.easybiz.helper.User;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAccountActivity extends AppCompatActivity {

    private String name,email,password,uid,type,number;
    private ArrayList<HashMap<String, String>> filtered = new ArrayList<>();

    private ImageView backBtn,addStaffBtn;
    private TextView textviewUid,textviewName,textviewEmail,textviewNumber,textviewType;
    private LinearLayout logoutBtn;
    private CardView createStaffView;
    private ListView listviewStaff;
    private LottieAnimationView loadingAnimation;

    private final FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private final DatabaseReference dbref = fbdb.getReference("users");
    private SharedPreferences details;
    private FirebaseAuth auth;
    private OnCompleteListener<AuthResult> auth_create_user_listener;
    private OnCompleteListener<AuthResult> auth_sign_in_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        backBtn = findViewById(R.id.backBtn);
        addStaffBtn = findViewById(R.id.addStaffBtn);
        textviewUid = findViewById(R.id.textviewUid);
        textviewName = findViewById(R.id.textviewName);
        textviewEmail = findViewById(R.id.textviewEmail);
        textviewNumber = findViewById(R.id.textviewNumber);
        textviewType = findViewById(R.id.textviewType);
        logoutBtn = findViewById(R.id.logoutBtn);
        createStaffView = findViewById(R.id.createStaffView);
        listviewStaff = findViewById(R.id.listviewStaff);
        loadingAnimation = findViewById(R.id.loadingAnimation);

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
                in.setClass(getApplicationContext(), DashboardActivity.class);
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
                View promptsView = li.inflate(R.layout.dialog_add_staff, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyAccountActivity.this);
                alertDialogBuilder.setView(promptsView);

                // get user input
                final EditText userInput1 = promptsView.findViewById(R.id.editTextDialogUserInput1);
                final EditText userInput2 = promptsView.findViewById(R.id.editTextDialogUserInput2);
                final EditText userInput3 = promptsView.findViewById(R.id.editTextDialogUserInput3);
                final EditText userInput4 = promptsView.findViewById(R.id.editTextDialogUserInput4);
                final Spinner dialogSpinnerRole = promptsView.findViewById(R.id.dialogSpinnerRole);

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
                details.edit().putString("uid", "").commit();
                details.edit().putString("name", "").commit();
                details.edit().putString("email", "").commit();
                details.edit().putString("password", "").commit();
                details.edit().putString("type", "").commit();
                details.edit().putString("number", "").commit();
                finish();
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
                inf.setClass(getApplicationContext(), DashboardActivity.class);
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
                        if(map.containsKey("type")&&(!map.get("type").equals("Admin")))
                            filtered.add(map);
                    }
                    loadingAnimation.setVisibility(View.GONE);
                    listviewStaff.setAdapter(new StaffAdapter(MyAccountActivity.this, filtered));
                    ((BaseAdapter)listviewStaff.getAdapter()).notifyDataSetChanged();
                }
                catch (Exception e) {
                    Toast.makeText(MyAccountActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError _databaseError) {
            }
        });
    }
}