package com.ujjwalkumar.easybiz.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Feedback {
    String message,user,name,email,time;

    public Feedback() {
        this.message = "message";
        this.user = "user";
        this.name = "name";
        this.email = "email";
        this.time = "time";
    }

    public Feedback(String message, String user, String name, String email) {
        Calendar cal = Calendar.getInstance();
        this.message = message;
        this.user = user;
        this.name = name;
        this.email = email;
        this.time = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(cal.getTime());
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
