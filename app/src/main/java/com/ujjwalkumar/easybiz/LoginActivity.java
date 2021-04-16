package com.ujjwalkumar.easybiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.ujjwalkumar.easybiz.helper.User;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private String name,email,password,uid,type,number;

    private EditText edittextEmail,edittextPassword;
    private LinearLayout loginBtn;
    private TextView forgotPasswordBtn;
    private LottieAnimationView loadingAnimation;

    private FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private DatabaseReference dbref = fbdb.getReference("users");
    private SharedPreferences details;
    private FirebaseAuth auth;
    private OnCompleteListener<Void> auth_reset_password_listener;
    private OnCompleteListener<AuthResult> auth_sign_in_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edittextEmail = findViewById(R.id.edittextEmail);
        edittextPassword = findViewById(R.id.edittextPassword);
        forgotPasswordBtn = findViewById(R.id.forgotPasswordBtn);
        loginBtn = findViewById(R.id.loginBtn);
        loadingAnimation = findViewById(R.id.loadingAnimation);

        loadingAnimation.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();
        details = getSharedPreferences("user", Activity.MODE_PRIVATE);

        auth_sign_in_listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> param1) {
                final boolean success = param1.isSuccessful();
                final String errorMessage = param1.getException() != null ? param1.getException().getMessage() : "";
                if (success) {
                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<HashMap<String, Object>> lmp = new ArrayList<>();
                            try {
                                GenericTypeIndicator<HashMap<String, Object>> ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                                };
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    HashMap<String, Object> map = data.getValue(ind);
                                    lmp.add(map);
                                }
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }

                            int t = 0;
                            int flag = 0;
                            for (int i = 0; i < lmp.size(); i++) {
                                if (lmp.get(t).get("uid").toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    flag = t;
                                    break;
                                }
                                t++;
                            }

                            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            name = lmp.get(flag).get("name").toString();
                            email = lmp.get(flag).get("email").toString();
                            password = lmp.get(flag).get("password").toString();
                            type = lmp.get(flag).get("type").toString();
                            number = lmp.get(flag).get("number").toString();

                            details.edit().putString("uid", uid).commit();
                            details.edit().putString("name", name).commit();
                            details.edit().putString("email", email).commit();
                            details.edit().putString("password", password).commit();
                            details.edit().putString("type", type).commit();
                            details.edit().putString("number", number).commit();

                            User user = new User(name,email,password,uid,type,number);
                            dbref.child(uid).setValue(user);

                            Intent in = new Intent();
                            in.setAction(Intent.ACTION_VIEW);
                            in.setClass(getApplicationContext(), Dashboard.class);
                            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(in);
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(LoginActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    loadingAnimation.setVisibility(View.GONE);
                    loginBtn.setVisibility(View.VISIBLE);
                }
            }
        };

        auth_reset_password_listener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> param1) {
                final boolean _success = param1.isSuccessful();
                Toast.makeText(LoginActivity.this, "Reset password email sent", Toast.LENGTH_SHORT).show();
            }
        };

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edittextEmail.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                } else {
                    auth.sendPasswordResetEmail(edittextEmail.getText().toString()).addOnCompleteListener(auth_reset_password_listener);
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingAnimation.setVisibility(View.VISIBLE);
                loginBtn.setVisibility(View.GONE);
                details.edit().putString("email", edittextEmail.getText().toString()).commit();
                details.edit().putString("password", edittextPassword.getText().toString()).commit();
                auth.signInWithEmailAndPassword(edittextEmail.getText().toString(), edittextPassword.getText().toString()).addOnCompleteListener(LoginActivity.this, auth_sign_in_listener);
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
}