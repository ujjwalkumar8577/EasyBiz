package com.ujjwalkumar.easybiz.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ujjwalkumar.easybiz.R;
import com.ujjwalkumar.easybiz.adapter.OrderAdapter;
import com.ujjwalkumar.easybiz.helper.Order;

import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class OrderActivity extends AppCompatActivity {

    private String userType = "Staff";
    private String curDate = "";
    private ArrayList<HashMap<String, String>> items = new ArrayList<>();
    private ArrayList<HashMap<String, String>> filtered = new ArrayList<>();
    private DecimalFormat decimalFormat;
    private PdfDocument pdfDocument;
    private static final int CREATE_FILE = 156;

    private ImageView backBtn, printBtn, routeBtn;
    private ListView listviewOrder;
    private DatePicker datepicker;
    private LottieAnimationView loadingAnimation;

    private SharedPreferences details;
    private final FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private final DatabaseReference dbref = fbdb.getReference("orders");
    private final DatabaseReference dbref2 = fbdb.getReference("items");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }

        backBtn = findViewById(R.id.backBtn);
        printBtn = findViewById(R.id.printBtn);
        routeBtn = findViewById(R.id.routeBtn);
        listviewOrder = findViewById(R.id.listviewOrder);
        datepicker = findViewById(R.id.datepicker);
        loadingAnimation = findViewById(R.id.loadingAnimation);
        details = getSharedPreferences("user", Activity.MODE_PRIVATE);
        curDate = getCurDate(datepicker.getYear(), datepicker.getMonth(), datepicker.getDayOfMonth());
        userType = details.getString("type", "");
        decimalFormat = new DecimalFormat("0.#");

        backBtn.setOnClickListener(view -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), DashboardActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        printBtn.setOnClickListener(view -> savePDF());

        routeBtn.setOnClickListener(view -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), RouteActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            in.putExtra("orderList", new Gson().toJson(filtered));
            startActivity(in);
        });

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        datepicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), (datePicker, i, i1, i2) -> {
            curDate = getCurDate(i,i1,i2);
            loadList();
        });

        loadItems();
        loadList();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exit = new AlertDialog.Builder(this);
        exit.setTitle("Exit");
        exit.setMessage("Do you want to exit?");
        exit.setPositiveButton("Yes", (dialog, which) -> {
            Intent inf = new Intent();
            inf.setAction(Intent.ACTION_VIEW);
            inf.setClass(getApplicationContext(), DashboardActivity.class);
            inf.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(inf);
            finish();
        });
        exit.setNegativeButton("No", (dialog, which) -> {

        });
        exit.create().show();
    }

    private String getCurDate(int y, int m, int d) {
        String tmp = Integer.toString(y);
        m = m+1;
        if(m/10==0)
            tmp = tmp + "0" + m;
        else
            tmp = tmp + m;
        if(d/10==0)
            tmp = tmp + "0" + d;
        else
            tmp = tmp + d;

        return tmp;
    }

    private void loadItems() {
        dbref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items = new ArrayList<>();
                try {
                    GenericTypeIndicator<HashMap<String, String>> ind = new GenericTypeIndicator<HashMap<String, String>>() {};
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, String> map = data.getValue(ind);
                        items.add(map);
                    }
                }
                catch (Exception e) {
                    Toast.makeText(OrderActivity.this, "Failed to load items", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OrderActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePDF() {
        int pageHeight = 595;
        int pageWidth = 842;
        pdfDocument = new PdfDocument();
        Paint title = new Paint();
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        Canvas canvas = myPage.getCanvas();
        title.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        title.setTextSize(15);
        title.setColor(ContextCompat.getColor(this, R.color.black));

        StringBuilder result = new StringBuilder("Area : " + String.format("%-20s", " ") + "Date : " + curDate);
        canvas.drawText(result.toString(), 50, 50, title);
        result = new StringBuilder(String.format("%-5s", "S.N.") + String.format("%-20s", "Customer"));

        for(HashMap<String,String> item : items) {
            String name = getAcronym(item.get("name"));
            result.append(name + " ");
        }
        canvas.drawText(result.toString(), 50, 70, title);

        int sno = 1;
        for(HashMap<String,String> hmp : filtered) {
            ArrayList<HashMap<String, String>> cartTmp = new Gson().fromJson(hmp.get("cart"), new TypeToken<ArrayList<HashMap<String, String>>>() {}.getType());
            HashMap<String, String> tmpOrder = new HashMap<>();
            for(HashMap<String, String> map : cartTmp) {
                tmpOrder.put(map.get("name"), decimalFormat.format(Double.parseDouble(map.get("qty"))));
            }

            result = new StringBuilder(String.format("%-5s", sno) + String.format("%-20s", hmp.get("name")));
            for(HashMap<String,String> item : items) {
                if(tmpOrder.containsKey(item.get("name")))
                    result.append(String.format("%-5s", tmpOrder.get(item.get("name"))));
                else
                    result.append(String.format("%-5s", "0"));
            }
            canvas.drawText(result.toString(), 50, 70+sno*20, title);
            sno++;
        }

        pdfDocument.finishPage(myPage);

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "Order_" + curDate + ".pdf");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        startActivityForResult(intent, CREATE_FILE);
    }

    public void loadList() {
        loadingAnimation.setVisibility(View.VISIBLE);
        dbref.child(curDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                filtered = new ArrayList<>();
                try {
                    GenericTypeIndicator<HashMap<String, String>> ind = new GenericTypeIndicator<HashMap<String, String>>() {};
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, String> map = data.getValue(ind);
                        if(map.containsKey("status") && map.get("status").equals(Order.STATUS_PENDING))
                            filtered.add(map);
                    }
                    loadingAnimation.setVisibility(View.GONE);
                    listviewOrder.setAdapter(new OrderAdapter(OrderActivity.this, userType, curDate, filtered));
                    ((BaseAdapter)listviewOrder.getAdapter()).notifyDataSetChanged();
                }
                catch (Exception e) {
                    Toast.makeText(OrderActivity.this, "An exception occurred", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OrderActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CREATE_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
                FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                pdfDocument.writeTo(fileOutputStream);
                fileOutputStream.close();
                pfd.close();

                Toast.makeText(OrderActivity.this, "PDF generated successfully.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pdfDocument.close();
        }
    }

    private String getAcronym(String str) {
        StringBuilder sb = new StringBuilder();
        boolean space = true;
        for(int i=0; i<str.length(); i++) {
            if(str.charAt(i)==' ') {
                space = true;
            }
            else if(space) {
                space = false;
                sb.append(str.charAt(i));
            }
        }
        return sb.toString();
    }
}