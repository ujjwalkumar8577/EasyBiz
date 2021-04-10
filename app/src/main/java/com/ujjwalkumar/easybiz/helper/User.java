package com.ujjwalkumar.easybiz.helper;

public class User {
    String name,email,password,uid,type,number;

    public User() {
        this.name = "name";
        this.email = "email";
        this.password = "password";
        this.uid = "uid";
        this.type = "type";
        this.number = "number";
    }

    public User(String name, String email, String password, String uid, String type, String number) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.uid = uid;
        this.type = type;
        this.number = number;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
