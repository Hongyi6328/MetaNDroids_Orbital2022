package com.example.parti.wrappers;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ProjectComment implements Serializable {

    // [start of field constants]
    public static final String SENDER_ID_FIELD = "senderId";
    public static final String COMMENT_FIELD = "comment";
    public static final String RATING_FIELD = "rating";
    // [end of field constants]

    private String senderId;
    private String comment;
    private int rating;

    public ProjectComment() {}

    public ProjectComment(
            @NonNull String senderId,
            @NonNull String comment,
            int rating) {
        this.senderId = senderId;
        this.comment = comment;
        this.rating = rating;
    }

    public String getSenderId() {
        return this.senderId;
    }
    public String getComment() {
        return this.comment;
    }
    public int getRating() {return rating;}

    public void setSenderId(String senderId) {this.senderId = senderId;}
    public void setComment(String comment) {this.comment = comment;}
    public void setRating(int rating) {this.rating = rating;}
}
