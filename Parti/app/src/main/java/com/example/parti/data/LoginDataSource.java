package com.example.parti.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.parti.MainActivity;
import com.example.parti.data.model.LoggedInUser;
import com.example.parti.ui.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    LoggedInUser loggedInUser;

    public Result<LoggedInUser> login(String uuid, String password) {

        this.loggedInUser = new LoggedInUser(uuid, "You");
        String[] output = new String[2]; //TODO: Hard coding: try to avoid circumventing the barrier
        Context context = null; //TODO: try to reference the current context

        try {

            // TODO: handle loggedInUser authentication

            //Extract identity verification from url

            StringRequest stringRequest = new StringRequest(Request.Method.GET, MainActivity.FIREBASE_KEY, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("items");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject authenticationPackage = jsonArray.getJSONObject(i);

                            JSONObject jsonObjectReadings = authenticationPackage.getJSONObject("verified");
                            String timestamp = authenticationPackage.getString("timestamp").substring(0, 19);
                            output[0] = jsonObjectReadings.toString();
                            output[1] = timestamp;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
            //requestQueue.

            return new Result.Success<>(this.loggedInUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}