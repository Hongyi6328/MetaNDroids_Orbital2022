package com.example.parti.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.parti.Parti;
import com.example.parti.databinding.ActivityEditProjectBinding;
import com.example.parti.databinding.ActivityViewProjectBinding;
import com.example.parti.wrappers.ProjectType;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditProjectActivity extends AppCompatActivity {

    public enum Purpose {UPDATE, CREATE}

    private static final int PICK_IMAGE = 1010;
    private static final String PROJECT_COLLECTION_PATH = Parti.PROJECT_COLLECTION_PATH;
    private static final ProjectType[] PROJECT_TYPES = Parti.PROJECT_TYPES;

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

        activityEditProjectBinding.projectImageBig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE); //TODO
            }
        });
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
                if (!validateInput()) return;
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                DocumentReference projectCollection = firebaseFirestore.collection(PROJECT_COLLECTION_PATH).document();

                String projectId = projectCollection.getId();
                String projectName = activityEditProjectBinding.projectTitleBig.getText().toString();
                ProjectType projectType = PROJECT_TYPES[activityEditProjectBinding.projectType.getSelectedItemPosition()];

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) { //pick an image from local gallery or remote resources and show it
            if (resultCode != Activity.RESULT_OK || data == null) {
                Toast.makeText(this, "Failed to Pick Image", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                activityEditProjectBinding.projectImageBig.setImageBitmap(selectedImage);
            } catch (FileNotFoundException ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    public boolean validateInput() {
        if (activityEditProjectBinding.projectTitleBig.getText().toString().isEmpty()) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: Empty title", Toast.LENGTH_LONG).show();
            return false;
        }
        if (activityEditProjectBinding.projectDescription.getText().toString().isEmpty()) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: Empty description", Toast.LENGTH_LONG).show();
            return false;
        }
        if (activityEditProjectBinding.numberOfParticipants.getText().toString().isEmpty()) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: Empty number of participants needed", Toast.LENGTH_LONG).show();
            return false;
        }
        if (activityEditProjectBinding.ppPerParticipant.getText().toString().isEmpty()) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: Empty PPs for each participant", Toast.LENGTH_LONG).show();
            return false;
        }

        int numParticipants = Integer.parseInt(activityEditProjectBinding.numberOfParticipants.getText().toString());
        if (numParticipants <= 0) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: Non-positive number of participants needed", Toast.LENGTH_LONG).show();
            return false;
        }
        double ppPerParticipant = Double.parseDouble(activityEditProjectBinding.ppPerParticipant.getText().toString());
        if (ppPerParticipant < 0) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: Negative PPs for each participant", Toast.LENGTH_LONG).show();
            return false;
        }

        double currentPPs = ((Parti) this.getApplication()).getLoggedInUser().getParticipationPoints();
        if (Parti.calculatePPCost(numParticipants, ppPerParticipant) > currentPPs) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: You have insufficient PPs to launch the project", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}