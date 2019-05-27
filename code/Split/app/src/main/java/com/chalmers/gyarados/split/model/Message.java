package com.chalmers.gyarados.split.model;

import com.chalmers.gyarados.split.MessageType;

import java.util.Comparator;
import java.util.Date;

public class Message {

    private final MessageType type;
    private String content;
    private User sender;
    private Date timestamp;

    public Message(String content, User user, Date timestamp, MessageType type) {
        this.content = content;
        this.sender=user;
        this.timestamp = timestamp;
        this.type=type;
    }

    public Message(String content, User user, MessageType type) {
        this.content = content;
        this.sender=user;
        this.type=type;


    }



    public MessageType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp){this.timestamp = timestamp;}

    public User getSender() {
        return sender;
    }

}
