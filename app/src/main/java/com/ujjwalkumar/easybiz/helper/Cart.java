package com.ujjwalkumar.easybiz.helper;

import java.util.HashMap;

public class Cart {
    HashMap<String, CartItem> items;

    public Cart() {
        items = new HashMap<>();
    }

    public void increaseItemQuantity(String itemID, String name, double price, double weight) {
        double quantity = getItemQuantity(itemID) + 1;
        items.put(itemID, new CartItem(itemID, name, price, weight, quantity));
    }

    public void decreaseItemQuantity(String itemID, String name, double price, double weight) {
        double quantity = getItemQuantity(itemID) - 1;
        if(quantity<=0)
            items.remove(itemID);
        else
            items.put(itemID, new CartItem(itemID, name, price, weight, quantity));
    }

    public void setItemQuantity(String itemID, String name, double price, double weight, double quantity) {
        if(quantity<=0)
            items.remove(itemID);
        else
            items.put(itemID, new CartItem(itemID, name, price, weight, quantity));
    }

    public double getItemQuantity(String itemID) {
        if(items.containsKey(itemID))
            return items.get(itemID).getQuantity();
        else
            return 0;
    }

    public double getCartAmount() {
        double amt = 0.0d;
        for(String key: items.keySet()) {
            double q = items.get(key).getQuantity();
            double p = items.get(key).getPrice();
            amt += 1.0*p*q;
        }
        return amt;
    }
}