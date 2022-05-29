package com.example.parti.data;

import com.example.parti.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    LoggedInUser loggedInUser;

    public Result<LoggedInUser> login(String username, String password) {

        LoggedInUser fakeUser =
                new LoggedInUser(
                        java.util.UUID.randomUUID().toString(),
                        "Jane Doe");

        try {
            // TODO: handle loggedInUser authentication



            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}