package com.example.passion.models;

public class ModelChat {
    String message, receiver, sender, timestamp, result, reply, whoreply;

    public ModelChat(){
    }

    public ModelChat(String message, String receiver, String sender, String timestamp, String result, String reply, String whoreply) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timestamp = timestamp;
        this.result = result;
        this.reply = reply;
        this.whoreply = whoreply;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getWhoreply() {
        return whoreply;
    }

    public void setWhoreply(String whoreply) {
        this.whoreply = whoreply;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
