package com.example.instagram.Models;

public class Notification {

    private String userid;
    private String textnotification;
    private String postID;
    private Boolean isPost;

    public Notification(String userid, String textnotification, String postID, Boolean isPost) {
        this.userid = userid;
        this.textnotification = textnotification;
        this.postID = postID;
        this.isPost = isPost;
    }

    public Notification() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTextnotification() {
        return textnotification;
    }

    public void setTextnotification(String textnotification) {
        this.textnotification = textnotification;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public Boolean getPost() {
        return isPost;
    }

    public void setPost(Boolean post) {
        isPost = post;
    }
}
