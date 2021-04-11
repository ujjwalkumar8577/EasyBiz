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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ujjwalkumar.easybiz.helper.Feedback;

public class FeedbackActivity extends AppCompatActivity {

    String message,user,name,email;

    private ImageView backBtn;
    private EditText edittextFeedback;
    private LinearLayout sendBtn;

    private SharedPreferences details;
    private FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private DatabaseReference dbref = fbdb.getReference("feedbacks");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        edittextFeedback = findViewById(R.id.edittextFeedback);
        backBtn = findViewById(R.id.backBtn);
        sendBtn = findViewById(R.id.sendBtn);

        details = getSharedPreferences("user", Activity.MODE_PRIVATE);

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

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = edittextFeedback.getText().toString();
                if(message.equals("")) {
                    Toast.makeText(FeedbackActivity.this, "Please enter your message", Toast.LENGTH_SHORT).show();
                }
                else {
                    user = details.getString("uid", "");
                    name = details.getString("name", "");
                    email = details.getString("email", "");

                    Feedback feedback = new Feedback(message,user,name,email);
                    dbref.child(dbref.push().getKey()).setValue(feedback);
                }
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