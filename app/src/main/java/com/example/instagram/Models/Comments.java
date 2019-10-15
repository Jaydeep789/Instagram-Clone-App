package com.example.instagram.Models;

public class Comments {

    private String comment;
    private String publisher;

    public Comments(String comment, String publisher) {
        this.comment = comment;
        this.publisher = publisher;
    }

    public Comments() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
