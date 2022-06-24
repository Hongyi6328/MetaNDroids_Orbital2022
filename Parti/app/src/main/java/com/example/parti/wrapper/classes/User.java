package com.example.parti.wrapper.classes;

import com.example.parti.Parti;

import java.util.ArrayList;
import java.util.List;

public class User {

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

    public User() {}

    public User(String uuid, String email) {
        this(uuid, email, uuid, DEFAULT_PROFILE_IMAGE_ID, DEFAULT_PARTICIPATION_POINTS, new ArrayList<>(), new ArrayList<>(),
                Major.DEFAULT, DEFAULT_YEAR_OF_MATRIC, DEFAULT_USER_SELF_DESCRIPTION, new ArrayList<>());
    }

    public User(String uuid, String email, String alias, String profileImageId, long participationPoints, List<String> projectsPosted,
                List<String> projectParticipated, Major major, String yearOfMatric, String selfDescription,
                List<String> commentsPosted) {
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
    List<String> getCommentsPosted() {return commentsPosted;}

    public void setAlias(String alias) {this.alias = alias;}
    public void setMajor(Major major) {this.major = major;}
    public void setYearOfMatric(String yearOfMatric) {this.yearOfMatric = yearOfMatric;}
    public void setSelfDescription(String selfDescription) {this.selfDescription = selfDescription;}
}
