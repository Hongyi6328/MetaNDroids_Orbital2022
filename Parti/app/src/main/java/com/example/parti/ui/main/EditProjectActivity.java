package com.example.parti.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.parti.Parti;
import com.example.parti.databinding.ActivityEditProjectBinding;
import com.example.parti.wrappers.Email;
import com.example.parti.wrappers.Project;
import com.example.parti.wrappers.ProjectType;
import com.example.parti.wrappers.User;
import com.example.parti.wrappers.VerificationCodeBundle;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditProjectActivity extends AppCompatActivity {

    public enum Purpose {UPDATE, CREATE}

    public static final String PURPOSE = "purpose";
    public static final int EDIT_PROJECT_RESULT_CODE = 1026;

    private static final ProjectType[] PROJECT_TYPES = Parti.PROJECT_TYPES;

    private ActivityEditProjectBinding activityEditProjectBinding;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private Purpose purpose;
    private Project project;
    private User user;
    private VerificationCodeBundle verificationCodeBundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityEditProjectBinding = ActivityEditProjectBinding.inflate(getLayoutInflater());
        setContentView(activityEditProjectBinding.getRoot());
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        Bundle extras = getIntent().getExtras();
        purpose = Purpose.CREATE;
        if (extras != null) purpose = (Purpose) extras.get(PURPOSE);
        user = ((Parti) getApplication()).getLoggedInUser();
        initialise();
        returnUpdatedProject(RESULT_CANCELED);

        activityEditProjectBinding.imageEditProject.setOnClickListener(v -> {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

            startActivityForResult(chooserIntent, Parti.PICK_IMAGE_REQUEST_CODE); //TODO use the updated version
        });

        activityEditProjectBinding.inputEditProjectNumOfActionsNeeded.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) displayPpEstimate();
        });
        activityEditProjectBinding.inputEditProjectNumOfActionsNeeded.setOnKeyListener((v, keyCode, event) -> {
            displayPpEstimate();
            return false;
        });
        activityEditProjectBinding.inputEditProjectNumOfActionsNeeded.setOnEditorActionListener((v, actionId, event) -> {
            displayPpEstimate();
            return false;
        });

        activityEditProjectBinding.inputEditProjectPpPerAction.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) displayPpEstimate();
        });
        activityEditProjectBinding.inputEditProjectPpPerAction.setOnKeyListener((v, keyCode, event) -> {
            displayPpEstimate();
            return false;
        });
        activityEditProjectBinding.inputEditProjectPpPerAction.setOnEditorActionListener((v, actionId, event) -> {
            displayPpEstimate();
            return false;
        });

        activityEditProjectBinding.buttonEditProjectBack.setOnClickListener(v -> finish());

        activityEditProjectBinding.buttonEditProjectSubmit.setOnClickListener(v -> {
            if (!validateInput()) return;
            DocumentReference projectDocument = firebaseFirestore.collection(Parti.PROJECT_COLLECTION_PATH).document();

            String projectId = project == null
                    ? projectDocument.getId()
                    : project.getProjectId();
            String projectName = activityEditProjectBinding.inputEditProjectTitle.getText().toString();
            ProjectType projectType = PROJECT_TYPES[activityEditProjectBinding.spinnerEditProjectType.getSelectedItemPosition()];

            String admin = user.getUuid();
            List<String> developers = List.of(admin);
            List<String> participants = new ArrayList<>();
            int numActions = project == null ? 0 : project.getNumActions();
            int numActionsNeeded = Integer.parseInt(activityEditProjectBinding.inputEditProjectNumOfActionsNeeded.getText().toString());
            int numParticipants = 0;
            int numParticipantsNeeded = 0;
            double ranking = Project.DEFAULT_DYNAMIC_RANKING + Project.DEFAULT_STATIC_RANKING;
            double dynamicRanking = Project.DEFAULT_DYNAMIC_RANKING;
            double staticRanking = Project.DEFAULT_STATIC_RANKING;
            String description = activityEditProjectBinding.intputEditProjectDescription.getText().toString();
            int numComments = 0;
            List<String> comments = new ArrayList<>();
            long totalRating = 0;
            String lastUpdateDate = ZonedDateTime.now().format(Parti.STANDARD_DATE_TIME_FORMAT);
            String imageId = Parti.PROJECT_IMAGE_COLLECTION_PATH + '/' + projectId + ".jpg";
            List<Double> participationPoints = List.of(Double.parseDouble(activityEditProjectBinding.inputEditProjectPpPerAction.getText().toString()));
            staticRanking += participationPoints.get(0);
            ranking += participationPoints.get(0);
            double participationPointsBalance = (numActionsNeeded - numActions) * participationPoints.get(0);
            double oldParticipationPointsBalance = 0;
            double donatedParticipationPoints = 0;
            Map<String, Double> donors = new HashMap<>();
            boolean concluded = numActions == numActionsNeeded;

            if (project == null) {
                project = new Project(
                        projectId,
                        projectName,
                        projectType,
                        concluded,
                        admin,
                        developers,
                        participants,
                        numActions,
                        numActionsNeeded,
                        numParticipants,
                        numParticipantsNeeded,
                        ranking,
                        dynamicRanking,
                        staticRanking,
                        description,
                        numComments,
                        comments,
                        totalRating,
                        lastUpdateDate,
                        imageId,
                        participationPoints,
                        participationPointsBalance,
                        donatedParticipationPoints,
                        donors);
                user.addProjectPosted(project);

                verificationCodeBundle = new VerificationCodeBundle(projectId);
                verificationCodeBundle.adjustList(numActionsNeeded, participationPoints.get(0));

            } else {
                project.setProjectName(projectName);
                project.setProjectType(projectType);
                project.setConcluded(concluded);
                project.setNumActionsNeeded(numActionsNeeded);
                project.setDescription(description);
                project.setParticipationPoints(participationPoints);
                oldParticipationPointsBalance = project.getParticipationPointsBalance();
                project.setParticipationPointsBalance(participationPointsBalance);

                verificationCodeBundle.adjustList(numActionsNeeded, participationPoints.get(0));
            }

            double costOffset =
                    Parti.calculatePPRefund(
                            (numActionsNeeded - numActions) * participationPoints.get(0)
                                    - oldParticipationPointsBalance);
            user.increaseParticipationPoints(costOffset);

            updateUpdatables(imageId);
            returnUpdatedProject(EDIT_PROJECT_RESULT_CODE);
            displayPpEstimate();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        displayPpEstimate();
    }

    private void initialise() {
        if (purpose == Purpose.CREATE) {
            displayNewProject();
        } else {
            project = (Project) getIntent().getExtras().get(Project.CLASS_ID);
            verificationCodeBundle = (VerificationCodeBundle) getIntent().getExtras().get(VerificationCodeBundle.CLASS_ID);

            downloadImage();
            displayExistingProject();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Parti.PICK_IMAGE_REQUEST_CODE) { //pick an image from local gallery or remote resources and show it
            if (resultCode != Activity.RESULT_OK || data == null) {
                Toast.makeText(this, "Failed to pick image.", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                activityEditProjectBinding.imageEditProject.setImageBitmap(selectedImage);
            } catch (FileNotFoundException ex) {
                Toast.makeText(EditProjectActivity.this, "Something went wrong when displaying the image.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void displayNewProject() {
        Glide.with(activityEditProjectBinding.imageEditProject.getContext())
                .load(android.R.drawable.ic_dialog_info)
                .into(activityEditProjectBinding.imageEditProject);

        activityEditProjectBinding.inputEditProjectTitle.setText("");
        activityEditProjectBinding.inputEditProjectTitle.setHint("The title of your project.");

        activityEditProjectBinding.intputEditProjectDescription.setText("");
        activityEditProjectBinding.intputEditProjectDescription.setHint(
                "Provide a description of your project. \nYou may talk about what your project is and how people can participate in.");

        String defaultNumberOfActionsNeeded = "" + Project.DEFAULT_NUM_ACTIONS_NEEDED;
        String defaultPpPerAction = String.format(Locale.ENGLISH, "%.2f", Project.DEFAULT_PP_PER_ACTION);
        activityEditProjectBinding.inputEditProjectNumOfActionsNeeded.setText(defaultNumberOfActionsNeeded);
        activityEditProjectBinding.inputEditProjectPpPerAction.setText(defaultPpPerAction);
    }

    private void downloadImage() {
        //Download image
        StorageReference imageReference = firebaseStorage.getReference().child(project.getImageId());
        final long ONE_MEGABYTE = 1024 * 1024;
        imageReference
                .getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(bytes -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    activityEditProjectBinding.imageEditProject.setImageBitmap(bitmap);
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(EditProjectActivity.this, "Failed to download project image.", Toast.LENGTH_LONG).show();
                    //If failed, load the default local image;
                    Glide.with(activityEditProjectBinding.imageEditProject.getContext())
                            .load(android.R.drawable.ic_dialog_info)
                            .into(activityEditProjectBinding.imageEditProject);
                });
    }

    private void displayExistingProject() {
        // Display project details
        activityEditProjectBinding.inputEditProjectTitle.setText(project.getName());
        activityEditProjectBinding.intputEditProjectDescription.setText(project.getDescription());
        activityEditProjectBinding.inputEditProjectNumOfActionsNeeded.setText(String.valueOf(project.getNumActionsNeeded()));
        activityEditProjectBinding.inputEditProjectPpPerAction.setText(String.format(Locale.ENGLISH, "%.2f", project.getParticipationPoints().get(0)));
    }

    private boolean validateInput() {
        displayPpEstimate();

        if (activityEditProjectBinding.inputEditProjectTitle.getText().toString().length() > Project.TITLE_LENGTH) {
            Toast.makeText(this, "Failed to submit: \nThe length of the project title should not be longer than " + Project.TITLE_LENGTH + " characters.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (activityEditProjectBinding.inputEditProjectTitle.getText().toString().isEmpty()) {
            Toast.makeText(this, "Failed to submit: \nEmpty title.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (activityEditProjectBinding.intputEditProjectDescription.getText().toString().isEmpty()) {
            Toast.makeText(this, "Failed to submit: \nEmpty description.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (activityEditProjectBinding.inputEditProjectNumOfActionsNeeded.getText().toString().isEmpty()) {
            Toast.makeText(this, "Failed to submit: \nEmpty number of actions that are needed.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (activityEditProjectBinding.inputEditProjectPpPerAction.getText().toString().isEmpty()) {
            Toast.makeText(this, "Failed to submit: \nEmpty PPs for every action.", Toast.LENGTH_LONG).show();
            return false;
        }

        int numActionsNeeded = Integer.parseInt(activityEditProjectBinding.inputEditProjectNumOfActionsNeeded.getText().toString());
        if (numActionsNeeded <= 0) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: \nNon-positive number of actions that are needed.", Toast.LENGTH_LONG).show();
            return false;
        }
        double ppPerAction = Double.parseDouble(activityEditProjectBinding.inputEditProjectPpPerAction.getText().toString());
        if (ppPerAction < 0) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: \nNegative PPs for each action.", Toast.LENGTH_LONG).show();
            return false;
        }

        double currentPPs = user.getParticipationPoints();
        int numActions = 0;
        double participationPointsBalance = 0;
        if (project != null) {
            numActions = project.getNumActions();
            participationPointsBalance = project.getParticipationPointsBalance();
        }
        if (Parti.calculatePPCost(numActionsNeeded - numActions, ppPerAction, participationPointsBalance) > currentPPs) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: \nYou have insufficient PPs to launch the project.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (project != null && project.getNumActions() > Integer.parseInt(activityEditProjectBinding.inputEditProjectNumOfActionsNeeded.getText().toString())) {
            Toast.makeText(EditProjectActivity.this, "Failed to submit: \nThe number of actions needed cannot be smaller than the actual number of actions done.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private StorageTask<UploadTask.TaskSnapshot> uploadImage(String imageId) {
        //upload image
        activityEditProjectBinding.imageEditProject.setDrawingCacheEnabled(true);
        activityEditProjectBinding.imageEditProject.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) activityEditProjectBinding.imageEditProject.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream); //TODO crop and resize
        byte[] data = byteArrayOutputStream.toByteArray();
        UploadTask uploadTask = firebaseStorage.getReference().child(imageId).putBytes(data);
        return uploadTask
                .addOnFailureListener(exception -> {
                    Toast.makeText(EditProjectActivity.this, "Something went wrong when uploading image.", Toast.LENGTH_LONG).show();
                });
    }

    private Task<Void> updateProject(Project project) {
        String projectId = project.getProjectId();
        return firebaseFirestore.collection(Parti.PROJECT_COLLECTION_PATH).document(projectId).set(project)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful())
                        Toast.makeText(EditProjectActivity.this, "Something went wrong when uploading the project.", Toast.LENGTH_LONG).show();
                });
    }

    private Task<Void> updateUser(User user) {
        return firebaseFirestore.collection(Parti.USER_COLLECTION_PATH).document(user.getUuid()).set(user)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful())
                        Toast.makeText(EditProjectActivity.this, "Failed to modify user PP balance.", Toast.LENGTH_LONG).show();
                });
    }

    private Task<Void> updateVerificationCodeBundle(String projectId) {
        DocumentReference documentReference = firebaseFirestore.collection(Parti.VERIFICATION_CODE_OBJECT_COLLECTION_PATH).document(projectId);
        return documentReference.set(verificationCodeBundle).addOnCompleteListener(task -> {
            if (!task.isSuccessful())
                Toast.makeText(EditProjectActivity.this, "Failed to verification code bundle.", Toast.LENGTH_LONG).show();
        });
    }

    private Task<DocumentReference> sendVerificationCodeBundleEmail(String emailAddress, String projectName) {
        CollectionReference collectionReference = firebaseFirestore.collection(Parti.EMAIL_COLLECTION_PATH);
        Email email = verificationCodeBundle.composeEmail(emailAddress, projectName);
        return collectionReference.add(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditProjectActivity.this, "Sent verification code list of your project to your email. \nPlease also check your junk mail box.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(EditProjectActivity.this, "Failed to send verification code list. \nPlease click on edit again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayPpEstimate() {
        int numActionsNeeded = 0;
        double ppPerAction = 0;
        try {
            numActionsNeeded = Integer.parseInt(activityEditProjectBinding.inputEditProjectNumOfActionsNeeded.getText().toString());
            ppPerAction = Double.parseDouble(activityEditProjectBinding.inputEditProjectPpPerAction.getText().toString());
        } catch (Exception ex) {
        }

        double balance = 0;
        if (project != null) {
            numActionsNeeded -= project.getNumActions();
            balance = project.getParticipationPointsBalance();
        }
        double cost = Parti.calculatePPCost(numActionsNeeded, ppPerAction, balance);
        double currentPPs = user.getParticipationPoints();
        String hint = String.format(Locale.ENGLISH, "A total of %.2f PPs needed\nYou currently have: %.2f", cost, currentPPs);
        activityEditProjectBinding.inputEditProjectPpEstimate.setText(hint);
    }

    private void updateUpdatables(String imageId) {
        Task<Void> taskUploadProject = updateProject(project);
        StorageTask<UploadTask.TaskSnapshot> taskUploadImage = uploadImage(imageId);
        Task<Void> taskUpdateUser = updateUser(user);
        Task<Void> taskUploadVerificationCodeBundle = updateVerificationCodeBundle(project.getProjectId());
        Task<DocumentReference> taskSendVerificationCodeBundleEmail = sendVerificationCodeBundleEmail(user.getEmail(), project.getName());
        Tasks.whenAll(
                        taskUploadProject,
                        taskUploadImage,
                        taskUpdateUser,
                        taskUploadVerificationCodeBundle,
                        taskSendVerificationCodeBundleEmail
                )
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        purpose = Purpose.UPDATE;
                        Toast.makeText(
                                        EditProjectActivity.this,
                                        "Fully uploaded project, image, user, and verification code bundle.",
                                        Toast.LENGTH_LONG
                                )
                                .show();
                    } else {
                        Toast.makeText(
                                        EditProjectActivity.this,
                                        "Something went wrong when uploading project, image, user, and verification code bundle.",
                                        Toast.LENGTH_LONG
                                )
                                .show();
                    }
                });
    }

    private void returnUpdatedProject(int result) {
        Intent data = new Intent();
        data.putExtra(Project.CLASS_ID, project);
        setResult(result, data);
    }
}