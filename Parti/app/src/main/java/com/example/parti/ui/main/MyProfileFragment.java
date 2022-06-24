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
import com.example.parti.R;
import com.example.parti.databinding.FragmentMyProfileBinding;
import com.example.parti.wrapper.classes.User;

public class MyProfileFragment extends Fragment {

    FragmentMyProfileBinding fragmentMyProfileBinding;

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
        User user = ((Parti) getActivity().getApplication()).g

        Glide.with(fragmentMyProfileBinding.profileImage.getContext())
                .load(android.R.drawable.sym_def_app_icon) //TODO
                .into(fragmentMyProfileBinding.profileImage);

    }
}