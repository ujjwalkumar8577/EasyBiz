package com.ujjwalkumar.easybiz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ujjwalkumar.easybiz.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ListviewItemAdapter3 extends BaseAdapter {
    Context context;
    ArrayList<HashMap<String, String>> data;
    Double amt;

    public ListviewItemAdapter3(Context context, ArrayList<HashMap<String, String>> arr) {
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
            v = inflater.inflate(R.layout.custom_items2, null);
        }

        final TextView textviewItemName = v.findViewById(R.id.textviewItemName);
        final TextView textviewItemPrice = v.findViewById(R.id.textviewItemPrice);
        final EditText textviewItemQty = v.findViewById(R.id.textviewItemQty);
        final ImageView imageviewminus = v.findViewById(R.id.imageviewminus);
        final ImageView imageviewplus = v.findViewById(R.id.imageviewplus);

        textviewItemName.setText(data.get(position).get("name"));
        textviewItemPrice.setText(data.get(position).get("price"));
        textviewItemQty.setText(data.get(position).get("qty"));

        imageviewplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                HashMap<String, String> itm = data.get(position);
                textviewItemQty.setText(String.valueOf((long) (Double.parseDouble(textviewItemQty.getText().toString()) + 1)));
                itm.put("qty", textviewItemQty.getText().toString());
                amt = amt + Double.parseDouble(data.get(position).get("price"));
//                setAmount(amt);
//                for (int i = 0; i < cart.size(); i++) {
//                    if (cart.get(i).get("id").equals(data.get(position).get("id"))) {
//                        cart.remove(i);
//                        break;
//                    }
//                }
//                cart.add(itm);
            }
        });
        imageviewminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (Double.parseDouble(textviewItemQty.getText().toString()) > 0) {
                    HashMap<String, String> itm = data.get(position);
                    textviewItemQty.setText(String.valueOf((long) (Double.parseDouble(textviewItemQty.getText().toString()) - 1)));
                    itm.put("qty", textviewItemQty.getText().toString());
                    amt = amt - Double.parseDouble(data.get(position).get("price"));
//                    setAmount(amt);
//                    for (int i = 0; i < cart.size(); i++) {
//                        if (cart.get(i).get("id").equals(data.get(position).get("id"))) {
//                            cart.remove(i);
//                            break;
//                        }
//                    }
//                    if (Double.parseDouble(textviewItemQty.getText().toString()) > 0) {
//                        cart.add(itm);
//                    }
                }
            }
        });

        return v;
    }
}