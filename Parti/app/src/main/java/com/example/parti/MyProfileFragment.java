package com.example.parti;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class MyProfileFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}