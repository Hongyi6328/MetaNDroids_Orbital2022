package com.example.parti.wrapper.classes;

import java.util.Date;
import java.util.List;

public class Project {

    private long id; //required
    private String name; //required
    private ProjectType projectType; //required
    private boolean concluded; //required
    private long admin; //required
    private List<Long> developers; //required
    private List<Long> participants; //required
    private double ranking; //required
    private String description; //required
    private List<ProjectComment> comments; //required
    private ProjectFeedback projectFeedback; //required
    private Date launchDate; //required

    public Project(long id, String name, ProjectType projectType, boolean concluded, long admin,
                   List<Long> developers, List<Long> participants, double ranking, String description,
                   List<ProjectComment> comments, ProjectFeedback projectFeedback, Date launchDate) {
        this.id = id;
        this.name = name;
        this.projectType = projectType;
        this.concluded = concluded;
        this.admin = admin;
        this.developers = developers;
        this.participants = participants;
        this.ranking = ranking;
        this.description = description;
        this.comments = comments;
        this.projectFeedback = projectFeedback;
        this.launchDate = launchDate;
    }


}
