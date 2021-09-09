package com.ujjwalkumar.easybiz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ujjwalkumar.easybiz.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemAdapter extends BaseAdapter {
    Context context;
    ArrayList<HashMap<String, String>> data;

    public ItemAdapter(Context context, ArrayList<HashMap<String, String>> arr) {
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
            v = inflater.inflate(R.layout.custom_items1, null);
        }

        final TextView textViewItemName = v.findViewById(R.id.textViewItemName);
        final TextView textViewItemPrice = v.findViewById(R.id.textViewItemPrice);
        final TextView textViewItemWeight = v.findViewById(R.id.textViewItemWeight);
        final ImageView imageView = v.findViewById(R.id.imageView);

        textViewItemName.setText(data.get(position).get("name"));
        textViewItemPrice.setText(data.get(position).get("price"));
        textViewItemWeight.setText(data.get(position).get("weight"));
        Glide.with(context).load(data.get(position).get("img"))
                .placeholder(R.drawable.imageupload)
                .into(imageView);

        return v;
    }
}