package com.example.parti.wrapper.classes;

import com.google.firebase.auth.FirebaseAuth;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Project {

    public static final int SHORT_DESCRIPTION_LENGTH = 300;
    public static final int DEFAULT_RANKING = 0;

    private String id; //required
    private String name; //required
    private ProjectType projectType; //required
    private boolean concluded; //required
    private String admin; //required
    private List<String> developers; //required
    private List<String> participants; //required
    private double ranking; //required
    private String description; //required
    private List<ProjectComment> comments; //required
    private ProjectFeedback projectFeedback; //required
    private String launchDate; //required
    private int imageId;

    public Project() {}

    public Project(String id, String name, String description, int imageId) {
        this(id, name, ProjectType.APP, false, FirebaseAuth.getInstance().getCurrentUser().getUid(),
                List.of(FirebaseAuth.getInstance().getCurrentUser().getUid()), new ArrayList<>(), DEFAULT_RANKING,
                description, new ArrayList<>(), new ProjectFeedback(), LocalDate.now().toString(), 0);
    }

    public Project(String id, String name, ProjectType projectType, boolean concluded, String admin,
                   List<String> developers, List<String> participants, double ranking, String description,
                   List<ProjectComment> comments, ProjectFeedback projectFeedback, String launchDate,
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
    public String getAdmin() {return admin;}
    public List<String> getDevelopers() {return developers;}
    public List<String> getParticipants() {return participants;}
    public double getRanking() {return ranking;}
    public String getDescription() {return description;}
    public List<ProjectComment> getComments() {return comments;}
    public ProjectFeedback getProjectFeedback() {return projectFeedback;}
    public String getLaunchDateString() {return launchDate;}
    public int getImageId() {return imageId;}

    public String getShortDescription() {
        return description.length() > SHORT_DESCRIPTION_LENGTH
                ? description.substring(0, SHORT_DESCRIPTION_LENGTH) + "...>"
                : description;
    }
}
