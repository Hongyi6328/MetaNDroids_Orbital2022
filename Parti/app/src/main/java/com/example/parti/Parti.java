package com.example.parti;

import android.app.Application;

import com.google.firebase.auth.FirebaseUser;

// An Application class that is used to deal with global variables
public class Parti extends Application {
    private boolean loginStatus;
    private FirebaseUser user;

    public boolean isLoginStatus() {
        return loginStatus;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void setLoginStatus(boolean loginStatus) {
        this.loginStatus = loginStatus; //TODO: This is not quite safe
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

}
