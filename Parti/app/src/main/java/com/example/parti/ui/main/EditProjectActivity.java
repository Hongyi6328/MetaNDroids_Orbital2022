package com.example.parti.ui.main;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.parti.Parti;
import com.example.parti.databinding.ActivityEditProjectBinding;
import com.example.parti.databinding.ActivityViewProjectBinding;

import java.util.Locale;

public class EditProjectActivity extends AppCompatActivity {

    public enum Purpose {UPDATE, CREATE}

    private ActivityEditProjectBinding activityEditProjectBinding;
    private Purpose purpose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityEditProjectBinding = ActivityEditProjectBinding.inflate(getLayoutInflater());
        setContentView(activityEditProjectBinding.getRoot());
        activityEditProjectBinding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        purpose = Purpose.CREATE;
        if (extras != null) purpose = (Purpose) extras.get("purpose");
        if (purpose == Purpose.CREATE) setCreatePurpose();
        else if (purpose == Purpose.UPDATE) setUpdatePurpose();
    }

    protected void setCreatePurpose() {
        Glide.with(activityEditProjectBinding.projectImageBig.getContext())
                .load(android.R.drawable.ic_dialog_info)
                .into(activityEditProjectBinding.projectImageBig);

        activityEditProjectBinding.projectTitleBig.setText("");
        activityEditProjectBinding.projectTitleBig.setHint("The title of your project.");

        activityEditProjectBinding.projectDescription.setText("");
        activityEditProjectBinding.projectDescription.setHint(
                "Provide a description of your project. You may talk about what your project is and how people can participate in.");

        activityEditProjectBinding.numberOfParticipants.setText("100");
        activityEditProjectBinding.ppPerParticipant.setHint("100");
        double cost = Parti.calculatePPCost(100, 100);
        double currentPPs = ((Parti) this.getApplication()).getLoggedInUser().getParticipationPoints();
        String hint = String.format(Locale.ENGLISH, "A total of %.3f PPs needed\nYou currently have: %.3f", cost, currentPPs);
        activityEditProjectBinding.totalPp.setText(hint);

        activityEditProjectBinding.switchEnded.setChecked(false);

        activityEditProjectBinding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }

    protected void setUpdatePurpose() {
        activityEditProjectBinding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}