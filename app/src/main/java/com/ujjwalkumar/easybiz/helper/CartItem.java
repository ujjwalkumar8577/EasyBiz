package com.ujjwalkumar.easybiz.helper;

import java.io.Serializable;
import java.math.BigDecimal;

import com.hishd.tinycart.model.Item;

public class CartItem implements Item, Serializable {
    String itemID, name;
    double price, weight;

    public CartItem(String itemID, String name, double price, double weight) {
        this.itemID = itemID;
        this.name = name;
        this.price = price;
        this.weight = weight;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public BigDecimal getItemPrice() {
        return new BigDecimal(this.price);
    }

    @Override
    public String getItemName() {
        return this.name;
    }
}