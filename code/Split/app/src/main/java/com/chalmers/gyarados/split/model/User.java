package com.chalmers.gyarados.split.model;

import java.util.Map;


public class User {

    private String name;
    private String profileURL;
    private String userID;


    public User(String name, String profileURL) {
        this.name = name;
        this.profileURL = profileURL;
    }

    public User(String name, String userID, String profileURL) {
        this.name = name;
        this.userID = userID;
        this.profileURL = profileURL;
    }

    public User(Map user) {
        name=(String)user.get("name");
        userID=(String)user.get("userID");
    }

    public void setId(String id) {
        this.userID = id;
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
        return userID;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return name.equals(user.name) &&
                userID.equals(user.userID);
    }

    @Override
    public int hashCode() {
        return userID.hashCode();
    }
}
