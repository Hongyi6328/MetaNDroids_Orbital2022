package com.example.parti.ui.main;

import android.os.Bundle;

import com.example.parti.Parti;
import com.example.parti.R;
import com.example.parti.databinding.ActivityViewUserBinding;
import com.example.parti.wrappers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewUserActivity extends AppCompatActivity {

    private ActivityViewUserBinding activityViewUserBinding;
    private UserProfileFragment userProfileFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityViewUserBinding = ActivityViewUserBinding.inflate(getLayoutInflater());
        setContentView(activityViewUserBinding.getRoot());

        User user = (User) getIntent().getExtras().get(User.CLASS_ID);
        if (user == null) {
            String uuid = getIntent().getExtras().getString(User.UUID_FIELD);
            FirebaseFirestore
                    .getInstance()
                    .collection(Parti.USER_COLLECTION_PATH)
                    .document(uuid).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                User user = task.getResult().toObject(User.class);
                                setMyProfileFragment(user);
                            } else {
                                Toast.makeText(ViewUserActivity.this, "User not found", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });
        } else {
            setMyProfileFragment(user);
        }

        activityViewUserBinding.buttonViewUserBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setMyProfileFragment(User user) {
        userProfileFragment = new UserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(User.CLASS_ID, user);
        //bundle.putBoolean(UserProfileFragment.CURRENT_USER_INDICATOR, false);
        userProfileFragment.setArguments(bundle);
        displayFragment();
    }

    private void displayFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .add(R.id.nav_host_fragment_view_user, userProfileFragment)
                .addToBackStack(null)
                .show(userProfileFragment)
                .commit();
    }
}