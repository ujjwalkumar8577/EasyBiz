package com.ujjwalkumar.easybiz.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Customer {
    String custID,name,user,lat,lng,img,area,address,contact,time;

    public Customer() {
        this.custID = "custID";
        this.name = "name";
        this.user = "user";
        this.lat = "lat";
        this.lng = "lng";
        this.img = "img";
        this.area = "area";
        this.address = "address";
        this.contact = "contact";
        this.time = "time";
    }

    public Customer(String custID, String name, String user, String lat, String lng, String img, String area, String address, String contact) {
        Calendar cal = Calendar.getInstance();
        this.custID = custID;
        this.name = name;
        this.user = user;
        this.lat = lat;
        this.lng = lng;
        this.img = img;
        this.area = area;
        this.address = address;
        this.contact = contact;
        this.time = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(cal.getTime());
    }

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
