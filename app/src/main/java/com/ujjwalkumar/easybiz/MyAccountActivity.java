package com.ujjwalkumar.easybiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class MyAccountActivity extends AppCompatActivity {

    private ArrayList<String> roles = new ArrayList<>();

    private ImageView addStaffBtn;
    private Spinner dialogSpinnerRole;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        addStaffBtn = findViewById(R.id.addStaffBtn);
        dialogSpinnerRole = findViewById(R.id.dialogSpinnerRole);

        roles.add("-Select role-");
        roles.add("Staff");
        roles.add("Admin");
//        dialogSpinnerRole.setAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,roles));
//        ((ArrayAdapter)dialogSpinnerRole.getAdapter()).notifyDataSetChanged();
//        dialogSpinnerRole.setSelection(0);

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

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        String ans = userInput1.getText() + " " + userInput2.getText() + " " + userInput3.getText();
                                        Toast.makeText(MyAccountActivity.this, ans, Toast.LENGTH_SHORT).show();
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