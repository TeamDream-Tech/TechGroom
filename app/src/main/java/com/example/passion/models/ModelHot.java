package com.example.passion.models;

public class ModelHot {
    String uid, source, pId, pTitle, PDescription, PImage, PTime, pComments, views, newPImage, gerne;

    public ModelHot(){

    }

    public ModelHot(String uid, String source, String pId, String pTitle, String PDescription, String PImage, String PTime, String pComments, String views, String newPImage, String gerne) {
        this.uid = uid;
        this.source = source;
        this.pId = pId;
        this.pTitle = pTitle;
        this.PDescription = PDescription;
        this.PImage = PImage;
        this.PTime = PTime;
        this.pComments = pComments;
        this.views = views;
        this.newPImage = newPImage;
        this.gerne = gerne;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public String getpComments() {
        return pComments;
    }

    public void setpComments(String pComments) {
        this.pComments = pComments;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getNewPImage() {
        return newPImage;
    }

    public void setNewPImage(String newPImage) {
        this.newPImage = newPImage;
    }

    public String getGerne() {
        return gerne;
    }

    public void setGerne(String gerne) {
        this.gerne = gerne;
    }
}
