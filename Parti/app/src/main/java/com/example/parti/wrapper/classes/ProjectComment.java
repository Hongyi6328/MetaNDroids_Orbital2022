package com.example.parti.wrapper.classes;

public class ProjectComment {
    private String senderId;
    private String projectId;
    private String comment;

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
