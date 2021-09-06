package com.ujjwalkumar.easybiz.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ujjwalkumar.easybiz.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerAdapter extends BaseAdapter {
    Context context;
    ArrayList<HashMap<String, String>> data;

    public CustomerAdapter(Context context, ArrayList<HashMap<String, String>> arr) {
        this.context = context;
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
            v = inflater.inflate(R.layout.custom_customers, null);
        }

        final TextView textview1 = v.findViewById(R.id.textview1);
        final TextView textview2 = v.findViewById(R.id.textview2);
        final TextView textview3 = v.findViewById(R.id.textview3);
        final ImageView imageviewCall = v.findViewById(R.id.imageviewCall);
        final ImageView imageview1Dir = v.findViewById(R.id.imageviewDir);

        double lat = Double.parseDouble(data.get(position).get("lat"));
        double lng = Double.parseDouble(data.get(position).get("lng"));

        textview1.setText(data.get(position).get("name"));
        textview2.setText(data.get(position).get("contact"));
        textview3.setText(data.get(position).get("area"));

        imageviewCall.setOnClickListener(_view -> {
            Intent inv = new Intent();
            inv.setAction(Intent.ACTION_CALL);
            inv.setData(Uri.parse("tel:".concat(data.get(position).get("contact"))));
            context.startActivity(inv);
        });
        imageview1Dir.setOnClickListener(_view -> {
            Intent inv = new Intent();
            inv.setAction(Intent.ACTION_VIEW);
            inv.setData(Uri.parse("google.navigation:q=".concat(String.valueOf(lat).concat(",".concat(String.valueOf(lng))))));
            if (inv.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(inv);
            } else {
                Toast.makeText(context, "No app found for navigation", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}