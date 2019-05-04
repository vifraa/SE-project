package com.chalmers.gyarados.split.Model;

import java.util.List;

public class Group {

    private String id;
    private List<Message> messages;
    private List<User> users;

    public String getId() {
        return id;
    }

    public Group(String id, List<Message> messages, List<User> users) {
        this.id=id;
        this.messages = messages;
        this.users = users;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<User> getUsers() {
        return users;
    }
}
