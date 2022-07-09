package com.example.parti.ui.main;

import android.os.Bundle;
import android.view.View;

import com.example.parti.Parti;
import com.example.parti.databinding.ActivityViewProjectBinding;
import com.example.parti.wrappers.Project;

import androidx.appcompat.app.AppCompatActivity;

public class ViewProjectActivity extends AppCompatActivity {

    private ActivityViewProjectBinding activityViewProjectBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityViewProjectBinding = ActivityViewProjectBinding.inflate(getLayoutInflater());
        setContentView(activityViewProjectBinding.getRoot());


        activityViewProjectBinding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        Project project = (Project) extras.get("project");
        if (project.getAdmin().equals(((Parti) getApplication()).getLoggedInUser().getUuid())) {
            activityViewProjectBinding.buttonEdit.setVisibility(View.VISIBLE);
        } else {
            activityViewProjectBinding.buttonEdit.setVisibility(View.INVISIBLE);
        }

        activityViewProjectBinding.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });
    }
}