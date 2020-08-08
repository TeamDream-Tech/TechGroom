package com.example.passion.models;

public class ModelQuestions {
    String uid, uName, uEmail, uDp, pId, pTitle, PDescription, PImage, PTime, pLikes, pComments, bestanswer, views;

    public ModelQuestions(){

    }

    public ModelQuestions(String uid, String uName, String uEmail, String uDp, String pId, String pTitle, String PDescription, String PImage, String PTime, String pLikes, String pComments, String bestanswer, String views) {
        this.uid = uid;
        this.uName = uName;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.pId = pId;
        this.pTitle = pTitle;
        this.PDescription = PDescription;
        this.PImage = PImage;
        this.PTime = PTime;
        this.pLikes = pLikes;
        this.pComments = pComments;
        this.bestanswer = bestanswer;
        this.views = views;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
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

    public String getBestanswer() {
        return bestanswer;
    }

    public void setBestanswer(String bestanswer) {
        this.bestanswer = bestanswer;
    }
}
