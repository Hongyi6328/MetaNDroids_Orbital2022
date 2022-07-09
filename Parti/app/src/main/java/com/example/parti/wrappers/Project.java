package com.example.parti.wrappers;

import androidx.annotation.NonNull;

import com.example.parti.Parti;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {

    public static final int SHORT_DESCRIPTION_LENGTH = Parti.SHORT_DESCRIPTION_LENGTH;
    public static final int DEFAULT_RANKING = Parti.DEFAULT_RANKING;

    // TODO: Consider String References vs Wrapper Classes
    private String projectId; //required
    private String name; //required
    private ProjectType projectType; //required
    private boolean concluded; //required
    private String admin; //required
    private List<String> developers; //required
    private List<String> participants; //required
    private int numParticipants; //required
    private int numParticipantsNeeded; // required
    private double ranking; //required
    private String description; //required
    private List<String> comments; //required
    private long totalRating; //required
    private String launchDate; //required
    private String imageId;
    private List<Double> participationPoints;

    public Project() {}

    public Project(@NonNull String id,
                   @NonNull String name,
                   @NonNull String description,
                   @NonNull String imageId) {
        this(id, name, ProjectType.APP, false, FirebaseAuth.getInstance().getCurrentUser().getUid(),
                List.of(FirebaseAuth.getInstance().getCurrentUser().getUid()), new ArrayList<>(), 0, 0, DEFAULT_RANKING,
                description, new ArrayList<>(), 0, LocalDate.now().toString(), imageId,
                new ArrayList<>());
    }

    public Project(@NonNull String id,
                   @NonNull String name,
                   @NonNull ProjectType projectType,
                   boolean concluded,
                   @NonNull String admin,
                   @NonNull List<String> developers,
                   @NonNull List<String> participants,
                   int numParticipants,
                   int numParticipantsNeeded,
                   double ranking,
                   @NonNull String description,
                   @NonNull List<String> comments,
                   long totalRating, String launchDate,
                   @NonNull String imageId,
                   @NonNull List<Double> participationPoints) {
        this.projectId = id;
        this.name = name;
        this.projectType = projectType;
        this.concluded = concluded;
        this.admin = admin;
        this.developers = developers;
        this.participants = participants;
        this.numParticipants = numParticipants;
        this.numParticipantsNeeded = numParticipantsNeeded;
        this.ranking = ranking;
        this.description = description;
        this.comments = comments;
        this.totalRating = totalRating;
        this.launchDate = launchDate;
        this.imageId = imageId;
        this.participationPoints = participationPoints;
    }

    public String getProjectId() {return projectId;}
    public String getName() {return name;}
    public ProjectType getProjectType() {return projectType;}
    public boolean isConcluded() {return concluded;}
    //public boolean getConcluded() {return concluded;}
    public String getAdmin() {return admin;}
    public List<String> getDevelopers() {return developers;}
    public List<String> getParticipants() {return participants;}
    public int getNumParticipants() {return numParticipants;}
    public int getNumParticipantsNeeded() {return numParticipantsNeeded;}
    public double getRanking() {return ranking;}
    public String getDescription() {return description;}
    public List<String> getComments() {return comments;}
    public long getTotalRating() {return totalRating;}
    public String getLaunchDate() {return launchDate;}
    public String getImageId() {return imageId;}
    public List<Double> getParticipationPoints() {return participationPoints;}

    //public void setProjectId() {return projectId;}
    public void setName(String name) {this.name = name;}
    public void setProjectType(ProjectType projectType) {this.projectType = projectType;}
    public void setConcluded(boolean concluded) {this.concluded = concluded;}
    //public boolean getConcluded() {return concluded;}
    public void setAdmin(String admin) {this.admin = admin;}
    public void setDevelopers(List<String> developers) {this.developers = developers;}
    public void setParticipants(List<String> participants) {this.participants = participants;}
    public void setNumParticipants(int numParticipants) {this.numParticipants = numParticipants;}
    public void setNumParticipantsNeeded(int numParticipantsNeeded) {this.numParticipantsNeeded = numParticipantsNeeded;}
    public void setRanking(double ranking) {this.ranking = ranking;}
    public void setDescription(String description) {this.description = description;}
    public void setComments(List<String> comments) {this.comments = comments;}
    public void setTotalRating(long totalRating) {this.totalRating = totalRating;}
    public void setLaunchDate(String launchDate) {this.launchDate = launchDate;}
    public void setImageId(String imageId) {this.imageId = imageId;}
    public void setParticipationPoints(List<Double> participationPoints) {this.participationPoints = participationPoints;}

    public String getShortDescription() {
        return description.length() > SHORT_DESCRIPTION_LENGTH
                ? description.substring(0, SHORT_DESCRIPTION_LENGTH) + "...>"
                : description;
    }
}
