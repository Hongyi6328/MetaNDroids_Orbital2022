package com.example.parti.ui.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.parti.Parti;
import com.example.parti.databinding.ActivityViewProjectBinding;
import com.example.parti.recyclerview.BrowseProjectsAdapter;
import com.example.parti.recyclerview.CommentAdapter;
import com.example.parti.wrappers.Project;
import com.example.parti.wrappers.ProjectType;
import com.example.parti.wrappers.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

public class ViewProjectActivity extends AppCompatActivity implements CommentAdapter.OnCommentSelectedListener {

    private ActivityViewProjectBinding activityViewProjectBinding;
    private Project project;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private CommentAdapter commentAdapter;
    private User user;
    private Query query;

    @Override
    public void onCommentSelected(DocumentSnapshot comment) {

    }

    /*
    private static final int PARTICIPATION_STATUS_ADMIN = 0;
    private static final int PARTICIPATION_STATUS_UNKNOWN = 1;
    private static final int PARTICIPATION_STATUS_NOT_PARTICIPATED = 2;
    private static final int PARTICIPATION_STATUS_PARTICIPATED = 3;
    private static final int PARTICIPATION_STATUS_COMMENTED = 4;
     */

    private enum ParticipationStatus {
        DEFAULT,
        UNKNOWN,
        ADMIN,
        NOT_PARTICIPATED,
        PARTICIPATED,
        COMMENTED,
    }

    private ParticipationStatus participationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityViewProjectBinding = ActivityViewProjectBinding.inflate(getLayoutInflater());
        setContentView(activityViewProjectBinding.getRoot());

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        participationStatus = ParticipationStatus.DEFAULT;

        Bundle extras = getIntent().getExtras();
        project = (Project) extras.get("project");
        user = ((Parti) getApplication()).getLoggedInUser();
        checkParticipationStatus();
        setUpComments();

        activityViewProjectBinding.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProjectActivity.this, EditProjectActivity.class);
                intent.putExtra("project", project);
                startActivity(intent);
            }
        });

        activityViewProjectBinding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        float rating = 0;
        int numPeopleRated = project.getNumComments();
        if (numPeopleRated != 0) rating = ((float) project.getTotalRating()) / numPeopleRated;
        String ratingDetail = String.format(Locale.CANADA, "Average Rating: %.1f\n%d People Rated", rating, numPeopleRated);
        activityViewProjectBinding.projectRating.setRating(rating);
        activityViewProjectBinding.projectRatingDetails.setText(ratingDetail);
        double participationPointsEarned = 0;
        User user = ((Parti) getApplication()).getLoggedInUser();
        if (user.getProjectsParticipated().contains(project.getProjectId()))
            participationPointsEarned = user.getParticipationPointsEarned().getOrDefault(project.getProjectId(), 0.0);
        String ppEarned = String.format(Locale.ENGLISH, "You have earned %.2f PPs from this project", participationPointsEarned);
        activityViewProjectBinding.ppEarned.setText(ppEarned);
    }

    private void onCommentAdded() {

    }

    private void onPpEntered() {

    }

    private void checkParticipationStatus() {
        if (user.getUuid().equals(project.getAdmin()))
            handleParticipationStatus(ParticipationStatus.ADMIN);
        else if (!user.getProjectsParticipated().contains(project.getProjectId()))
            handleParticipationStatus(ParticipationStatus.NOT_PARTICIPATED);
        else if (user.getCommentsPosted().contains(project.getProjectId()))
            handleParticipationStatus(ParticipationStatus.COMMENTED);
        else handleParticipationStatus(ParticipationStatus.PARTICIPATED);
    }

    private void handleParticipationStatus(ParticipationStatus newStatus) {
        if (participationStatus == newStatus) return;

        switch (newStatus) {
            case ADMIN:
                activityViewProjectBinding.buttonEdit.setVisibility(View.VISIBLE);
                activityViewProjectBinding.constraintLayoutVerificationCode.setVisibility(View.GONE);
                activityViewProjectBinding.constraintLayoutAddComment.setVisibility(View.GONE);
                break;
            case NOT_PARTICIPATED:
                activityViewProjectBinding.buttonEdit.setVisibility(View.INVISIBLE);
                activityViewProjectBinding.constraintLayoutVerificationCode.setVisibility(View.VISIBLE);
                activityViewProjectBinding.constraintLayoutAddComment.setVisibility(View.GONE);
                break;
            case PARTICIPATED:
                activityViewProjectBinding.buttonEdit.setVisibility(View.INVISIBLE);
                activityViewProjectBinding.constraintLayoutVerificationCode.setVisibility(View.VISIBLE);
                activityViewProjectBinding.constraintLayoutAddComment.setVisibility(View.VISIBLE);
                String buttonCommentText = "Comment";
                activityViewProjectBinding.buttonComment.setText(buttonCommentText);
                break;
            case COMMENTED:
                activityViewProjectBinding.buttonEdit.setVisibility(View.INVISIBLE);
                activityViewProjectBinding.constraintLayoutVerificationCode.setVisibility(View.VISIBLE);
                activityViewProjectBinding.constraintLayoutAddComment.setVisibility(View.VISIBLE);
                buttonCommentText = "Update";
                activityViewProjectBinding.buttonComment.setText(buttonCommentText);
                break;
            default:
                break;
        }
        participationStatus = newStatus;
    }

    private void setUpComments() {
        query = firebaseFirestore.collection(Parti.COMMENT_COLLECTION_PATH).document(project.getProjectId()).collection(Parti.COMMENT_SUBCOLLECTION_PATH);
        commentAdapter = new CommentAdapter(query, this);
        activityViewProjectBinding.projectCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityViewProjectBinding.projectCommentsRecyclerView.setAdapter(commentAdapter);
    }
}