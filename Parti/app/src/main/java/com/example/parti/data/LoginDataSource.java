package com.example.parti.data;

import android.util.Log;
import android.widget.Toast;

import com.example.parti.MainActivity;
import com.example.parti.data.model.LoggedInUser;

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

        try {

            // TODO: handle loggedInUser authentication
            String[] output = new String[1]; //Hard coding: try to avoid

            //Extract identity verification from url
            StringRequest stringRequest = new StringRequest(Request.Method.GET, MainActivity.FIREBASE_KEY, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("items");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject airQuality = jsonArray.getJSONObject(i);

                            JSONObject jsonObjectReadings = airQuality.getJSONObject("readings");
                            JSONObject jsonObjectpm25 = jsonObjectReadings.getJSONObject("pm25_one_hourly");
                            String timestamp = airQuality.getString("timestamp").substring(0, 19);

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