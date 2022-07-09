package com.example.parti.ui.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.parti.Parti;
import com.example.parti.databinding.ActivityViewProjectBinding;
import com.example.parti.wrappers.Project;
import com.example.parti.wrappers.ProjectType;
import com.example.parti.wrappers.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ViewProjectActivity extends AppCompatActivity {

    private ActivityViewProjectBinding activityViewProjectBinding;
    private Project project;
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityViewProjectBinding = ActivityViewProjectBinding.inflate(getLayoutInflater());
        setContentView(activityViewProjectBinding.getRoot());

        firebaseStorage = FirebaseStorage.getInstance();

        activityViewProjectBinding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        project = (Project) extras.get("project");
        if (project.getAdmin().equals(((Parti) getApplication()).getLoggedInUser().getUuid())) {
            activityViewProjectBinding.buttonEdit.setVisibility(View.VISIBLE);
            //TODO Set the visibility of Verification Code Section
        } else {
            activityViewProjectBinding.buttonEdit.setVisibility(View.INVISIBLE);
        }

        activityViewProjectBinding.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProjectActivity.this, EditProjectActivity.class);
                intent.putExtra("project", project);
            }
        });
    }

    //TODO: Update view after editing
    @Override
    public void onResume() {
        super.onResume();

        //Download image
        StorageReference imageReference = firebaseStorage.getReference().child(project.getImageId());
        final long ONE_MEGABYTE = 1024 * 1024;
        imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                activityViewProjectBinding.projectImageBig.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ViewProjectActivity.this, "Failed to download project image", Toast.LENGTH_LONG).show();
                //If failed, load the default local image;
                Glide.with(activityViewProjectBinding.projectImageBig.getContext())
                        .load(android.R.drawable.ic_dialog_info)
                        .into(activityViewProjectBinding.projectImageBig);
            }
        });

        //Set the displayed values
        activityViewProjectBinding.projectTitleBig.setText(project.getName());
        ProjectType type = project.getProjectType();
        int index = 0;
        for (; index < Parti.PROJECT_TYPES.length; index++) if (Parti.PROJECT_TYPES[index] == type) break;
        activityViewProjectBinding.projectType.setSelection(index);
        activityViewProjectBinding.projectProgressBar.setMax(project.getNumParticipantsNeeded());
        activityViewProjectBinding.projectProgressBar.setProgress(project.getNumParticipants());
        String progress = project.getNumParticipants() + "/" + project.getNumParticipantsNeeded() + " Participated";
        activityViewProjectBinding.projectProgressText.setText(progress);
        activityViewProjectBinding.projectDescription.setText(project.getDescription());
        int numPeopleRated = project.getComments().size();
        float rating = ((float) 0) * project.getTotalRating() / numPeopleRated;
        activityViewProjectBinding.projectRating.setRating(rating);
        String ratingDetail = String.format(Locale.CANADA, "Average Rating: %.1f\n%d People Rated", rating, numPeopleRated);
        activityViewProjectBinding.projectRatingDetails.setText(ratingDetail);
        int participationPointsEarned = 0;
        User user = ((Parti) getApplication()).getLoggedInUser();
        if (user.getProjectParticipated().contains(project.getProjectId()))
            participationPointsEarned = user.getParticipationPointsEarned().getOrDefault(project.getProjectId(), 0);
        
    }
}