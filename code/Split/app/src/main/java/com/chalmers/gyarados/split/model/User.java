package com.chalmers.gyarados.split.model;

import android.net.Uri;

import java.util.Map;


public class User {

    private String name;
    private String userID;
    private String photo;


    public User(String name, String photo) {
        this.name = name;
        this.photo = photo;
    }

    public User(String name, String userID, String photo) {
        this.name = name;
        this.userID = userID;
        this.photo = photo;
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

    public String getUserId() {
        return userID;
    }

    public void setPhoto(String photo) {this.photo = photo;}

    public String getPhoto() { return photo; }

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
