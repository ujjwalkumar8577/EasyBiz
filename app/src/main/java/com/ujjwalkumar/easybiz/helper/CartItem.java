package com.ujjwalkumar.easybiz.helper;

public class CartItem {
    String id, name, price, weight, quantity;

    public CartItem(String id, String name, String price, String weight, String quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
