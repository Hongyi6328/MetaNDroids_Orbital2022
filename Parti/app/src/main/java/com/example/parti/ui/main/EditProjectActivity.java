package com.example.parti.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.parti.Parti;
import com.example.parti.databinding.ActivityEditProjectBinding;
import com.example.parti.wrappers.Project;
import com.example.parti.wrappers.ProjectType;
import com.example.parti.wrappers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditProjectActivity extends AppCompatActivity {

    public enum Purpose {UPDATE, CREATE}

    private static final String PROJECT_COLLECTION_PATH = Parti.PROJECT_COLLECTION_PATH;
    private static final ProjectType[] PROJECT_TYPES = Parti.PROJECT_TYPES;

    private ActivityEditProjectBinding activityEditProjectBinding;
    private Purpose purpose;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private Project project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityEditProjectBinding = ActivityEditProjectBinding.inflate(getLayoutInflater());
        setContentView(activityEditProjectBinding.getRoot());
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        activityEditProjectBinding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        purpose = Purpose.CREATE;
        if (extras != null) purpose = (Purpose) extras.get("purpose");
        //if (purpose == Purpose.CREATE)
        initialize();
        //else if (purpose == Purpose.UPDATE) setUpdatePurpose();

        activityEditProjectBinding.projectImageBig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, Parti.PICK_IMAGE_REQUEST_CODE); //TODO
            }
        });
    }

    protected void initialize() {
        if (purpose == Purpose.CREATE) {

            Glide.with(activityEditProjectBinding.projectImageBig.getContext())
                    .load(android.R.drawable.ic_dialog_info)
                    .into(activityEditProjectBinding.projectImageBig);

            activityEditProjectBinding.projectTitleBig.setText("");
            activityEditProjectBinding.projectTitleBig.setHint("The title of your project.");

            activityEditProjectBinding.projectDescription.setText("");
            activityEditProjectBinding.projectDescription.setHint(
                    "Provide a description of your project. You may talk about what your project is and how people can participate in.");

            String defaultNumberOfParticipants = "100";
            String defaultPpPerParticipant = "100";
            activityEditProjectBinding.numberOfParticipants.setText(defaultNumberOfParticipants);
            activityEditProjectBinding.ppPerParticipant.setText(defaultPpPerParticipant);
            double cost = Parti.calculatePPCost(100, 100);
            double currentPPs = ((Parti) this.getApplication()).getLoggedInUser().getParticipationPoints();
            String hint = String.format(Locale.ENGLISH, "A total of %.3f PPs needed\nYou currently have: %.3f", cost, currentPPs);
            activityEditProjectBinding.totalPp.setText(hint);

            activityEditProjectBinding.switchEnded.setChecked(false);
        } else {
            project = (Project) getIntent().getExtras().get("project");

            //Download image
            StorageReference imageReference = firebaseStorage.getReference().child(project.getImageId());
            final long ONE_MEGABYTE = 1024 * 1024;
            imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    activityEditProjectBinding.projectImageBig.setImageBitmap(bmp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(EditProjectActivity.this, "Failed to download project image", Toast.LENGTH_LONG).show();
                    //If failed, load the default local image;
                    Glide.with(activityEditProjectBinding.projectImageBig.getContext())
                            .load(android.R.drawable.ic_dialog_info)
                            .into(activityEditProjectBinding.projectImageBig);
                }
            });

            // Display project details
            activityEditProjectBinding.projectTitleBig.setText(project.getName());
            activityEditProjectBinding.projectDescription.setText(project.getDescription());
            activityEditProjectBinding.numberOfParticipants.setText(String.valueOf(project.getNumParticipants()));
            activityEditProjectBinding.ppPerParticipant.setText(String.format(Locale.ENGLISH, "%.2f", project.getParticipationPoints().get(0)));

            //TODO Calculate PP Balance of a project and validate
            //TODO Set a limit on the number of decimal places of PPs
            double cost = Parti.calculatePPCost(100, 100);
            double currentPPs = ((Parti) this.getApplication()).getLoggedInUser().getParticipationPoints();
            String hint = String.format(Locale.ENGLISH, "A total of %.2f PPs needed\nYou currently have: %.2f", cost, currentPPs);
            activityEditProjectBinding.totalPp.setText(hint);

            activityEditProjectBinding.switchEnded.setChecked(false);
        }

        activityEditProjectBinding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateInput()) return;
                DocumentReference projectCollection = firebaseFirestore.collection(PROJECT_COLLECTION_PATH).document();

                String projectId = project == null
                        ? projectCollection.getId()
                        : project.getProjectId();
                String projectName = activityEditProjectBinding.projectTitleBig.getText().toString();
                ProjectType projectType = PROJECT_TYPES[activityEditProjectBinding.projectType.getSelectedItemPosition()];
                boolean concluded = activityEditProjectBinding.switchEnded.isChecked();
                String admin = ((Parti) EditProjectActivity.this.getApplication()).getLoggedInUser().getUuid();
                List<String> developers = List.of(admin);
                List<String> participants = new ArrayList<>();
                int numParticipants = 0;
                int numParticipantsNeeded = Integer.parseInt(activityEditProjectBinding.numberOfParticipants.getText().toString());
                double ranking = Parti.DEFAULT_RANKING;
                String description = activityEditProjectBinding.projectDescription.getText().toString();
                List<String> comments = new ArrayList<>();
                long totalRating = 0;
                String launchDate = LocalDateTime.now().toString();
                String imageId = Parti.PROJECT_IMAGE_COLLECTION_PATH + '/' + projectId + ".jpg";
                List<Double> participationPoints = List.of(Double.parseDouble(activityEditProjectBinding.ppPerParticipant.getText().toString()));
                double participationPointsBalance = numParticipantsNeeded * participationPoints.get(0);

                int oldNumParticipants = 0;
                double oldParticipationPoints = 0;

                if (project == null) {
                    project = new Project(
                            projectId,
                            projectName,
                            projectType,
                            concluded,
                            admin,
                            developers,
                            participants,
                            numParticipants,
                            numParticipantsNeeded,
                            ranking,
                            description,
                            comments,
                            totalRating,
                            launchDate,
                            imageId,
                            participationPoints,
                            participationPointsBalance);
                } else {
                    project.setProjectName(projectName);
                    project.setProjectType(projectType);
                    project.setConcluded(concluded);
                    project.setNumParticipantsNeeded(numParticipantsNeeded);
                    project.setDescription(description);
                    project.setLaunchDate(launchDate);
                    project.setParticipationPoints(participationPoints);

                    oldNumParticipants = project.getNumParticipants();
                    oldParticipationPoints = project.getParticipationPoints().get(0);
                    project.increaseParticipationPointsBalance(old);
                }

                double costOffset =
                        Parti.calculatePPRefund(
                                numParticipantsNeeded * participationPoints.get(0)
                                        - oldNumParticipants * oldParticipationPoints);

                uploadProject(project);
                uploadImage(imageId);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Parti.PICK_IMAGE_REQUEST_CODE) { //pick an image from local gallery or remote resources and show it
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
        if (Parti.calculatePPCost(numParticipants, ppPerParticipant) - project.getParticipationPointsBalance() > currentPPs) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: You have insufficient PPs to launch the project", Toast.LENGTH_LONG).show();
            return false;
        }

        if (project != null && project.getNumParticipants() > Integer.parseInt(activityEditProjectBinding.numberOfParticipants.getText().toString())) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: The number of participants needed cannot be smaller than the actual number of participants", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    protected void uploadImage(String imageId) {
        //upload image
        activityEditProjectBinding.projectImageBig.setDrawingCacheEnabled(true);
        activityEditProjectBinding.projectImageBig.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) activityEditProjectBinding.projectImageBig.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream); //TODO
        byte[] data = byteArrayOutputStream.toByteArray();
        UploadTask uploadTask = firebaseStorage.getReference().child(imageId).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(EditProjectActivity.this, "Something went wrong when uploading image", Toast.LENGTH_LONG).show();
                //purpose = Purpose.CREATE; TODO
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Toast.makeText(EditProjectActivity.this, "Image uploaded successfully", Toast.LENGTH_LONG).show();
                //purpose = Purpose.UPDATE; TODO
            }
        });
    }

    protected void uploadProject(Project project) {
        String projectId = project.getProjectId();
        firebaseFirestore.collection(Parti.PROJECT_COLLECTION_PATH).document(projectId).set(project).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProjectActivity.this, "Created a new project", Toast.LENGTH_LONG).show();

                    User user = ((Parti) getApplication()).getLoggedInUser();



                    purpose = Purpose.UPDATE;
                }
                else {
                    Toast.makeText(EditProjectActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    if (purpose == Purpose.CREATE) purpose = Purpose.CREATE;
                }
            }
        });
    }
}