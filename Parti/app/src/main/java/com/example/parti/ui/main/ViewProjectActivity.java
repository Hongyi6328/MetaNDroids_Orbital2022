package com.example.parti.ui.main;

import android.os.Bundle;
import android.view.View;

import com.example.parti.databinding.ActivityViewProjectBinding;

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

        activityViewProjectBinding.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });
    }
}