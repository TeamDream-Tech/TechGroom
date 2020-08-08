package com.example.passion.models;

public class ModelPost {

    String pId, pTitle, PDescription, pLikes, pComments, PImage, PTime, uid, uEmail, uDp, uName;

    public ModelPost(){

    }

    public ModelPost(String pId, String pTitle, String PDescription, String pLikes, String pComments, String PImage, String PTime, String uid, String uEmail, String uDp, String uName) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.PDescription = PDescription;
        this.pLikes = pLikes;
        this.pComments = pComments;
        this.PImage = PImage;
        this.PTime = PTime;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uName = uName;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getPDescription() {
        return PDescription;
    }

    public void setPDescription(String PDescription) {
        this.PDescription = PDescription;
    }

    public String getpLikes() {
        return pLikes;
    }

    public void setpLikes(String pLikes) {
        this.pLikes = pLikes;
    }

    public String getpComments() {
        return pComments;
    }

    public void setpComments(String pComments) {
        this.pComments = pComments;
    }

    public String getPImage() {
        return PImage;
    }

    public void setPImage(String PImage) {
        this.PImage = PImage;
    }

    public String getPTime() {
        return PTime;
    }

    public void setPTime(String PTime) {
        this.PTime = PTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }
}
