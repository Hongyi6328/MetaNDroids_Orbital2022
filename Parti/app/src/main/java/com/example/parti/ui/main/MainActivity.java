package com.example.parti.ui.main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.parti.Parti;
import com.example.parti.R;
import com.example.parti.databinding.ActivityMainBinding;
import com.example.parti.wrappers.User;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    //no longer needed
    //public static final String FIREBASE_URL = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZhZ3N5cWtmeGtvY252ZGtveXhlIiwicm9sZSI6ImFub24iLCJpYXQiOjE2NTM3ODA4ODUsImV4cCI6MTk2OTM1Njg4NX0.AHfdIb0SEb4svskC9BEiM7p7fzP6xBFY58P3Ql9rA-s";
    //public static final String FIREBASE_KEY = "https://fagsyqkfxkocnvdkoyxe.supabase.co";

    //private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding activityMainBinding;

    //Use the fragments as a singleton
    private BrowseProjectsFragment browseProjectsFragment;
    private BrowseUsersFragment browseUsersFragment;
    //private IdeaPoolFragment ideaPoolFragment;
    private UserProfileFragment userProfileFragment;

    //This keeps track of the current user's details
    //migrated to public class Parti
    /*
    private LoggedInUser loggedInUser;
    private boolean loggedIn;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        browseProjectsFragment = new BrowseProjectsFragment();
        browseUsersFragment = new BrowseUsersFragment();
        //ideaPoolFragment = new IdeaPoolFragment();
        userProfileFragment = new UserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(User.CLASS_ID, ((Parti) getApplication()).getLoggedInUser());
        //bundle.putSerializable(UserProfileFragment.CURRENT_USER_INDICATOR, true);
        userProfileFragment.setArguments(bundle);

        activityMainBinding.bottomNavigationViewMain.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            //Configure actions for selecting menu items in navigation bar
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                switch (item.getItemId()) {
                    case (R.id.action_browse_projects):
                        fragmentManager.beginTransaction()
                                .show(browseProjectsFragment)
                                .hide(browseUsersFragment)
                                //.hide(ideaPoolFragment)
                                .hide(userProfileFragment)
                                .commit();
                        break;
                    case (R.id.action_browse_users):
                        fragmentManager.beginTransaction()
                                .hide(browseProjectsFragment)
                                .show(browseUsersFragment)
                                //.hide(ideaPoolFragment)
                                .hide(userProfileFragment)
                                .commit();
                        break;
                    /*
                    case (R.id.action_idea_pool):
                        fragmentManager.beginTransaction()
                                .hide(browseProjectsFragment)
                                .hide(browseUsersFragment)
                                .show(ideaPoolFragment)
                                .hide(userProfileFragment)
                                .commit();
                        break;
                    */
                    case (R.id.action_my_profile):
                        fragmentManager.beginTransaction()
                                .hide(browseProjectsFragment)
                                .hide(browseUsersFragment)
                                //.hide(ideaPoolFragment)
                                .show(userProfileFragment)
                                .commit();
                        break;
                    default:
                        break;
                }
                return true;
            }
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
                //.add(R.id.nav_host_fragment_main, ideaPoolFragment)
                //.addToBackStack(null)
                .add(R.id.nav_host_fragment_main, userProfileFragment)
                .addToBackStack(null)
                .show(browseProjectsFragment)
                .hide(browseUsersFragment)
                //.hide(ideaPoolFragment)
                .hide(userProfileFragment)
                .commit();

        //((Parti) this.getApplication()).setLoginStatus(false);
        //FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        //if (firebaseAuth.getCurrentUser() != null) firebaseAuth.signOut();

        /*
        // The following block of code has been placed by nav_graph and main_bottom_navigation_view
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        */

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


        // The following block is no longer used. Its function is implemented by BottomNavigationView and NavGraph
        //BottomNavigationView navView = activityMainBinding.mainBottomNavigationView;

        /*
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_browse_projects, R.id.nav_my_projects, R.id.nav_idea_pool, R.id.action_my_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.mainBottomNavigationView, navController);
        */
    }

    @Override
    public void onResume() {
        super.onResume();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            //Intent loginIntent = new Intent(this, LoginActivity.class);
            //startActivity(loginIntent);
            finish();
        }

        /*
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String uuid = firebaseUser.getUid();
            User user = ((Parti) getApplication()).getLoggedInUser();
            if (user == null || !user.getUuid().equals(firebaseUser.getUid())) {
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                DocumentReference documentReference =
                        firebaseFirestore.collection(Parti.USER_COLLECTION_PATH).document(uuid);
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User loggedInUser = documentSnapshot.toObject(User.class);
                        ((Parti) getApplication()).setLoggedInUser(loggedInUser);
                    }
                });
            }
        }
        */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //((Parti) getApplication()).setUser(null);
        //((Parti) getApplication()).setLoginStatus(false);
        User user = ((Parti) getApplication()).getLoggedInUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String uuid = user.getUuid();
        if (user != null) firebaseFirestore.collection(Parti.USER_COLLECTION_PATH).document(uuid).set(user);
        ((Parti) getApplication()).setLoggedInUser(null);
        FirebaseAuth.getInstance().signOut();
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    */

    /*
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
    */

    /*
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
    */
}