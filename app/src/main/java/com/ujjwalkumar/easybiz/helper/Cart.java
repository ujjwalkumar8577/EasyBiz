package com.ujjwalkumar.easybiz.helper;

import java.util.HashMap;

public class Cart {
    private final HashMap<String, QtyItem> items;

    public Cart() {
        items = new HashMap<>();
    }

    public void updateItem(String itemID, String price, String quantity) {
        if(Integer.parseInt(quantity)<=0) {
            items.remove(itemID);
        }
        QtyItem i = new QtyItem(itemID, price, quantity);
        items.put(itemID, i);
    }

    public String getItemQuantity(String itemID) {
        if(items.containsKey(itemID))
            return String.valueOf(items.get(itemID).getQuantity());
        else
            return "0";
    }

    public double getCartAmount() {
        double amt = 0.0d;
        for(String key: items.keySet()) {
            int q = items.get(key).getQuantity();
            double p = items.get(key).getPrice();
            amt += 1.0*p*q;
        }
        return amt;
    }

    public String toString() {
        return items.toString();
    }
}

class QtyItem {
    String itemID;
    int quantity;
    double price;

    public QtyItem(String itemID, String price, String quantity) {
        this.itemID = itemID;
        this.price = Double.parseDouble(price);
        this.quantity = Integer.parseInt(quantity);
    }

    public QtyItem(String itemID, double price, int quantity) {
        this.itemID = itemID;
        this.price = price;
        this.quantity = quantity;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return ("{ " + quantity + " " + price + " }");
    }
}
