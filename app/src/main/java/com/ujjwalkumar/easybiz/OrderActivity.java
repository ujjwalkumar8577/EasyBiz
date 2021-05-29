package com.ujjwalkumar.easybiz;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ujjwalkumar.easybiz.helper.Order;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class OrderActivity extends AppCompatActivity {

    private String curDate = "";
    private String key = "";
    private ArrayList<HashMap<String, String>> items = new ArrayList<>();
    private ArrayList<HashMap<String, String>> filtered = new ArrayList<>();
    private ArrayList<HashMap<String, String>> cart = new ArrayList<>();

    private ImageView backBtn,printBtn;
    private ListView listviewOrder;
    private DatePicker datepicker;
    private LottieAnimationView loadingAnimation;

    private SharedPreferences details;
    private FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private DatabaseReference dbref = fbdb.getReference("orders");
    private DatabaseReference dbref2 = fbdb.getReference("items");

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }

        backBtn = findViewById(R.id.backBtn);
        printBtn = findViewById(R.id.printBtn);
        listviewOrder = findViewById(R.id.listviewOrder);
        datepicker = findViewById(R.id.datepicker);
        loadingAnimation = findViewById(R.id.loadingAnimation);
        details = getSharedPreferences("user", Activity.MODE_PRIVATE);
        curDate = getCurDate(datepicker.getYear(),datepicker.getMonth(),datepicker.getDayOfMonth());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1000);
        }

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

        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //saveOrderPDF();
                savePDF();
            }
        });

        datepicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                curDate = getCurDate(i,i1,i2);
                loadList();
            }
        });

        loadItems();
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

    private String getCurDate(int y, int m, int d) {
        String tmp = Integer.toString(y);
        m = m+1;
        if(m/10==0)
            tmp = tmp + "0" + m;
        else
            tmp = tmp + m;
        tmp = tmp + d;

        return tmp;
    }

    private void loadItems() {
        dbref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items = new ArrayList<>();
                try {
                    GenericTypeIndicator<HashMap<String, String>> ind = new GenericTypeIndicator<HashMap<String, String>>() {
                    };
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

        int pageHeight = 842;
        int pagewidth = 595;
        PdfDocument pdfDocument = new PdfDocument();
        Paint title = new Paint();
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        Canvas canvas = myPage.getCanvas();
        title.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        title.setTextSize(15);
        title.setColor(ContextCompat.getColor(this, R.color.black));

        StringBuilder result = new StringBuilder("Area : " + String.format("%-20s", " ") + "Date : " + curDate);
        canvas.drawText(result.toString(), 50, 50, title);
        result = new StringBuilder(String.format("%-5s", "S.N.") + String.format("%-20s", "Customer"));
        // Print Item Names
//        for(HashMap<String,String> item : items) {
//            String name = item.get("name");
//            if(name.length()>5)
//                name = name.substring(name.length()-5,name.length());
//            result += name + " ";
//        }
        canvas.drawText(result.toString(), 50, 70, title);

        int sno = 1;
        for(HashMap<String,String> hmp : filtered) {
            ArrayList<HashMap<String, String>> cartTmp = new ArrayList<>();
            cartTmp = new Gson().fromJson(hmp.get("cart"), new TypeToken<ArrayList<HashMap<String, String>>>() {
            }.getType());
            HashMap<String, String> tmpOrder = new HashMap<>();
            for(HashMap<String, String> map : cartTmp) {
                tmpOrder.put(map.get("name"),map.get("qty"));
            }

            result = new StringBuilder(String.format("%-5s", sno) + String.format("%-20s", hmp.get("name")));
            for(HashMap<String,String> item : items) {
                if(tmpOrder.containsKey(item.get("name")))
                    result.append(String.format("%-6s", tmpOrder.get(item.get("name"))));
                else
                    result.append(String.format("%-6s", "0"));
            }
            canvas.drawText(result.toString(), 50, 70+sno*20, title);
            sno++;
        }

        pdfDocument.finishPage(myPage);
        File file = new File(Environment.getExternalStorageDirectory(), "Order_" + curDate + ".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(OrderActivity.this, "PDF generated successfully.", Toast.LENGTH_SHORT).show();
//            Intent target = new Intent(Intent.ACTION_VIEW);
//            target.setDataAndType(Uri.fromFile(file),"application/pdf");
//            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//
//            Intent intent = Intent.createChooser(target, "Open File");
//            try {
//                startActivity(intent);
//            } catch (ActivityNotFoundException e) {
//                Toast.makeText(this, "No PDF reader found", Toast.LENGTH_SHORT).show();
//                // Instruct the user to install a PDF reader here, or something
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
    }

    private void loadList() {
        loadingAnimation.setVisibility(View.VISIBLE);
        dbref.child(curDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                filtered = new ArrayList<>();
                try {
                    GenericTypeIndicator<HashMap<String, String>> ind = new GenericTypeIndicator<HashMap<String, String>>() {
                    };
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, String> map = data.getValue(ind);
                        if(map.containsKey("status")&&map.get("status").equals(Order.STATUS_PENDING))
                            filtered.add(map);
                    }
                    loadingAnimation.setVisibility(View.GONE);
                    listviewOrder.setAdapter(new OrderActivity.ListviewOrderAdapter(filtered));
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

    public class ListviewOrderAdapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> data;

        public ListviewOrderAdapter(ArrayList<HashMap<String, String>> arr) {
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
                v = inflater.inflate(R.layout.orders, null);
            }

            final TextView textview1 = (TextView) v.findViewById(R.id.textview1);
            final TextView textview2 = (TextView) v.findViewById(R.id.textview2);
            final TextView textview3 = (TextView) v.findViewById(R.id.textview3);
            final ImageView imageviewCall = (ImageView) v.findViewById(R.id.imageviewCall);
            final ImageView imageview1Dir = (ImageView) v.findViewById(R.id.imageviewDir);
            final LinearLayout manageOrderView = (LinearLayout) v.findViewById(R.id.manageOrderView);
            final TextView textviewCancel = (TextView) v.findViewById(R.id.textviewCancel);
            final TextView textviewPostpone = (TextView) v.findViewById(R.id.textviewPostpone);
            final TextView textviewDeliver = (TextView) v.findViewById(R.id.textviewDeliver);

            if (details.getString("type", "").equals("Admin")) {
                manageOrderView.setVisibility(View.VISIBLE);
            } else {
                manageOrderView.setVisibility(View.GONE);
            }

            double lat = Double.parseDouble(filtered.get(position).get("lat"));
            double lng = Double.parseDouble(filtered.get(position).get("lng"));
            String orderID = filtered.get(position).get("orderID");
            long delTime = Long.parseLong(filtered.get(position).get("delTime"));

            cart = new Gson().fromJson(filtered.get(position).get("cart"),new TypeToken<ArrayList<HashMap<String, String>>>() { }.getType());
            StringBuilder tmpOrder = new StringBuilder();
            for(HashMap<String, String> map : cart) {
                tmpOrder.append(map.get("qty")).append(" * ").append(map.get("name")).append("\n");
            }

            textview1.setText(filtered.get(position).get("name"));
            textview2.setText(filtered.get(position).get("area"));
            textview3.setText(tmpOrder.toString());

            imageviewCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    Intent inv = new Intent();
                    inv.setAction(Intent.ACTION_CALL);
                    inv.setData(Uri.parse("tel:".concat(filtered.get(position).get("contact"))));
                    startActivity(inv);
                }
            });
            imageview1Dir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    Intent inv = new Intent();
                    inv.setAction(Intent.ACTION_VIEW);
                    inv.setData(Uri.parse("google.navigation:q=".concat(String.valueOf(lat).concat(",".concat(String.valueOf(lng))))));
                    if(inv.resolveActivity(getPackageManager())!=null) {
                        startActivity(inv);
                    }
                    else
                    {
                        Toast.makeText(OrderActivity.this, "No app found for navigation", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            textviewCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, Object> mpUpdate = new HashMap<>();
                    mpUpdate.put("status", Order.STATUS_CANCELLED);
                    dbref.child(curDate).child(orderID).updateChildren(mpUpdate);
                    Toast.makeText(OrderActivity.this, "Cancelled successfully", Toast.LENGTH_SHORT).show();
                }
            });
            textviewPostpone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    long updatedDelTime = delTime + 86400000L;
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(updatedDelTime);
                    key = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());

                    HashMap<String, String> mpUpdate = new HashMap<>();
                    mpUpdate = filtered.get(position);
                    mpUpdate.put("delTime",Long.toString(updatedDelTime));

                    dbref.child(curDate).child(orderID).removeValue();
                    dbref.child(key).child(orderID).setValue(mpUpdate);
                    Toast.makeText(OrderActivity.this, "Postponed successfully", Toast.LENGTH_SHORT).show();
                    loadList();
                }
            });
            textviewDeliver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, Object> mpUpdate = new HashMap<>();
                    mpUpdate.put("status", Order.STATUS_DELIVERED);
                    dbref.child(curDate).child(orderID).updateChildren(mpUpdate);
                    Toast.makeText(OrderActivity.this, "Delivered successfully", Toast.LENGTH_SHORT).show();
                }
            });

            return v;
        }
    }

}