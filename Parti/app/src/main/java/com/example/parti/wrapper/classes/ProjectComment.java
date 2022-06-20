package com.example.parti.wrapper.classes;

public class ProjectComment {
    private long senderId;
    private String comment;

    public ProjectComment(long senderId, String comment) {
        this.senderId = senderId;
        this.comment = comment;
    }

    public void changeComment(long senderId, String comment) {
        if (senderId == this.senderId) this.comment = comment;
    }

    public long getSenderId() {
        return this.senderId;
    }

    public String getComment() {
        return this.comment;
    }
}
