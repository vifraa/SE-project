package com.chalmers.gyarados.split.model;

import com.google.gson.JsonElement;

import java.util.Objects;

public class User {

    private String name;
    private String profileURL;
    private String id;


    public User(String name, String profileURL) {
        this.name = name;
        this.profileURL = profileURL;
    }

    public User(String name, String userID, String profileURL) {
        this.name = name;
        this.id = userID;
        this.profileURL = profileURL;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public String getUserId() {
        return id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return name.equals(user.name) &&
                id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
