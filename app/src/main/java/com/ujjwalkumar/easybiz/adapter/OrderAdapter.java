package com.ujjwalkumar.easybiz.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ujjwalkumar.easybiz.R;
import com.ujjwalkumar.easybiz.helper.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class OrderAdapter extends BaseAdapter {
    Context context;
    String userType, curDate;
    ArrayList<HashMap<String, String>> data;
    FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    DatabaseReference dbref = fbdb.getReference("orders");

    public OrderAdapter(Context context,String userType, String curDate, ArrayList<HashMap<String, String>> arr) {
        this.context = context;
        this.userType = userType;
        this.curDate = curDate;
        this.data = arr;
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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = view;
        if (v == null) {
            v = inflater.inflate(R.layout.custom_orders, null);
        }

        final TextView textview1 = v.findViewById(R.id.textview1);
        final TextView textview2 = v.findViewById(R.id.textview2);
        final TextView textview3 = v.findViewById(R.id.textview3);
        final ImageView imageviewCall = v.findViewById(R.id.imageviewCall);
        final ImageView imageview1Dir = v.findViewById(R.id.imageviewDir);
        final LinearLayout manageOrderView = v.findViewById(R.id.manageOrderView);
        final TextView textviewCancel = v.findViewById(R.id.textviewCancel);
        final TextView textviewPostpone = v.findViewById(R.id.textviewPostpone);
        final TextView textviewDeliver = v.findViewById(R.id.textviewDeliver);

        if (userType.equals("Admin")) {
            manageOrderView.setVisibility(View.VISIBLE);
        } else {
            manageOrderView.setVisibility(View.GONE);
        }

        double lat = Double.parseDouble(data.get(position).get("lat"));
        double lng = Double.parseDouble(data.get(position).get("lng"));
        String orderID = data.get(position).get("orderID");
        long delTime = Long.parseLong(data.get(position).get("delTime"));

        ArrayList<HashMap<String, String>> cart = new Gson().fromJson(data.get(position).get("cart"), new TypeToken<ArrayList<HashMap<String, String>>>() {}.getType());
        StringBuilder tmpOrder = new StringBuilder();
        for(HashMap<String, String> map : cart) {
            tmpOrder.append(map.get("qty")).append(" * ").append(map.get("name")).append("\n");
        }

        textview1.setText(data.get(position).get("name"));
        textview2.setText(data.get(position).get("area"));
        textview3.setText(tmpOrder.toString());

        imageviewCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                Intent inv = new Intent();
                inv.setAction(Intent.ACTION_CALL);
                inv.setData(Uri.parse("tel:".concat(data.get(position).get("contact"))));
                context.startActivity(inv);
            }
        });
        imageview1Dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                Intent inv = new Intent();
                inv.setAction(Intent.ACTION_VIEW);
                inv.setData(Uri.parse("google.navigation:q=".concat(String.valueOf(lat).concat(",".concat(String.valueOf(lng))))));
                if(inv.resolveActivity(context.getPackageManager())!=null)
                    context.startActivity(inv);
                else
                    Toast.makeText(context, "No app found for navigation", Toast.LENGTH_SHORT).show();
            }
        });
        textviewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> mpUpdate = new HashMap<>();
                mpUpdate.put("status", Order.STATUS_CANCELLED);
                dbref.child(curDate).child(orderID).updateChildren(mpUpdate);
                Toast.makeText(context, "Cancelled successfully", Toast.LENGTH_SHORT).show();
            }
        });
        textviewPostpone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long updatedDelTime = delTime + 86400000L;
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(updatedDelTime);
                String key = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());

                HashMap<String, String> mpUpdate = new HashMap<>();
                mpUpdate = data.get(position);
                mpUpdate.put("delTime",Long.toString(updatedDelTime));

                dbref.child(curDate).child(orderID).removeValue();
                dbref.child(key).child(orderID).setValue(mpUpdate);
                Toast.makeText(context, "Postponed successfully", Toast.LENGTH_SHORT).show();
//                loadList();
            }
        });
        textviewDeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> mpUpdate = new HashMap<>();
                mpUpdate.put("status", Order.STATUS_DELIVERED);
                dbref.child(curDate).child(orderID).updateChildren(mpUpdate);
                Toast.makeText(context, "Delivered successfully", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}