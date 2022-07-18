package com.example.parti.wrappers;

import androidx.annotation.NonNull;

import com.example.parti.Parti;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable, Updatable {

    /*
    public static final String DEFAULT_PROFILE_IMAGE_ID = Parti.DEFAULT_PROFILE_IMAGE_ID;
    public static final String DEFAULT_YEAR_OF_MATRIC = Parti.DEFAULT_YEAR_OF_MATRIC;
    public static final String DEFAULT_USER_SELF_DESCRIPTION = Parti.DEFAULT_USER_SELF_DESCRIPTION;
    public static final long DEFAULT_PARTICIPATION_POINTS = Parti.DEFAULT_PARTICIPATION_POINTS;
     */

    // [start of field constants]
    public static final String UUID_FIELD = "uuid";
    public static final String EMAIL_FIELD = "email";
    public static final String ALIAS_FIELD = "alias";
    public static final String PROFILE_IMAGE_ID_FIELD = "profileImageId";
    public static final String PARTICIPATION_POINTS_FIELD = "participationPoints";
    public static final String PROJECT_POSTED_FIELD = "projectsPosted";
    public static final String PROJECTS_PARTICIPATED_FIELD = "projectsParticipated";
    public static final String MAJOR_FIELD = "major";
    public static final String YEAR_OF_MATRIC_FIELD = "yearOfMatric";
    public static final String SELF_DESCRIPTION_FIELD = "selfDescription";
    public static final String COMMENTS_POSTED_FIELD = "commentsPosted";
    public static final String PARTICIPATION_POINTS_EARNED_FIELD = "participationPointsEarned";
    // [end of field constants]

    private String uuid;
    private String email;
    private String alias;
    private String profileImageId;
    private double participationPoints;
    private List<String> projectsPosted;
    private List<String> projectsParticipated;
    private Major major;
    private String yearOfMatric;
    private String selfDescription;
    private List<String> commentsPosted; // The id of commented projects
    private Map<String, Double> participationPointsEarned;
    //private Map<String, Double> participationPointsDonated;

    public User() {}

    public User(@NonNull String uuid,
                @NonNull String email) {
        this(
                uuid,
                email,
                uuid,
                Parti.DEFAULT_PROFILE_IMAGE_ID,
                Parti.DEFAULT_PARTICIPATION_POINTS,
                new ArrayList<>(),
                new ArrayList<>(),
                Major.DEFAULT,
                Parti.DEFAULT_YEAR_OF_MATRIC,
                Parti.DEFAULT_USER_SELF_DESCRIPTION,
                new ArrayList<>(),
                new HashMap<>());
    }

    public User(@NonNull String uuid,
                @NonNull String email,
                @NonNull String alias,
                @NonNull String profileImageId,
                double participationPoints,
                @NonNull List<String> projectsPosted,
                @NonNull List<String> projectsParticipated,
                @NonNull Major major,
                @NonNull String yearOfMatric,
                @NonNull String selfDescription,
                @NonNull List<String> commentsPosted,
                @NonNull Map<String, Double> participationPointsEarned) {
        this.uuid = uuid;
        this.email = email;
        this.alias = alias;
        this.profileImageId = profileImageId;
        this.participationPoints = participationPoints;
        this.projectsPosted = projectsPosted;
        this.projectsParticipated = projectsParticipated;
        this.major = major;
        this.yearOfMatric = yearOfMatric;
        this.selfDescription = selfDescription;
        this.commentsPosted = commentsPosted;
        this.participationPointsEarned = participationPointsEarned;
    }

    public String getUuid() {return uuid;}
    public String getEmail() {return email;}
    public String getAlias() {return alias;}
    public double getParticipationPoints() {return participationPoints;}
    public String getProfileImageId() {return profileImageId;}
    public List<String> getProjectsPosted() {return projectsPosted;}
    public List<String> getProjectsParticipated() {return projectsParticipated;}
    public Major getMajor() {return major;}
    public String getYearOfMatric() {return yearOfMatric;}
    public String getSelfDescription() {return selfDescription;}
    public List<String> getCommentsPosted() {return commentsPosted;}
    public Map<String, Double> getParticipationPointsEarned() {return participationPointsEarned;}

    public void setAlias(String alias) {this.alias = alias;}
    public void setParticipationPoints(long participationPoints) {this.participationPoints = participationPoints;}
    public void setProfileImageId(String profileImageId) {this.profileImageId = profileImageId;}
    public void setProjectsPosted(List<String> projectsPosted) {this.projectsPosted = projectsPosted;}
    public void setProjectsParticipated(List<String> projectsParticipated) {this.projectsParticipated = projectsParticipated;}
    public void setMajor(Major major) {this.major = major;}
    public void setYearOfMatric(String yearOfMatric) {this.yearOfMatric = yearOfMatric;}
    public void setSelfDescription(String selfDescription) {this.selfDescription = selfDescription;}
    public void setCommentsPosted(List<String> commentsPosted) {this.commentsPosted = commentsPosted;}
    public void setParticipationPointsEarned(Map<String, Double> participationPointsEarned) {this.participationPointsEarned = participationPointsEarned;}

    public String shortDescription() {
        return selfDescription.length() > Parti.SHORT_DESCRIPTION_LENGTH
                ? selfDescription.substring(0, Parti.SHORT_DESCRIPTION_LENGTH)
                : selfDescription;
    }

    public void increaseParticipationPoints(double offset) {this.participationPoints += offset;}
    public void addProjectPosted(Project project) {
        if (projectsPosted.contains(project.getProjectId())) return;
        projectsPosted.add(project.getProjectId());
    }
    public void addProjectParticipated(Project project) {
        String projectId = project.getProjectId();
        if (projectsParticipated.contains(projectId)) return;
        projectsParticipated.add(projectId);
    }
    public void addComment(String projectId) {
        if (commentsPosted.contains(projectId)) return;
        commentsPosted.add(projectId);
    }
    public void removeComment(String projectId) {
        commentsPosted.remove(projectId);
    }
    public void participate(Project project) {
        increaseParticipationPoints(project.getParticipationPoints().get(0));
        addProjectParticipated(project);
        double cumulatedPp = participationPointsEarned.getOrDefault(project.getProjectId(), 0.0);
        cumulatedPp += project.getParticipationPoints().get(0);
        participationPointsEarned.put(project.getProjectId(), cumulatedPp);
    }
    public void donate(Project project, double amount) {
        increaseParticipationPoints(-amount);
    }

    @Override
    public void update() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection(Parti.USER_COLLECTION_PATH).document(uuid);
        documentReference.set(this);
    }
}
