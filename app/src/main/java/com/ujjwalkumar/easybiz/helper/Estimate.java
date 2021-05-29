package com.ujjwalkumar.easybiz.helper;

import java.util.Calendar;

public class Estimate {
    String estimateID,name,user,lat,lng,area,address,contact,cart,createTime,status;

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_DONE = "done";

    public Estimate() {
        this.estimateID = "estimateID";
        this.name = "name";
        this.user = "user";
        this.lat = "lat";
        this.lng = "lng";
        this.area = "area";
        this.address = "address";
        this.contact = "contact";
        this.cart = "{}";
        this.createTime = "createTime";
        this.status = STATUS_PENDING;
    }

    public Estimate(String estimateID, String name, String user, String lat, String lng, String area, String address, String contact, String cart) {
        Calendar cal = Calendar.getInstance();
        this.estimateID = estimateID;
        this.name = name;
        this.user = user;
        this.lat = lat;
        this.lng = lng;
        this.area = area;
        this.address = address;
        this.contact = contact;
        this.cart = cart;
        this.createTime = Long.toString(cal.getTimeInMillis());
        this.status = STATUS_PENDING;
    }

    public String getEstimateID() {
        return estimateID;
    }

    public void setEstimateID(String estimateID) {
        this.estimateID = estimateID;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
