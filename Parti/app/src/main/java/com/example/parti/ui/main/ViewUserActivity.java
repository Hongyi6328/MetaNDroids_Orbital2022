package com.example.parti.ui.main;

import android.os.Bundle;

import com.example.parti.R;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.parti.databinding.ActivityViewUserBinding;

public class ViewUserActivity extends AppCompatActivity {

    private ActivityViewUserBinding activityViewUserBinding;
    private MyProfileFragment myProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityViewUserBinding = ActivityViewUserBinding.inflate(getLayoutInflater());
        setContentView(activityViewUserBinding.getRoot());

        myProfileFragment = new MyProfileFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .add(R.id.nav_host_fragment_view_user, myProfileFragment)
                .addToBackStack(null)
                .show(myProfileFragment)
                .commit();
    }
}