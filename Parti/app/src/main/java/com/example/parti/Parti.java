package com.example.parti;

import android.app.Application;

import com.google.firebase.auth.FirebaseUser;

// An Application class that is used to deal with global variables
public class Parti extends Application {

    public static final int SHORT_DESCRIPTION_LENGTH = 300;
    public static final int DEFAULT_RANKING = 0;
    public static final String DEFAULT_PROFILE_IMAGE_ID = "0";
    public static final String DEFAULT_PROJECT_IMAGE_ID = "0";



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
