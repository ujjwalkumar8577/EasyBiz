package com.ujjwalkumar.easybiz.helper;

public class Item {
    String itemID,name,price,weight,img;

    public Item() {
        this.itemID = "itemID";
        this.name = "name";
        this.price = "price";
        this.weight = "weight";
        this.img = "img";
    }

    public Item(String itemID, String name, String price, String weight, String img) {
        this.itemID = itemID;
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.img = img;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
