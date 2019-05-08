package com.chalmers.gyarados.split.model;

import java.util.List;

public class Group {

    private String groupId;
    private List<Message> messages;
    private List<User> users;

    public String getId() {
        return groupId;
    }

    public Group(String groupId, List<Message> messages, List<User> users) {
        this.groupId = groupId;
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
