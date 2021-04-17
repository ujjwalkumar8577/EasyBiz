package com.ujjwalkumar.easybiz.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyLocation {
    String uid,lat,lng,acc,time;

    public MyLocation() {
        this.uid = "uid";
        this.lat = "lat";
        this.lng = "lng";
        this.acc = "acc";
        this.time = "time";
    }

    public MyLocation(String uid, String lat, String lng, String acc, long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        this.uid = uid;
        this.lat = lat;
        this.lng = lng;
        this.acc = acc;
        this.time = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(cal.getTime());
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
