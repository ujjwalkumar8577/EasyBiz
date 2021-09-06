package com.ujjwalkumar.easybiz.helper;

public class CartItem {
    String itemID, name;
    double price, weight, quantity;

    public CartItem(String itemID, String name, double price, double weight, double quantity) {
        this.itemID = itemID;
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.quantity = quantity;
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
        return price;
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                ", name='" + name +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
