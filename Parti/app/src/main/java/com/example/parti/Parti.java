package com.example.parti;

import android.app.Application;

import com.example.parti.wrappers.Major;
import com.example.parti.wrappers.User;

// An Application class that is used to deal with global variables
public class Parti extends Application {

    public static final int SHORT_DESCRIPTION_LENGTH = 300;
    public static final int DEFAULT_RANKING = 0;
    public static final String DEFAULT_PROFILE_IMAGE_ID = "0";
    public static final String DEFAULT_PROJECT_IMAGE_ID = "s0";
    public static final String DEFAULT_YEAR_OF_MATRIC = "2022";
    public static final String DEFAULT_USER_SELF_DESCRIPTION = "This user hasn't left any self description";
    public static final long DEFAULT_PARTICIPATION_POINTS = 500;
    public static final String PROJECT_COLLECTION_PATH = "test-projects";
    public static final String USER_COLLECTION_PATH = "users";
    public static final String DIVIDER_HEIGHT = "20dp";
    public static final Major[] MAJORS = Major.class.getEnumConstants();
    public static final int EARLIEST_YEAR_OF_MATRIC = 2000;
    public static final int NUMBER_OF_MAJORS = 25;
    public static final int MAX_SELF_DESCRIPTION_LENGTH = 500;


    public User loggedInUser;

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public User getLoggedInUser() {return this.loggedInUser;}

    public static final double calculatePPCost(int numOfParticipants, double _PPperParticipant) {
        return numOfParticipants * _PPperParticipant / 10;
    }












    //private boolean loginStatus;
    //private FirebaseUser user = null;

    /*
    public boolean getLoginStatus() {
        return loginStatus;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void setLoginStatus(boolean loginStatus) {
        this.loginStatus = loginStatus;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }
     */
}
