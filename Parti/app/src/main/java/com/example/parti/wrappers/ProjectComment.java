package com.example.parti.wrappers;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ProjectComment implements Serializable {

    public static final String CLASS_ID = "project_comment";
    public static final int COMMENT_BODY_LENGTH = 2000;
    public static final String DEFAULT_COMMENT_HINT = "This user didn't leave any words.";

    // [start of field constants]
    public static final String SENDER_ID_FIELD = "senderId";
    public static final String COMMENT_FIELD = "comment";
    public static final String RATING_FIELD = "rating";
    public static final String LAST_UPDATE_DATE_FIELD = "lastUpdateDate";
    // [end of field constants]

    private String senderId;
    private String comment;
    private int rating;
    private String lastUpdateDate;

    public ProjectComment() {}

    public ProjectComment(
            @NonNull String senderId,
            @NonNull String comment,
            int rating,
            @NonNull String lastUpdateDate) {
        this.senderId = senderId;
        this.comment = comment;
        this.rating = rating;
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getSenderId() {
        return this.senderId;
    }
    public String getComment() {
        return this.comment;
    }
    public int getRating() {return rating;}
    public String getLastUpdateDate() {return lastUpdateDate;}

    public void setSenderId(String senderId) {this.senderId = senderId;}
    public void setComment(String comment) {this.comment = comment;}
    public void setRating(int rating) {this.rating = rating;}
    public void setLastUpdateDate(String lastUpdateDate) {this.lastUpdateDate = lastUpdateDate;}
}
