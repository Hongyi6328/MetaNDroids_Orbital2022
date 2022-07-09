package com.example.parti.wrappers;

import androidx.annotation.NonNull;

import com.example.parti.Parti;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {

    public static final String DEFAULT_PROFILE_IMAGE_ID = Parti.DEFAULT_PROFILE_IMAGE_ID;
    public static final String DEFAULT_YEAR_OF_MATRIC = Parti.DEFAULT_YEAR_OF_MATRIC;
    public static final String DEFAULT_USER_SELF_DESCRIPTION = Parti.DEFAULT_USER_SELF_DESCRIPTION;
    public static final long DEFAULT_PARTICIPATION_POINTS = Parti.DEFAULT_PARTICIPATION_POINTS;

    // TODO: Consider String References vs Wrapper Classes
    String uuid;
    String email;
    String alias;
    String profileImageId;
    long participationPoints;
    List<String> projectsPosted;
    List<String> projectParticipated;
    Major major;
    String yearOfMatric;
    String selfDescription;
    List<String> commentsPosted;
    Map<String, Double> participationPointsEarned;

    public User() {}

    public User(@NonNull String uuid,
                @NonNull String email) {
        this(
                uuid,
                email,
                uuid,
                DEFAULT_PROFILE_IMAGE_ID,
                DEFAULT_PARTICIPATION_POINTS,
                new ArrayList<>(),
                new ArrayList<>(),
                Major.DEFAULT,
                DEFAULT_YEAR_OF_MATRIC,
                DEFAULT_USER_SELF_DESCRIPTION,
                new ArrayList<>(),
                new HashMap<>());
    }

    public User(@NonNull String uuid,
                @NonNull String email,
                @NonNull String alias,
                @NonNull String profileImageId,
                long participationPoints,
                @NonNull List<String> projectsPosted,
                @NonNull List<String> projectParticipated,
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
        this.projectParticipated = projectParticipated;
        this.major = major;
        this.yearOfMatric = yearOfMatric;
        this.selfDescription = selfDescription;
        this.commentsPosted = commentsPosted;
        this.participationPointsEarned = participationPointsEarned;
    }

    public String getUuid() {return uuid;}
    public String getEmail() {return email;}
    public String getAlias() {return alias;}
    public long getParticipationPoints() {return participationPoints;}
    public String getProfileImageId() {return profileImageId;}
    public List<String> getProjectsPosted() {return projectsPosted;}
    public List<String> getProjectParticipated() {return projectParticipated;}
    public Major getMajor() {return major;}
    public String getYearOfMatric() {return yearOfMatric;}
    public String getSelfDescription() {return selfDescription;}
    public List<String> getCommentsPosted() {return commentsPosted;}
    public Map<String, Double> getParticipationPointsEarned() {return participationPointsEarned;}

    public void setAlias(String alias) {this.alias = alias;}
    public void setParticipationPoints(long participationPoints) {this.participationPoints = participationPoints;}
    public void setProfileImageId(String profileImageId) {this.profileImageId = profileImageId;}
    public void setProjectsPosted(List<String> projectsPosted) {this.projectsPosted = projectsPosted;}
    public void setProjectParticipated(List<String> projectParticipated) {this.projectParticipated = projectParticipated;}
    public void setMajor(Major major) {this.major = major;}
    public void setYearOfMatric(String yearOfMatric) {this.yearOfMatric = yearOfMatric;}
    public void setSelfDescription(String selfDescription) {this.selfDescription = selfDescription;}
    public void setCommentsPosted(List<String> commentsPosted) {this.commentsPosted = commentsPosted;}
    public void setParticipationPointsEarned(Map<String, Double> participationPointsEarned) {this.participationPointsEarned = participationPointsEarned;}
    public void increaseParticipationPoints(double offset) {this.participationPoints += offset;}
}
