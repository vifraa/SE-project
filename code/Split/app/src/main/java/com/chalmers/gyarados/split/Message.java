package com.chalmers.gyarados.split;

public class Message {

    String message;
    User sender;
    String createdAt;

    public Message(String message, String createdAt) {
        this.message = message;
        this.createdAt=createdAt;
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
