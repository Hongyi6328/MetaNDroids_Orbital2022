package com.example.parti.ui.main;

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
import com.example.parti.wrapper.classes.Major;
import com.example.parti.wrapper.classes.User;

import java.util.HashMap;

public class MyProfileFragment extends Fragment {

    FragmentMyProfileBinding fragmentMyProfileBinding;
    HashMap<String, Integer> majorMap = new HashMap<>();

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
        return fragmentMyProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Major[] majors = Parti.MAJORS;
        int mapSize = majors.length;
        for (int i = 0; i < mapSize; i++) majorMap.put(majors[i].toString(), i);

        User user = ((Parti) getActivity().getApplication()).getLoggedInUser();

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

    }
}