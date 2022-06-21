package com.example.parti;

import android.content.Intent;
import android.os.Bundle;

import com.example.parti.data.model.LoggedInUser;
import com.example.parti.ui.login.LoginActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.parti.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //no longer needed
    //public static final String FIREBASE_URL = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZhZ3N5cWtmeGtvY252ZGtveXhlIiwicm9sZSI6ImFub24iLCJpYXQiOjE2NTM3ODA4ODUsImV4cCI6MTk2OTM1Njg4NX0.AHfdIb0SEb4svskC9BEiM7p7fzP6xBFY58P3Ql9rA-s";
    //public static final String FIREBASE_KEY = "https://fagsyqkfxkocnvdkoyxe.supabase.co";

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    //This keeps track of the current user's details
    private LoggedInUser loggedInUser;
    private boolean loggedIn;

    //Use the fragments as a singleton
    BrowseProjectsFragment browseProjectsFragment = new BrowseProjectsFragment();
    MyProjectsFragment myProjectsFragment = new MyProjectsFragment();
    IdeaPoolFragment ideaPoolFragment = new IdeaPoolFragment();
    MyProfileFragment myProfileFragment = new MyProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setSupportActionBar(binding.toolbar); //TODO don't know what this does

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        /*
        // the fab has been deleted
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
         */


        //Instead of using replace() or attach() and detach(), hide() and show() keep the instance
        //of the fragments, so every time the user switches between fragments the main activity does
        //not need to instantiate a new fragment, saving a lot of resources.
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .add(R.id.fragmentContainerView, browseProjectsFragment)
                .addToBackStack(null)
                .add(R.id.fragmentContainerView, myProjectsFragment)
                .addToBackStack(null)
                .add(R.id.fragmentContainerView, ideaPoolFragment)
                .addToBackStack(null)
                .add(R.id.fragmentContainerView, myProfileFragment)
                .addToBackStack(null)
                .show(browseProjectsFragment)
                .hide(myProjectsFragment)
                .hide(ideaPoolFragment)
                .hide(myProfileFragment)
                .commit();

        this.loggedIn = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_my_profile) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!this.loggedIn) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }
    }

    public void loginSuccessful(LoggedInUser loggedInUser) {
        this.loggedIn = true;
        this.loggedInUser = loggedInUser;
    }

    //Configure actions for selecting menu items in navigation bar
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (item.getItemId()) {
            case (R.id.action_browse_projects):
                fragmentManager.beginTransaction()
                        .show(browseProjectsFragment)
                        .hide(myProjectsFragment)
                        .hide(ideaPoolFragment)
                        .hide(myProfileFragment)
                        .commit();
                break;
            case (R.id.action_my_projects):
                fragmentManager.beginTransaction()
                        .hide(browseProjectsFragment)
                        .show(myProjectsFragment)
                        .hide(ideaPoolFragment)
                        .hide(myProfileFragment)
                        .commit();
                break;
            case (R.id.action_idea_pool):
                fragmentManager.beginTransaction()
                        .hide(browseProjectsFragment)
                        .hide(myProjectsFragment)
                        .show(ideaPoolFragment)
                        .hide(myProfileFragment)
                        .commit();
                break;
            case (R.id.action_my_profile):
                fragmentManager.beginTransaction()
                        .hide(browseProjectsFragment)
                        .hide(myProjectsFragment)
                        .hide(ideaPoolFragment)
                        .show(myProfileFragment)
                        .commit();
                break;
            default:
                break;
        }
        return true;
    }
}