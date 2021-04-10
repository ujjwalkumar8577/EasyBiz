package com.ujjwalkumar.easybiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ujjwalkumar.easybiz.helper.User;

import java.util.ArrayList;

public class MyAccountActivity extends AppCompatActivity {

    private ImageView backBtn,addStaffBtn;
    private TextView textviewUid,textviewName,textviewEmail,textviewType;
    private Spinner dialogSpinnerRole;
    private LinearLayout logoutBtn;
    private ListView listviewStaff;

    private FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private DatabaseReference dbref = fbdb.getReference("users");
    private SharedPreferences details;
    private FirebaseAuth auth;
    private OnCompleteListener<AuthResult> _auth_create_user_listener;
    private OnCompleteListener<AuthResult> _auth_sign_in_listener;
    private OnCompleteListener<Void> _auth_reset_password_listener;

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
        textviewType = findViewById(R.id.textviewType);
        logoutBtn = findViewById(R.id.logoutBtn);
        dialogSpinnerRole = findViewById(R.id.dialogSpinnerRole);
        listviewStaff = findViewById(R.id.listviewStaff);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent();
                in.setAction(Intent.ACTION_VIEW);
                in.setClass(getApplicationContext(),Dashboard.class);
                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(in);
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

                                        // get user input and set it to result
                                        String ans = userInput1.getText() + " " + userInput2.getText() + " " + userInput3.getText();
                                        Toast.makeText(MyAccountActivity.this, ans, Toast.LENGTH_SHORT).show();

                                        String uid = "12345678";
                                        String name = userInput1.getText().toString();
                                        String email = userInput2.getText().toString();
                                        String password = userInput3.getText().toString();
                                        String number = userInput4.getText().toString();
                                        String type = dialogSpinnerRole.getSelectedItem().toString();

                                        User user = new User(name,email,password,uid,type,number);
                                        dbref.child(uid).setValue(user);

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
}