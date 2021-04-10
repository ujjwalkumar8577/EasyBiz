package com.ujjwalkumar.easybiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CashCalculatorActivity extends AppCompatActivity {

    private ImageView backBtn;
    private Button calculateBtn,clearBtn;
    private LinearLayout shareBtn;
    private TextView textviewDate,textview1,textview2,textview3,textview4,textview5,textview6,textview7,textview8,textview9,textviewTotalNotes,textviewTotalAmount;
    private EditText edittext1,edittext2,edittext3,edittext4,edittext5,edittext6,edittext7,edittext8,edittext9;

    private Intent in = new Intent();
    private Calendar cal = Calendar.getInstance();

    private double d2000 = 0;
    private double d500 = 0;
    private double d200 = 0;
    private double d100 = 0;
    private double d50 = 0;
    private double d20 = 0;
    private double d10 = 0;
    private double d5 = 0;
    private double doth = 0;
    private double R1 = 0;
    private double R2 = 0;
    private double R3 = 0;
    private double R4 = 0;
    private double R5 = 0;
    private double R6 = 0;
    private double R7 = 0;
    private double R8 = 0;
    private double R9 = 0;
    private double amount = 0;
    private double total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_calculator);

        backBtn = findViewById(R.id.backBtn);
        calculateBtn = findViewById(R.id.calculateBtn);
        clearBtn = findViewById(R.id.clearBtn);
        shareBtn = findViewById(R.id.shareBtn);
        textviewDate = findViewById(R.id.textviewDate);
        textviewTotalNotes = findViewById(R.id.textviewTotalNotes);
        textviewTotalAmount = findViewById(R.id.textviewTotalAmount);
        textview1 = findViewById(R.id.textview1);
        textview2 = findViewById(R.id.textview2);
        textview3 = findViewById(R.id.textview3);
        textview4 = findViewById(R.id.textview4);
        textview5 = findViewById(R.id.textview5);
        textview6 = findViewById(R.id.textview6);
        textview7 = findViewById(R.id.textview7);
        textview8 = findViewById(R.id.textview8);
        textview9 = findViewById(R.id.textview9);
        edittext1 = findViewById(R.id.edittext1);
        edittext2 = findViewById(R.id.edittext2);
        edittext3 = findViewById(R.id.edittext3);
        edittext4 = findViewById(R.id.edittext4);
        edittext5 = findViewById(R.id.edittext5);
        edittext6 = findViewById(R.id.edittext6);
        edittext7 = findViewById(R.id.edittext7);
        edittext8 = findViewById(R.id.edittext8);
        edittext9 = findViewById(R.id.edittext9);

        cal = Calendar.getInstance();
        textviewDate.setText("Date : ".concat(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime())));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in.setAction(Intent.ACTION_VIEW);
                in.setClass(getApplicationContext(),Dashboard.class);
                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(in);
            }
        });

        calculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edittext1.getText().toString().equals("")) {
                    d2000 = 0;
                    edittext1.setText("0");
                } else {
                    d2000 = Double.parseDouble(edittext1.getText().toString());
                }
                if (edittext2.getText().toString().equals("")) {
                    d500 = 0;
                    edittext2.setText("0");
                } else {
                    d500 = Double.parseDouble(edittext2.getText().toString());
                }
                if (edittext3.getText().toString().equals("")) {
                    d200 = 0;
                    edittext3.setText("0");
                } else {
                    d200 = Double.parseDouble(edittext3.getText().toString());
                }
                if (edittext4.getText().toString().equals("")) {
                    d100 = 0;
                    edittext4.setText("0");
                } else {
                    d100 = Double.parseDouble(edittext4.getText().toString());
                }
                if (edittext5.getText().toString().equals("")) {
                    d50 = 0;
                    edittext5.setText("0");
                } else {
                    d50 = Double.parseDouble(edittext5.getText().toString());
                }
                if (edittext6.getText().toString().equals("")) {
                    d20 = 0;
                    edittext6.setText("0");
                } else {
                    d20 = Double.parseDouble(edittext6.getText().toString());
                }
                if (edittext7.getText().toString().equals("")) {
                    d10 = 0;
                    edittext7.setText("0");
                } else {
                    d10 = Double.parseDouble(edittext7.getText().toString());
                }
                if (edittext8.getText().toString().equals("")) {
                    d5 = 0;
                    edittext8.setText("0");
                } else {
                    d5 = Double.parseDouble(edittext8.getText().toString());
                }
                if (edittext9.getText().toString().equals("")) {
                    doth = 0;
                    edittext9.setText("0");
                } else {
                    doth = Double.parseDouble(edittext9.getText().toString());
                }
                R1 = d2000 * 2000;
                R2 = d500 * 500;
                R3 = d200 * 200;
                R4 = d100 * 100;
                R5 = d50 * 50;
                R6 = d20 * 20;
                R7 = d10 * 10;
                R8 = d5 * 5;
                R9 = doth * 1;
                textview1.setText(String.valueOf((long) (R1)));
                textview2.setText(String.valueOf((long) (R2)));
                textview3.setText(String.valueOf((long) (R3)));
                textview4.setText(String.valueOf((long) (R4)));
                textview5.setText(String.valueOf((long) (R5)));
                textview6.setText(String.valueOf((long) (R6)));
                textview7.setText(String.valueOf((long) (R7)));
                textview8.setText(String.valueOf((long) (R8)));
                textview9.setText(String.valueOf((long) (R9)));
                amount = R1 + R2 + R3 + R4 + R5 + R6 + R7 + R8 + R9;
                total = d2000 + d500 + d200 + d100 + d50 + d20 + d10 + d5 + doth;
                textviewTotalAmount.setText(String.valueOf(amount));
                textviewTotalNotes.setText(String.valueOf(total));
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edittext1.setText("");
                edittext2.setText("");
                edittext3.setText("");
                edittext4.setText("");
                edittext5.setText("");
                edittext6.setText("");
                edittext7.setText("");
                edittext8.setText("");
                edittext9.setText("");
                textview1.setText("");
                textview2.setText("");
                textview3.setText("");
                textview4.setText("");
                textview5.setText("");
                textview6.setText("");
                textview7.setText("");
                textview8.setText("");
                textview9.setText("");
                textviewTotalNotes.setText("0");
                textviewTotalAmount.setText("0");
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmp = "2000 * " + d2000 + " = " + R1 + "\n" + "500   * " + d500 + " = " + R2 + "\n" + "200   * " + d200 + " = " + R3 + "\n" + "100   * " + d100 + " = " + R4 + "\n" + "50     * " + d50 + " = " + R5 + "\n" + "20     * " + d20 + " = " + R6 + "\n" + "10     * " + d10 + " = " + R7 + "\n" + "5      * " + d5 + " = " + R8 + "\n" + "1       * " + doth + " = " + R9 + "\n" + "Total Amt.  =  " + amount + "\n";
                Intent ind = new Intent(android.content.Intent.ACTION_SEND);
                ind.setType("text/plain");
                ind.putExtra(android.content.Intent.EXTRA_TEXT, tmp);
                startActivity(Intent.createChooser(ind, "Share using"));
            }
        });

    }
}