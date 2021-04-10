package com.ujjwalkumar.easybiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Dashboard extends AppCompatActivity {

    private TextView textviewUserName,textviewUserType;
    private ImageView myAccountBtn,feedbackBtn,aboutBtn;
    private CardView cardview1,cardview2,cardview3,cardview4,cardview5,cardview6,cardview7,cardview8;

    private SharedPreferences details;
    private Intent in = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        textviewUserName = findViewById(R.id.textviewUserName);
        textviewUserType = findViewById(R.id.textviewUserType);
        myAccountBtn = findViewById(R.id.myAccountBtn);
        feedbackBtn = findViewById(R.id.feedbackBtn);
        aboutBtn = findViewById(R.id.aboutBtn);
        cardview1 = findViewById(R.id.cardview1);
        cardview2 = findViewById(R.id.cardview2);
        cardview3 = findViewById(R.id.cardview3);
        cardview4 = findViewById(R.id.cardview4);
        cardview5 = findViewById(R.id.cardview5);
        cardview6 = findViewById(R.id.cardview6);
        cardview7 = findViewById(R.id.cardview7);
        cardview8 = findViewById(R.id.cardview8);

        details = getSharedPreferences("user", Activity.MODE_PRIVATE);
        textviewUserName.setText(details.getString("name", ""));
        textviewUserType.setText(details.getString("type", ""));

        myAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in.setAction(Intent.ACTION_VIEW);
                in.setClass(getApplicationContext(), MyAccountActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(in);
            }
        });

        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in.setAction(Intent.ACTION_VIEW);
                in.setClass(getApplicationContext(), FeedbackActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(in);
            }
        });

        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in.setAction(Intent.ACTION_VIEW);
                in.setClass(getApplicationContext(), AboutActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(in);
            }
        });

        cardview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in.setAction(Intent.ACTION_VIEW);
                in.setClass(getApplicationContext(), AddEstimateActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(in);
            }
        });

        cardview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in.setAction(Intent.ACTION_VIEW);
                in.setClass(getApplicationContext(), EstimateActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(in);
            }
        });

        cardview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in.setAction(Intent.ACTION_VIEW);
                in.setClass(getApplicationContext(), AddOrderActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(in);
            }
        });

        cardview4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in.setAction(Intent.ACTION_VIEW);
                in.setClass(getApplicationContext(), OrderActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(in);
            }
        });

        cardview5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in.setAction(Intent.ACTION_VIEW);
                in.setClass(getApplicationContext(), AddCustomerActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(in);
            }
        });

        cardview6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in.setAction(Intent.ACTION_VIEW);
                in.setClass(getApplicationContext(), CustomerActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(in);
            }
        });

        cardview7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in.setAction(Intent.ACTION_VIEW);
                in.setClass(getApplicationContext(), CashCalculatorActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(in);
            }
        });

        cardview8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in.setAction(Intent.ACTION_VIEW);
                in.setClass(getApplicationContext(), ProductActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(in);
            }
        });

    }

}