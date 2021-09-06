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

import com.ujjwalkumar.easybiz.R;
import com.ujjwalkumar.easybiz.activity.StaffActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class StaffAdapter extends BaseAdapter {
    Context context;
    ArrayList<HashMap<String, String>> data;

    public StaffAdapter(Context context, ArrayList<HashMap<String, String>> arr) {
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
            v = inflater.inflate(R.layout.custom_staffs, null);
        }

        final TextView textview1 = v.findViewById(R.id.textview1);
        final TextView textview2 = v.findViewById(R.id.textview2);
        final ImageView imageviewCall = v.findViewById(R.id.imageviewCall);
        final ImageView imageview1Dir = v.findViewById(R.id.imageviewDir);

        textview1.setText(data.get(position).get("name"));
        textview2.setText(data.get(position).get("number"));

        imageviewCall.setOnClickListener(_view -> {
            Intent inv = new Intent();
            inv.setAction(Intent.ACTION_CALL);
            inv.setData(Uri.parse("tel:".concat(data.get(position).get("number"))));
            context.startActivity(inv);
        });
        imageview1Dir.setOnClickListener(_view -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(context, StaffActivity.class);
            in.putExtra("uid", data.get(position).get("uid"));
            in.putExtra("name", data.get(position).get("name"));
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(in);
//                finish();
        });

        return v;
    }
}
