package com.chalmers.gyarados.split.model;

import java.util.List;
import java.util.Map;


public class User {

    private String name;
    private String userID;
    private String photoUrl;
    private List<Review> reviews;


    public User(String name, String photoUrl) {
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public User(String name, String userID, String photoUrl, List<Review> reviews) {
        this.name = name;
        this.userID = userID;
        this.photoUrl = photoUrl;
        this.reviews=reviews;
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

    public void setPhotoUrl(String photoUrl) {this.photoUrl = photoUrl;}

    public String getPhotoUrl() { return photoUrl; }

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


    public List<Review> getReviews() {
        return reviews;
    }
}
