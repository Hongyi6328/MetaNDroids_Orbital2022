package com.example.parti.ui.main;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.parti.Parti;
import com.example.parti.R;
import com.example.parti.databinding.ActivityMainBinding;
import com.example.parti.wrappers.Project;
import com.example.parti.wrappers.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;
    private CompletableFuture<Void> refreshThread;

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
        Bundle userProfileBundle = new Bundle();
        userProfileBundle.putSerializable(User.CLASS_ID, ((Parti) getApplication()).getLoggedInUser());
        userProfileBundle.putBoolean(UserProfileFragment.NO_LOG_OUT, false);
        userProfileFragment.setArguments(userProfileBundle);

        refreshRankings();

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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) finish();
    }

    @Override
    public void onStop() {
        super.onStop();
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
        stopRefreshingRankings();
    }

    private void refreshRankings() {
        final long refreshInterval = 1000 * 60 * 5; //refresh every 5 min
        refreshThread = CompletableFuture.runAsync(() -> {
            activityMainBinding.progressBarMainLoading.setVisibility(View.VISIBLE);
            while (true) {
                List<Task<Void>> taskList = new ArrayList<>();
                FirebaseFirestore
                        .getInstance()
                        .collection(Parti.PROJECT_COLLECTION_PATH)
                        .get()
                        .continueWithTask(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                List<DocumentSnapshot> documentSnapshotList = querySnapshot.getDocuments();
                                for (DocumentSnapshot documentSnapshot: documentSnapshotList) {
                                    Project project = documentSnapshot.toObject(Project.class);
                                    if (project != null) taskList.add(project.updateRankings());
                                }
                            }
                            return Tasks.whenAll(taskList);
                        })
                        .addOnCompleteListener(
                        task -> activityMainBinding.progressBarMainLoading.setVisibility(View.GONE)
                );
                try {
                    Thread.sleep(refreshInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void stopRefreshingRankings() {
        refreshThread.cancel(true);
        refreshThread = null;
    }
}