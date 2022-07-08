package com.example.parti.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.parti.Parti;
import com.example.parti.databinding.FragmentMyProfileBinding;
import com.example.parti.ui.login.LoginActivity;
import com.example.parti.wrappers.Major;
import com.example.parti.wrappers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MyProfileFragment extends Fragment {

    FragmentMyProfileBinding fragmentMyProfileBinding;
    HashMap<String, Integer> majorMap = new HashMap<>();
    boolean dataRead = false;

    public static final int EARLIEST_YEAR_OF_MATRIC = Parti.EARLIEST_YEAR_OF_MATRIC;
    Major[] majors = Parti.MAJORS;

    public MyProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        int mapSize = majors.length;
        if (majorMap.isEmpty()) for (int i = 0; i < mapSize; i++) majorMap.put(majors[i].toString(), i);

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
                ((Parti) MyProfileFragment.this.getActivity().getApplication()).setLoggedInUser(null);
                Intent loginIntent = new Intent(MyProfileFragment.this.getContext(), LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        fragmentMyProfileBinding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentMyProfileBinding.selfDescription.getText().length() > Parti.MAX_SELF_DESCRIPTION_LENGTH) {
                    Toast.makeText(MyProfileFragment.this.getContext(),"Your description cannot exceed 500 characters",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (fragmentMyProfileBinding.alias.getText().toString().contains(" ")) {
                    Toast.makeText(MyProfileFragment.this.getContext(),"No whitespaces in alias",Toast.LENGTH_SHORT).show();
                }
                User user = ((Parti) getActivity().getApplication()).getLoggedInUser();
                user.setAlias(fragmentMyProfileBinding.alias.getText().toString());
                user.setYearOfMatric(fragmentMyProfileBinding.yearOfMatric.getSelectedItem().toString());
                user.setMajor(majors[fragmentMyProfileBinding.major.getSelectedItemPosition()]);
                user.setSelfDescription(fragmentMyProfileBinding.selfDescription.getText().toString());
                FirebaseFirestore.getInstance().collection(Parti.USER_COLLECTION_PATH).document(user.getUuid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) Toast.makeText(getContext(), "Updated!", Toast.LENGTH_LONG).show();
                        else Toast.makeText(getContext(), "Failed to update!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        return fragmentMyProfileBinding.getRoot();
    }

    public void readData() { // TODO: This method is a bit tedious, can try to replace it with ValueEventListener.onDataChange(DataSnapshot)
        User user = ((Parti) getActivity().getApplication()).getLoggedInUser();
        if (user != null && !dataRead) {

            Glide.with(fragmentMyProfileBinding.profileImage.getContext())
                    .load(android.R.drawable.sym_def_app_icon) //TODO
                    .into(fragmentMyProfileBinding.profileImage);

            String emailString = "Email: " + user.getEmail();
            String participationPointsString = "Participation Points: " + user.getParticipationPoints();
            String userIdString = "User ID: " + user.getUuid();

            fragmentMyProfileBinding.email.setText(emailString);
            fragmentMyProfileBinding.participationPoints.setText(participationPointsString);
            fragmentMyProfileBinding.userId.setText(userIdString);

            //String aliasHint = "Alias: " + user.getAlias();
            //String selfDecriptionHint = "Self Description: " + user.getSelfDescription();

            fragmentMyProfileBinding.alias.setText(user.getAlias());
            fragmentMyProfileBinding.yearOfMatric.setSelection(Integer.parseInt(user.getYearOfMatric()) - EARLIEST_YEAR_OF_MATRIC);
            fragmentMyProfileBinding.major.setSelection(majorMap.get(user.getMajor().toString()));
            fragmentMyProfileBinding.selfDescription.setText(user.getSelfDescription());

            dataRead = true;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            readData();
        }
    }
}