package com.example.instagram.Models;

public class User {

    private String id;
    private String username;
    private String fullname;
    private String bio;
    private String imageURL;

    public User(String id, String username, String fullname, String bio, String imageURL) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.bio = bio;
        this.imageURL = imageURL;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
