package com.ujjwalkumar.easybiz.helper;

import java.util.Calendar;

public class Order {
    String orderID,name,user,lat,lng,area,address,contact,cart,delTime,status;

    final static String STATUS_PENDING = "pending";
    final static String STATUS_DELIVERED = "delivered";
    final static String STATUS_CANCELLED = "cancelled";

    public Order() {
        this.orderID = "orderID";
        this.name = "name";
        this.user = "user";
        this.lat = "lat";
        this.lng = "lng";
        this.area = "area";
        this.address = "address";
        this.contact = "contact";
        this.cart = "{}";
        this.delTime = "delTime";
        this.status = STATUS_PENDING;
    }

    public Order(String orderID, String name, String user, String lat, String lng, String area, String address, String contact, String cart) {
        Calendar cal = Calendar.getInstance();
        this.orderID = orderID;
        this.name = name;
        this.user = user;
        this.lat = lat;
        this.lng = lng;
        this.area = area;
        this.address = address;
        this.contact = contact;
        this.cart = cart;
        this.delTime = Long.toString(cal.getTimeInMillis()+86400000L);
        this.status = STATUS_PENDING;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCart() {
        return cart;
    }

    public void setCart(String cart) {
        this.cart = cart;
    }

    public String getDelTime() {
        return delTime;
    }

    public void setDelTime(String delTime) {
        this.delTime = delTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
