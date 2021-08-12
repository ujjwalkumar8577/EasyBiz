package com.ujjwalkumar.easybiz.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ujjwalkumar.easybiz.R;
import com.ujjwalkumar.easybiz.helper.CartItem;

import java.util.ArrayList;

public class CartItemAdapter extends ArrayAdapter<CartItem> {

    public CartItemAdapter(Context context, ArrayList<CartItem> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CartItem cartItem = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_items2, parent, false);
        }

        TextView textviewItemName = convertView.findViewById(R.id.textviewItemName);
        TextView textviewItemPrice = convertView.findViewById(R.id.textviewItemPrice);
        EditText textviewItemQty = convertView.findViewById(R.id.textviewItemQty);
        ImageView imageviewminus = convertView.findViewById(R.id.imageviewminus);
        ImageView imageviewplus = convertView.findViewById(R.id.imageviewplus);

        textviewItemName.setText(cartItem.getName());
        textviewItemPrice.setText(cartItem.getPrice());
        textviewItemQty.setText(cartItem.getQuantity());

        imageviewplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newQty = Integer.parseInt(cartItem.getQuantity()) + 1;
                textviewItemQty.setText(String.valueOf(newQty));
            }
        });

        imageviewminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newQty = Integer.parseInt(cartItem.getQuantity()) - 1;
                if(newQty<0)
                    newQty = 0;
                textviewItemQty.setText(String.valueOf(newQty));
            }
        });

        textviewItemQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return convertView;
    }
}
