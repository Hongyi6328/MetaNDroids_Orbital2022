package com.example.parti.wrapper.classes;

import java.util.Date;
import java.util.List;

public class Project {

    public static final int SHORT_DESCRIPTION_LENGTH = 300;

    private String id; //required
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
    private int imageId;

    public Project() {}

    public Project(String id, String name, String description, int imageId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageId = imageId;
    }

    public Project(String id, String name, ProjectType projectType, boolean concluded, long admin,
                   List<Long> developers, List<Long> participants, double ranking, String description,
                   List<ProjectComment> comments, ProjectFeedback projectFeedback, Date launchDate,
                   int imageId) {
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
        this.imageId = imageId;
    }

    public String getProjectId() {return id;}
    public String getName() {return name;}
    public ProjectType getProjectType() {return projectType;}
    public boolean isConcluded() {return concluded;}
    public long getAdmin() {return admin;}
    public List<Long> getDevelopers() {return developers;}
    public List<Long> getParticipants() {return participants;}
    public double getRanking() {return ranking;}
    public String getDescription() {return description;}
    public List<ProjectComment> getComments() {return comments;}
    public ProjectFeedback getProjectFeedback() {return projectFeedback;}
    public Date getLaunchDate() {return launchDate;}
    public int getImageId() {return imageId;}

    public String getShortDescription() {
        return description.length() > SHORT_DESCRIPTION_LENGTH
                ? description.substring(0, SHORT_DESCRIPTION_LENGTH) + "...>"
                : description;
    }
}
