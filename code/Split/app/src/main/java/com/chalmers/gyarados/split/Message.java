package com.chalmers.gyarados.split;

public class Message {

    String message;
    User sender;
    String createdAt;

    public Message(String message) {
        this.message = message;
        this.createdAt="now";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getSender() {
        return sender;
    }
}
