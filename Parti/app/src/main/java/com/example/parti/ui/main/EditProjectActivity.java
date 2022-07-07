package com.example.parti.ui.main;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parti.databinding.ActivityViewProjectBinding;

public class EditProjectActivity extends AppCompatActivity {

    public enum Purpose {UPDATE, CREATE}

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
        Purpose purpose = Purpose.CREATE;
        if (extras != null) purpose = (Purpose) extras.get("purpose");
        if (purpose == Purpose.CREATE) {
            
        }
    }
}