package com.example.parti.data;

import android.content.Context;

import com.example.parti.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
@Deprecated
public class LoginDataSource {

    LoggedInUser loggedInUser;

    public Result<LoggedInUser> login(String uuid, String password) {

        this.loggedInUser = new LoggedInUser(uuid, "You");
        String[] output = new String[2];
        Context context = null;

        try {
            return new Result.Success<>(this.loggedInUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {}
}