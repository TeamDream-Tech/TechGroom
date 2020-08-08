package com.example.passion.models;

public class ModelUsers {

    // use same name as in firebase database
    String name, email, phone, uid, image, onlineStatus;

    public ModelUsers(){

    }

    public ModelUsers(String name, String email, String phone, String uid, String image, String onlineStatus) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.uid = uid;
        this.image = image;
        this.onlineStatus = onlineStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOnlineStatus(){
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus){
        this.onlineStatus = onlineStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
