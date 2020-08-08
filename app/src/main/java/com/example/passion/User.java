package com.example.passion;

public class User {
    public String name, email, phone, password, uid, image, onlineStatus;

    public User(){

    }

    public User(String name, String email, String phone, String password, String uid, String image, String onlineStatus) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.uid = uid;
        this.image = image;
        this.onlineStatus = onlineStatus;

    }
}
