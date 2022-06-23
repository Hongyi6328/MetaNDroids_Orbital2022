package com.example.parti.wrapper.classes;

import com.example.parti.Parti;

import java.util.List;

public class User {

    public static final String DEFAULT_PROFILE_IMAGE = Parti.DEFAULT_PROFILE_IMAGE_ID;

    // TODO: Consider String References vs Wrapper Classes
    String uuid;
    String email;
    String profileImageId;
    long participationPoints;
    List<String> projectsPosted;
    List<String> projectParticipated;
    Major major;
    String yearOfMatric;
    String selfDescription;
    List<String> commentsPosted;

    public User(String uuid, String email) {
        this(uuid, email, DEFAULT_PROFILE_IMAGE, )
    }

    public User(String uuid, String email, String profileImageId, long participationPoints, List<String> projectsPosted,
                List<String> projectParticipated, Major major, String yearOfMatric, String selfDescription,
                List<String> commentsPosted) {
        this.uuid = uuid;
        this.email = email;
        this.profileImageId = profileImageId;
        this.participationPoints = participationPoints;
        this.projectsPosted = projectsPosted;
        this.projectParticipated = projectParticipated;
        this.major = major;
        this.yearOfMatric = yearOfMatric;
        this.selfDescription = selfDescription;
        this.commentsPosted = commentsPosted;
    }
}
