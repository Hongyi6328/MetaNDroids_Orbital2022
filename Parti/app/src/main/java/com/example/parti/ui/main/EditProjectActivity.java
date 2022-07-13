package com.example.parti.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.parti.Parti;
import com.example.parti.databinding.ActivityEditProjectBinding;
import com.example.parti.wrappers.Email;
import com.example.parti.wrappers.Project;
import com.example.parti.wrappers.ProjectType;
import com.example.parti.wrappers.User;
import com.example.parti.wrappers.VerificationCodeBundle;
import com.example.parti.wrappers.VerificationCodeBundleBox;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
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
    private VerificationCodeBundle verificationCodeBundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        activityEditProjectBinding.numberOfParticipantsNeeded.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) updatePpEstimate();
            }
        });
        activityEditProjectBinding.numberOfParticipantsNeeded.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                updatePpEstimate();
                return false;
            }
        });

         /*
        activityEditProjectBinding.numberOfParticipantsNeeded.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                updatePpEstimate();
                return false;
            }
        });
        */

        activityEditProjectBinding.ppPerParticipant.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) updatePpEstimate();
            }
        });
        activityEditProjectBinding.ppPerParticipant.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                updatePpEstimate();
                return false;
            }
        });

        /*
        activityEditProjectBinding.ppPerParticipant.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                updatePpEstimate();
                return false;
            }
        });
        */
    }

    @Override
    public void onStart() {
        super.onStart();
        updatePpEstimate();
    }

    public void initialize() {
        if (purpose == Purpose.CREATE) {

            Glide.with(activityEditProjectBinding.projectImageBig.getContext())
                    .load(android.R.drawable.ic_dialog_info)
                    .into(activityEditProjectBinding.projectImageBig);

            activityEditProjectBinding.projectTitleBig.setText("");
            activityEditProjectBinding.projectTitleBig.setHint("The title of your project.");

            activityEditProjectBinding.projectDescription.setText("");
            activityEditProjectBinding.projectDescription.setHint(
                    "Provide a description of your project. You may talk about what your project is and how people can participate in.");

            String defaultNumberOfParticipantsNeeded = "" + Parti.DEFAULT_NUM_PARTICIPANTS_NEEDED;
            String defaultPpPerParticipant = String.format(Locale.ENGLISH, "%.2f", Parti.DEFAULT_PP_PER_PARTICIPANT);
            activityEditProjectBinding.numberOfParticipantsNeeded.setText(defaultNumberOfParticipantsNeeded);
            activityEditProjectBinding.ppPerParticipant.setText(defaultPpPerParticipant);

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
                    Toast.makeText(EditProjectActivity.this, "Failed to download project image.", Toast.LENGTH_LONG).show();
                    //If failed, load the default local image;
                    Glide.with(activityEditProjectBinding.projectImageBig.getContext())
                            .load(android.R.drawable.ic_dialog_info)
                            .into(activityEditProjectBinding.projectImageBig);
                }
            });

            // Display project details
            activityEditProjectBinding.projectTitleBig.setText(project.getName());
            activityEditProjectBinding.projectDescription.setText(project.getDescription());
            activityEditProjectBinding.numberOfParticipantsNeeded.setText(String.valueOf(project.getNumParticipants()));
            activityEditProjectBinding.ppPerParticipant.setText(String.format(Locale.ENGLISH, "%.2f", project.getParticipationPoints().get(0)));

            //TODO Set a limit on the number of decimal places of PPs

            activityEditProjectBinding.switchEnded.setChecked(project.isConcluded());

            VerificationCodeBundle[] tempVerificationCodeBundleArray = new VerificationCodeBundle[1];
            DocumentReference documentReference = firebaseFirestore.collection(Parti.VERIFICATION_CODE_OBJECT_COLLECTION_PATH).document(project.getProjectId());
            Task<DocumentSnapshot> task = documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    tempVerificationCodeBundleArray[0] = documentSnapshot.toObject(VerificationCodeBundle.class);
                }
            });
            try {
                Tasks.await(task);
            } catch (Exception ex) {
                Log.d("upload-verification-code-bundle", "Failed to upload verification code bundle: " + ex.getMessage());
            }
            verificationCodeBundle = tempVerificationCodeBundleArray[0];
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
                int numParticipantsNeeded = Integer.parseInt(activityEditProjectBinding.numberOfParticipantsNeeded.getText().toString());
                double ranking = Parti.DEFAULT_RANKING;
                String description = activityEditProjectBinding.projectDescription.getText().toString();
                List<String> comments = new ArrayList<>();
                long totalRating = 0;
                String launchDate = LocalDateTime.now().toString();
                String imageId = Parti.PROJECT_IMAGE_COLLECTION_PATH + '/' + projectId + ".jpg";
                List<Double> participationPoints = List.of(Double.parseDouble(activityEditProjectBinding.ppPerParticipant.getText().toString()));
                double participationPointsBalance = (numParticipantsNeeded - numParticipants) * participationPoints.get(0);
                double oldParticipationPointsBalance = 0;

                //int oldNumParticipants = 0;
                //double oldParticipationPoints = 0;

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
                    User user = ((Parti) getApplication()).getLoggedInUser();
                    user.getProjectsPosted().add(projectId);

                    verificationCodeBundle = new VerificationCodeBundle(projectId);
                    verificationCodeBundle.adjustList(numParticipantsNeeded, participationPoints.get(0));

                } else {
                    project.setProjectName(projectName);
                    project.setProjectType(projectType);
                    project.setConcluded(concluded);
                    project.setNumParticipantsNeeded(numParticipantsNeeded);
                    project.setDescription(description);
                    //project.setLaunchDate(launchDate);
                    project.setParticipationPoints(participationPoints);
                    oldParticipationPointsBalance = project.getParticipationPointsBalance();
                    project.setParticipationPointsBalance(participationPointsBalance);

                }

                double costOffset =
                        Parti.calculatePPRefund(
                                (numParticipantsNeeded - numParticipants) * participationPoints.get(0)
                                        - oldParticipationPointsBalance);

                Task<Void> taskUploadProject = uploadProject(project);
                StorageTask<UploadTask.TaskSnapshot> taskUploadImage = uploadImage(imageId);
                User user = ((Parti) getApplication()).getLoggedInUser();
                user.increaseParticipationPoints(costOffset);
                Task<Void> taskUpdateUser = updateUser(user);
                Task<Void> taskUploadVerificationCodeBundle = uploadVerificationCodeBundle(projectId);
                Task<DocumentReference> taskSendVerificationCodeBundleEmail = sendVerificationCodeBundleEmail(user.getEmail(), projectName);
                Tasks.whenAllSuccess(
                        taskUploadProject,
                                taskUploadImage,
                                taskUpdateUser,
                                taskUploadVerificationCodeBundle,
                                taskSendVerificationCodeBundleEmail)
                        .addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                            @Override
                            public void onComplete(@NonNull Task<List<Object>> task) {
                                if (task.isSuccessful()) {
                                    purpose = Purpose.UPDATE;
                                    Toast.makeText(EditProjectActivity.this, "Fully uploaded project, image, user, and verification code bundle.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(EditProjectActivity.this, "Something went wrong when uploading project, image, user, and verification code bundle.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Parti.PICK_IMAGE_REQUEST_CODE) { //pick an image from local gallery or remote resources and show it
            if (resultCode != Activity.RESULT_OK || data == null) {
                Toast.makeText(this, "Failed to Pick Image.", Toast.LENGTH_LONG).show();
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
            Toast.makeText(EditProjectActivity.this, "Failed to submit: Empty title.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (activityEditProjectBinding.projectDescription.getText().toString().isEmpty()) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: Empty description.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (activityEditProjectBinding.numberOfParticipantsNeeded.getText().toString().isEmpty()) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: Empty number of participants needed.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (activityEditProjectBinding.ppPerParticipant.getText().toString().isEmpty()) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: Empty PPs for each participant.", Toast.LENGTH_LONG).show();
            return false;
        }

        int numParticipantsNeeded = Integer.parseInt(activityEditProjectBinding.numberOfParticipantsNeeded.getText().toString());
        if (numParticipantsNeeded <= 0) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: Non-positive number of participants needed.", Toast.LENGTH_LONG).show();
            return false;
        }
        double ppPerParticipant = Double.parseDouble(activityEditProjectBinding.ppPerParticipant.getText().toString());
        if (ppPerParticipant < 0) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: Negative PPs for each participant.", Toast.LENGTH_LONG).show();
            return false;
        }

        double currentPPs = ((Parti) this.getApplication()).getLoggedInUser().getParticipationPoints();
        int numParticipants = 0;
        double participationPointsBalance = 0;
        if (project != null) {
            numParticipants = project.getNumParticipants();
            participationPointsBalance = project.getParticipationPointsBalance();
        }
        if (Parti.calculatePPCost(numParticipantsNeeded - numParticipants, ppPerParticipant, participationPointsBalance) > currentPPs) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: You have insufficient PPs to launch the project.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (project != null && project.getNumParticipants() > Integer.parseInt(activityEditProjectBinding.numberOfParticipantsNeeded.getText().toString())) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: The number of participants needed cannot be smaller than the actual number of participants.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public StorageTask<UploadTask.TaskSnapshot> uploadImage(String imageId) {
        //upload image
        activityEditProjectBinding.projectImageBig.setDrawingCacheEnabled(true);
        activityEditProjectBinding.projectImageBig.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) activityEditProjectBinding.projectImageBig.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream); //TODO
        byte[] data = byteArrayOutputStream.toByteArray();
        UploadTask uploadTask = firebaseStorage.getReference().child(imageId).putBytes(data);
        return uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(EditProjectActivity.this, "Something went wrong when uploading image.", Toast.LENGTH_LONG).show();
                //purpose = Purpose.CREATE;
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Toast.makeText(EditProjectActivity.this, "Image uploaded successfully.", Toast.LENGTH_LONG).show();
                //purpose = Purpose.UPDATE;
            }
        });
    }

    public Task<Void> uploadProject(Project project) {
        String projectId = project.getProjectId();
        return firebaseFirestore.collection(Parti.PROJECT_COLLECTION_PATH).document(projectId).set(project)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProjectActivity.this, "Created a new project.", Toast.LENGTH_LONG).show();
                            //purpose = Purpose.UPDATE;
                        } else {
                            Toast.makeText(EditProjectActivity.this, "Something went wrong when uploading the project.", Toast.LENGTH_LONG).show();
                            //if (purpose == Purpose.CREATE) purpose = Purpose.CREATE;
                        }
                    }
                });
    }

    public Task<Void> updateUser(User user) {
        return firebaseFirestore.collection(Parti.USER_COLLECTION_PATH).document(user.getUuid()).set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProjectActivity.this, "Modified user PP balance successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(EditProjectActivity.this, "Failed to modify user PP balance.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public Task<Void> uploadVerificationCodeBundle(String projectId) {
        DocumentReference documentReference = firebaseFirestore.collection(Parti.VERIFICATION_CODE_OBJECT_COLLECTION_PATH).document(projectId);
        //VerificationCodeBundleBox box = verificationCodeBundle.toVerificationCodeBundleBox();
        return documentReference.set(verificationCodeBundle).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProjectActivity.this, "Updated verification code bundle.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EditProjectActivity.this, "Failed to verification code bundle.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public Task<DocumentReference> sendVerificationCodeBundleEmail(String emailAddress, String projectName) {
        CollectionReference collectionReference = firebaseFirestore.collection(Parti.EMAIL_COLLECTION_PATH);
        Email email = verificationCodeBundle.composeEmail(emailAddress, projectName);
        return collectionReference.add(email).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProjectActivity.this, "Sent verification code list of your project to your email. Please also check your junk mail box.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EditProjectActivity.this, "Failed to send verification code list", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void updatePpEstimate() {
        int numParticipantsNeeded = 0;
        double ppPerParticipant = 0;
        try {
            numParticipantsNeeded = Integer.parseInt(activityEditProjectBinding.numberOfParticipantsNeeded.getText().toString());
            ppPerParticipant = Double.parseDouble(activityEditProjectBinding.ppPerParticipant.getText().toString());
        } catch (Exception ex) {}

        double balance = 0;
        if (project != null) {
            numParticipantsNeeded -= project.getNumParticipants();
            balance = project.getParticipationPointsBalance();
        }
        double cost = Parti.calculatePPCost(numParticipantsNeeded, ppPerParticipant, balance);
        double currentPPs = ((Parti) this.getApplication()).getLoggedInUser().getParticipationPoints();
        String hint = String.format(Locale.ENGLISH, "A total of %.2f PPs needed\nYou currently have: %.2f", cost, currentPPs);
        activityEditProjectBinding.ppEstimate.setText(hint);
    }
}