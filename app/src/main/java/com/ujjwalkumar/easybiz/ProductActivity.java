package com.ujjwalkumar.easybiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ProductActivity extends AppCompatActivity {

    ImageView addItemBtn;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        backBtn = findViewById(R.id.backBtn);
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

        addItemBtn = findViewById(R.id.addItemBtn);

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get xml view
                LayoutInflater li = LayoutInflater.from(ProductActivity.this);
                View promptsView = li.inflate(R.layout.add_item_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductActivity.this);
                alertDialogBuilder.setView(promptsView);

                // get user input
                final EditText userInput1 = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput1);
                final EditText userInput2 = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput2);
                final EditText userInput3 = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput3);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        String ans = userInput1.getText() + " " + userInput2.getText() + " " + userInput3.getText();
                                        Toast.makeText(ProductActivity.this, ans, Toast.LENGTH_SHORT).show();
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

    }
}