package com.chalmers.gyarados.split.Model;

public class User {

    private String name;
    private String profileURL;


    public User(String name, String profileURL) {
        this.name = name;
        this.profileURL = profileURL;
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
}
