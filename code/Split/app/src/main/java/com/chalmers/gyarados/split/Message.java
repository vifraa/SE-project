package com.chalmers.gyarados.split;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

    private final MessageType type;
    private String message;
    private User sender;
    private Date createdAt;

    public Message(String message, User user, Date createdAt, MessageType type) {
        this.message = message;
        this.sender=user;
        this.createdAt=createdAt;
        this.type=type;


    }



    public MessageType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public User getSender() {
        return sender;
    }
}
