package com.example.parti.wrappers;

import java.io.Serializable;

public class ProjectComment implements Serializable {
    private String senderId;
    private String projectId;
    private String comment;
    private String rating;

    public ProjectComment(String senderId, String projectId, String comment) {
        this.senderId = senderId;
        this.projectId = projectId;
        this.comment = comment;
    }

    public void changeComment(String senderId, String comment) {
        if (senderId.equals(this.senderId)) this.comment = comment;
    }

    public String getSenderId() {
        return this.senderId;
    }
    public String getProjectId() {
        return this.projectId;
    }
    public String getComment() {
        return this.comment;
    }
}
