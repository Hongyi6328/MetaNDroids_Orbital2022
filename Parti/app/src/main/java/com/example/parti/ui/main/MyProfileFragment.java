package com.example.parti.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.parti.Parti;
import com.example.parti.databinding.FragmentMyProfileBinding;
import com.example.parti.ui.login.LoginActivity;
import com.example.parti.wrapper.classes.Major;
import com.example.parti.wrapper.classes.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

public class MyProfileFragment extends Fragment {

    FragmentMyProfileBinding fragmentMyProfileBinding;
    HashMap<String, Integer> majorMap = new HashMap<>();
    boolean dataRead = false;

    public static final int EARLIEST_YEAR_OF_MATRIC = Parti.EARLIEST_YEAR_OF_MATRIC;

    public MyProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentMyProfileBinding = FragmentMyProfileBinding.inflate(inflater, container, false);

        fragmentMyProfileBinding.logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                dataRead = false;
                Intent loginIntent = new Intent(MyProfileFragment.this.getContext(), LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        fragmentMyProfileBinding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return fragmentMyProfileBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        User user = ((Parti) getActivity().getApplication()).getLoggedInUser();
        if (user != null && !dataRead) {
            Major[] majors = Parti.MAJORS;
            int mapSize = majors.length;
            for (int i = 0; i < mapSize; i++) majorMap.put(majors[i].toString(), i);


            Glide.with(fragmentMyProfileBinding.profileImage.getContext())
                    .load(android.R.drawable.sym_def_app_icon) //TODO
                    .into(fragmentMyProfileBinding.profileImage);

            String emailString = "Email: " + user.getEmail();
            String participationPointsString = "Participation Points: " + user.getParticipationPoints();
            String userIdString = "User ID: " + user.getUuid();

            fragmentMyProfileBinding.email.setText(emailString);
            fragmentMyProfileBinding.alias.setText(participationPointsString);
            fragmentMyProfileBinding.userId.setText(userIdString);

            String aliasHint = "Alias: " + user.getAlias();
            String selfDecriptionHint = "Self Description: " + user.getSelfDescription();

            fragmentMyProfileBinding.alias.setHint(aliasHint);
            fragmentMyProfileBinding.yearOfMatric.setSelection(Integer.parseInt(user.getYearOfMatric()) - EARLIEST_YEAR_OF_MATRIC);
            fragmentMyProfileBinding.major.setSelection(majorMap.get(user.getMajor().toString()));
            fragmentMyProfileBinding.selfDescription.setHint(selfDecriptionHint);

            dataRead = true;
        }
    }
}