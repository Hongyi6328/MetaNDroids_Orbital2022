package com.example.parti.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.parti.Parti;
import com.example.parti.R;
import com.example.parti.databinding.ActivityMainBinding;
import com.example.parti.wrappers.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;

    //Use the fragments as a singleton
    private BrowseProjectsFragment browseProjectsFragment;
    private BrowseUsersFragment browseUsersFragment;
    private UserProfileFragment userProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        browseProjectsFragment = new BrowseProjectsFragment();
        browseUsersFragment = new BrowseUsersFragment();
        userProfileFragment = new UserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(User.CLASS_ID, ((Parti) getApplication()).getLoggedInUser());
        userProfileFragment.setArguments(bundle);

        activityMainBinding.bottomNavigationViewMain.setOnItemSelectedListener(item -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            switch (item.getItemId()) {
                case (R.id.action_browse_projects):
                    fragmentManager.beginTransaction()
                            .show(browseProjectsFragment)
                            .hide(browseUsersFragment)
                            .hide(userProfileFragment)
                            .commit();
                    break;
                case (R.id.action_browse_users):
                    fragmentManager.beginTransaction()
                            .hide(browseProjectsFragment)
                            .show(browseUsersFragment)
                            .hide(userProfileFragment)
                            .commit();
                    break;
                case (R.id.action_my_profile):
                    fragmentManager.beginTransaction()
                            .hide(browseProjectsFragment)
                            .hide(browseUsersFragment)
                            .show(userProfileFragment)
                            .commit();
                    break;
                default:
                    break;
            }
            return true;
        });

        //Instead of using replace() or attach() and detach(), hide() and show() keep the instance
        //of the fragments, so every time the user switches between fragments the main activity does
        //not need to instantiate a new fragment, saving a lot of resources.
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .add(R.id.nav_host_fragment_main, browseProjectsFragment)
                .addToBackStack(null)
                .add(R.id.nav_host_fragment_main, browseUsersFragment)
                .addToBackStack(null)
                .add(R.id.nav_host_fragment_main, userProfileFragment)
                .addToBackStack(null)
                .show(browseProjectsFragment)
                .hide(browseUsersFragment)
                .hide(userProfileFragment)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        User user = ((Parti) getApplication()).getLoggedInUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        if (user != null) {
            String uuid = user.getUuid();
            firebaseFirestore.collection(Parti.USER_COLLECTION_PATH).document(uuid).set(user);
        }
        ((Parti) getApplication()).setLoggedInUser(null);
        FirebaseAuth.getInstance().signOut();
    }
}