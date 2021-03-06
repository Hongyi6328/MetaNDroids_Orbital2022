package com.example.parti.ui.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.parti.Parti;
import com.example.parti.adapters.CommentRecyclerAdapter;
import com.example.parti.databinding.ActivityViewProjectBinding;
import com.example.parti.wrappers.Project;
import com.example.parti.wrappers.ProjectComment;
import com.example.parti.wrappers.ProjectType;
import com.example.parti.wrappers.User;
import com.example.parti.wrappers.VerificationCodeBundle;
import com.example.parti.wrappers.util.LinearLayoutManagerWrapper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Map;

public class ViewProjectActivity extends AppCompatActivity {

    private enum ParticipationStatus {
        DEFAULT,
        UNKNOWN,
        ADMIN,
        NOT_PARTICIPATED,
        PARTICIPATED,
        COMMENTED,
    }

    public static final int EDIT_PROJECT_REQUEST_CODE = 1025;

    private ParticipationStatus participationStatus;
    private ActivityViewProjectBinding activityViewProjectBinding;
    private Project project;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private VerificationCodeBundle verificationCodeBundle;
    private User user;
    private Query query;
    private ProjectComment myComment;
    private CommentRecyclerAdapter commentRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityViewProjectBinding = ActivityViewProjectBinding.inflate(getLayoutInflater());
        setContentView(activityViewProjectBinding.getRoot());

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        participationStatus = ParticipationStatus.DEFAULT;
        Bundle extras = getIntent().getExtras();

        project = (Project) extras.get(Project.CLASS_ID);
        user = ((Parti) getApplication()).getLoggedInUser();
        checkParticipationStatus();
        setUpCommentRecyclerView();
        downloadVerificationCodeBundle();

        activityViewProjectBinding.buttonViewProjectEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ViewProjectActivity.this, EditProjectActivity.class);
            intent.putExtra(Project.CLASS_ID, project);
            intent.putExtra(VerificationCodeBundle.CLASS_ID, verificationCodeBundle);
            startActivityForResult(intent, EDIT_PROJECT_REQUEST_CODE);
        });

        activityViewProjectBinding.buttonViewProjectBack.setOnClickListener(v -> finish());

        activityViewProjectBinding.buttonViewProjectSubmitVerificationCode.setOnClickListener(v -> {
            String code = activityViewProjectBinding.inputViewProjectVerificationCode.getText().toString();
            int resultCode = verificationCodeBundle.redeemCode(code, user.getUuid());
            String result = "";
            switch (resultCode) {
                case VerificationCodeBundle.REDEEM_RESULT_CODE_SUCCESS:
                    project.addAction(user);
                    user.participate(project);
                    updateUpdatables();
                    displayPpsEarned();
                    displayProgress();
                    if (participationStatus != ParticipationStatus.COMMENTED) {
                        handleParticipationStatus(ParticipationStatus.PARTICIPATED);
                    }
                    result = "Valid code redeemed.";
                    activityViewProjectBinding.inputViewProjectVerificationCode.setText("");
                    break;
                case VerificationCodeBundle.REDEEM_RESULT_CODE_REDEEMED:
                    result = "This code has been redeemed.";
                    break;
                case VerificationCodeBundle.REDEEM_RESULT_CODE_NOT_REDEEMABLE:
                    result = "This code is not redeemable.";
                    break;
                case VerificationCodeBundle.REDEEM_RESULT_CODE_NOT_FOUND:
                    result = "Code not found.";
                    break;
                default:
                    break;
            }
            Toast.makeText(ViewProjectActivity.this, result, Toast.LENGTH_LONG).show();
        });

        activityViewProjectBinding.buttonViewProjectAddComment.setOnClickListener(v -> {
            String commentBody = activityViewProjectBinding.inputViewProjectAddComment.getText().toString();
            if (commentBody.length() > ProjectComment.COMMENT_BODY_LENGTH) {
                String toast = String.format(Locale.ENGLISH, "The length of comment cannot exceed %d characters.", ProjectComment.COMMENT_BODY_LENGTH);
                Toast.makeText(ViewProjectActivity.this, toast, Toast.LENGTH_LONG).show();
                return;
            }
            int rating = (int) activityViewProjectBinding.ratingBarViewProjectCommentRating.getRating();
            ProjectComment comment = new ProjectComment(user.getUuid(), commentBody, rating, ZonedDateTime.now().format(Parti.STANDARD_DATE_TIME_FORMAT));
            DocumentReference documentReference = firebaseFirestore
                    .collection(Parti.COMMENT_COLLECTION_PATH).document(project.getProjectId())
                    .collection(Parti.COMMENT_SUBCOLLECTION_PATH).document(user.getUuid());
            documentReference.set(comment)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ViewProjectActivity.this, "Uploaded comment.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ViewProjectActivity.this, "Failed to upload comment.", Toast.LENGTH_LONG).show();
                        }
                    })
                    .onSuccessTask(unused -> {
                        user.addComment(project.getProjectId());
                        project.addComment(comment, myComment);
                        displayRating();
                        myComment = comment;
                        handleParticipationStatus(ParticipationStatus.COMMENTED);
                        setUpCommentRecyclerView();
                        return updateUpdatables();
                    });
        });

        activityViewProjectBinding.buttonViewProjectDeleteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = firebaseFirestore
                        .collection(Parti.COMMENT_COLLECTION_PATH).document(project.getProjectId())
                        .collection(Parti.COMMENT_SUBCOLLECTION_PATH).document(user.getUuid());
                documentReference.delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ViewProjectActivity.this, "Deleted comment.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ViewProjectActivity.this, "Failed to delete comment.", Toast.LENGTH_LONG).show();
                            }
                        })
                        .onSuccessTask(unused -> {
                            user.removeComment(project.getProjectId());
                            project.removeComment(myComment);
                            displayRating();
                            myComment = null;
                            handleParticipationStatus(ParticipationStatus.PARTICIPATED);
                            displayAddComment();
                            setUpCommentRecyclerView();
                            return updateUpdatables();
                        });
            }
        });

        activityViewProjectBinding.buttonViewProjectDonate.setOnClickListener(v -> {
            String input = activityViewProjectBinding.inputViewProjectDonation.getText().toString();
            if (input.isEmpty()) {
                Toast.makeText(ViewProjectActivity.this, "Failed to donate: empty input.", Toast.LENGTH_LONG).show();
                return;
            }
            double amount = Double.parseDouble(input);
            if (amount <= 0.0) {
                Toast.makeText(ViewProjectActivity.this, "Failed to donate: non-positive amount.", Toast.LENGTH_LONG).show();
                return;
            }
            if (amount > user.getParticipationPoints()) {
                Toast.makeText(ViewProjectActivity.this, "Failed to donate: amount greater than your current balance.", Toast.LENGTH_LONG).show();
                return;
            }
            project.addDonation(user, amount);
            user.donate(project, amount);
            updateUpdatables();
            displayDonations();
        });

        View.OnClickListener goToViewUserActivity = v -> {
            Intent intent = new Intent(ViewProjectActivity.this, ViewUserActivity.class);
            intent.putExtra(User.CLASS_ID, (Bundle) null);
            intent.putExtra(User.UUID_FIELD, project.getAdmin());
            startActivity(intent);
        };
        activityViewProjectBinding.imageViewProjectAdmin.setOnClickListener(goToViewUserActivity);
        activityViewProjectBinding.inputViewProjectAdmin.setOnClickListener(goToViewUserActivity);
    }

    @Override
    public void onStart() {
        super.onStart();
        downloadImage();
        initialise();
    }

    @Override
    public void onStop() {
        super.onStop();
        project.updateRankings();
    }

    @Override
    public void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (request == EDIT_PROJECT_REQUEST_CODE && result == EditProjectActivity.EDIT_PROJECT_RESULT_CODE) {
            project = (Project) data.getExtras().get(Project.CLASS_ID);
        }
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
                activityViewProjectBinding.buttonViewProjectEdit.setVisibility(View.VISIBLE);
                activityViewProjectBinding.constraintLayoutViewProjectVerificationCode.setVisibility(View.GONE);
                activityViewProjectBinding.constraintLayoutViewProjectAddComment.setVisibility(View.GONE);
                activityViewProjectBinding.buttonViewProjectDeleteComment.setVisibility(View.GONE);
                break;
            case NOT_PARTICIPATED:
                activityViewProjectBinding.buttonViewProjectEdit.setVisibility(View.INVISIBLE);
                activityViewProjectBinding.constraintLayoutViewProjectVerificationCode.setVisibility(View.VISIBLE);
                activityViewProjectBinding.constraintLayoutViewProjectAddComment.setVisibility(View.GONE);
                activityViewProjectBinding.buttonViewProjectDeleteComment.setVisibility(View.GONE);
                break;
            case PARTICIPATED:
                activityViewProjectBinding.buttonViewProjectEdit.setVisibility(View.INVISIBLE);
                activityViewProjectBinding.constraintLayoutViewProjectVerificationCode.setVisibility(View.VISIBLE);
                activityViewProjectBinding.constraintLayoutViewProjectAddComment.setVisibility(View.VISIBLE);
                activityViewProjectBinding.buttonViewProjectDeleteComment.setVisibility(View.GONE);

                String buttonCommentText = "Comment";
                activityViewProjectBinding.buttonViewProjectAddComment.setText(buttonCommentText);
                break;
            case COMMENTED:
                activityViewProjectBinding.buttonViewProjectEdit.setVisibility(View.INVISIBLE);
                activityViewProjectBinding.constraintLayoutViewProjectVerificationCode.setVisibility(View.VISIBLE);
                activityViewProjectBinding.constraintLayoutViewProjectAddComment.setVisibility(View.VISIBLE);
                activityViewProjectBinding.buttonViewProjectDeleteComment.setVisibility(View.VISIBLE);

                buttonCommentText = "Update";
                activityViewProjectBinding.buttonViewProjectAddComment.setText(buttonCommentText);
                downloadMyComment();
                break;
            default:
                break;
        }
        participationStatus = newStatus;
    }

    private void setUpCommentRecyclerView() {

        query = firebaseFirestore.collection(Parti.COMMENT_COLLECTION_PATH).document(project.getProjectId()).collection(Parti.COMMENT_SUBCOLLECTION_PATH);
        FirestoreRecyclerOptions<ProjectComment> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<ProjectComment>()
                .setQuery(query, ProjectComment.class)
                .setLifecycleOwner(this)
                .build();
        commentRecyclerAdapter = new CommentRecyclerAdapter(firestoreRecyclerOptions);
        activityViewProjectBinding.recyclerViewViewProjectComments.setLayoutManager(new LinearLayoutManagerWrapper(this));
        activityViewProjectBinding.recyclerViewViewProjectComments.setAdapter(commentRecyclerAdapter);
    }

    private void downloadImage() {
        //Download image
        StorageReference imageReference = firebaseStorage.getReference().child(project.getImageId());
        final long ONE_MEGABYTE = 1024 * 1024;
        imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            activityViewProjectBinding.imageViewProject.setImageBitmap(bmp);
        }).addOnFailureListener(exception -> {
            Toast.makeText(ViewProjectActivity.this, "Failed to download project image.", Toast.LENGTH_LONG).show();
            //If failed, load the default local image;
            Glide.with(activityViewProjectBinding.imageViewProject.getContext())
                    .load(android.R.drawable.ic_dialog_info)
                    .into(activityViewProjectBinding.imageViewProject);
        });
    }

    private void initialise() {
        //Set the displayed values
        displayPPs();
        displayAdmin();
        displayProjectTitle();
        displayProjectType();
        displayProgress();
        displayProjectDescription();
        displayRating();
        displayPpsEarned();
        displayDonations();
        displayAddComment();
    }

    private void displayPPs() {
        String pp = String.format(Locale.ENGLISH, "%.2f PPs!", project.getParticipationPoints().get(0));
        activityViewProjectBinding.inputViewProjectPPs.setText(pp);
    }

    private void displayAdmin() {
        final long ONE_MEGA_BYTE = 1024 * 1024;
        firebaseFirestore
                .collection(Parti.USER_COLLECTION_PATH)
                .document(project.getAdmin())
                .get()
                .onSuccessTask(documentSnapshot -> {
                    String alias = documentSnapshot.getString(User.ALIAS_FIELD);
                    if (user.getUuid().equals(documentSnapshot.getString(User.UUID_FIELD))) {
                        alias += " (You)";
                    }
                    activityViewProjectBinding.inputViewProjectAdmin.setText(alias);
                    String imageId = documentSnapshot.getString(User.PROFILE_IMAGE_ID_FIELD);
                    return firebaseStorage.getReference().child(imageId).getBytes(ONE_MEGA_BYTE);
                })
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        byte[] bytes = task.getResult();
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        activityViewProjectBinding.imageViewProjectAdmin.setImageBitmap(bmp);
                        return task;
                    } else {
                        return firebaseStorage.getReference().child(User.DEFAULT_PROFILE_IMAGE_ID).getBytes(ONE_MEGA_BYTE);
                    }
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        byte[] bytes = task.getResult();
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        activityViewProjectBinding.imageViewProjectAdmin.setImageBitmap(bmp);
                    } else {
                        String alias = "unknown";
                        activityViewProjectBinding.inputViewProjectAdmin.setText(alias);
                        Glide.with(activityViewProjectBinding.imageViewProjectAdmin.getContext())
                                .load(android.R.drawable.sym_def_app_icon)
                                .into(activityViewProjectBinding.imageViewProjectAdmin);
                        Toast.makeText(ViewProjectActivity.this, "Something went wrong when downloading admin details.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void displayProjectTitle() {
        activityViewProjectBinding.inputViewProjectTitle.setText(project.getName());
    }

    private void displayProjectType() {
        ProjectType type = project.getProjectType();
        activityViewProjectBinding.inputViewProjectType.setText(type.toString());
    }

    private void displayProgress() {
        activityViewProjectBinding.progressBarViewProject.setMax(project.getNumActionsNeeded());
        activityViewProjectBinding.progressBarViewProject.setProgress(project.getNumActions());
        String progress = project.getNumActions() + "/" + project.getNumActionsNeeded() + " Actions Done";
        activityViewProjectBinding.inputViewProjectProgressDetail.setText(progress);
    }

    private void displayProjectDescription() {
        activityViewProjectBinding.inputViewProjectDescription.setText(project.getDescription());
    }

    private void displayRating() {
        float rating = 0;
        int numPeopleRated = project.getNumComments();
        if (numPeopleRated != 0) rating = ((float) project.getTotalRating()) / numPeopleRated;
        String ratingDetail = String.format(Locale.CANADA, "Average Rating: %.1f\n%d People Rated", rating, numPeopleRated);
        activityViewProjectBinding.ratingBarViewProject.setRating(rating);
        activityViewProjectBinding.inputViewProjectRatingDetails.setText(ratingDetail);
    }

    private void displayPpsEarned() {
        double participationPointsEarned = 0;
        if (user.getProjectsParticipated().contains(project.getProjectId()))
            participationPointsEarned = user.getParticipationPointsEarned().getOrDefault(project.getProjectId(), 0.0);
        String ppEarned = String.format(Locale.ENGLISH, "You have earned %.2f PPs from this project", participationPointsEarned);
        activityViewProjectBinding.inputViewProjectPpEarned.setText(ppEarned);
    }

    private void displayDonations() {
        String detail = String.format(Locale.ENGLISH, "Donate your Participation Points to boost this project's ranking and let more people see it.\nYou currently have %.2f PPs.", user.getParticipationPoints());
        String userId = user.getUuid();
        Map<String, Double> donors = project.getDonors();
        if (donors.containsKey(userId)) {
            double myDonation = donors.get(userId);
            detail += String.format(Locale.ENGLISH, "\nYou have donated %.2f PPs to this project. Thank you for your support.", myDonation);
        } else {
            detail += "\nYou have donated 0.00 PPs to this project.";
        }
        detail += String.format(Locale.ENGLISH, "\nTotal donated amount to this project: %.2f", project.getDonatedParticipationPoints());
        activityViewProjectBinding.inputViewProjectDonationTips.setText(detail);
        String hint = "Enter the amount of PPs here.";
        activityViewProjectBinding.inputViewProjectDonation.setHint(hint);
    }

    private void displayAddComment() {
        if (participationStatus == ParticipationStatus.COMMENTED) {
            downloadMyComment();
        } else {
            activityViewProjectBinding.ratingBarViewProjectCommentRating.setRating(0);
            String hint = "Enter your comment here.";
            activityViewProjectBinding.inputViewProjectAddComment.setText("");
            activityViewProjectBinding.inputViewProjectAddComment.setHint(hint);
        }
    }

    private void downloadVerificationCodeBundle() {
        firebaseFirestore.collection(Parti.VERIFICATION_CODE_OBJECT_COLLECTION_PATH)
                .document(project.getProjectId())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        verificationCodeBundle = task.getResult().toObject(VerificationCodeBundle.class);
                    } else {
                        Toast.makeText(ViewProjectActivity.this, "Failed to download verification code bundle.", Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }

    private Task<Void> updateUpdatables() {
        DocumentReference projectReference = firebaseFirestore.collection(Parti.PROJECT_COLLECTION_PATH).document(project.getProjectId());
        DocumentReference userReference = firebaseFirestore.collection(Parti.USER_COLLECTION_PATH).document(user.getUuid());
        DocumentReference verificationReference = firebaseFirestore.collection(Parti.VERIFICATION_CODE_OBJECT_COLLECTION_PATH).document(project.getProjectId());

        Task<Void> uploadProjectTask = projectReference.set(project);
        Task<Void> uploadUserTask = userReference.set(user);
        Task<Void> uploadVerificationCodeBundleTask = verificationReference.set(verificationCodeBundle);

        return Tasks.whenAll(uploadProjectTask, uploadUserTask, uploadVerificationCodeBundleTask)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(
                                        ViewProjectActivity.this,
                                        "Fully uploaded project, user, and verification code bundle.",
                                        Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Toast.makeText(
                                        ViewProjectActivity.this,
                                        "Something went wrong when uploading project, user, and verification code bundle.",
                                        Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }

    private void downloadMyComment() {
        if (participationStatus == ParticipationStatus.COMMENTED) {
            firebaseFirestore.collection(Parti.COMMENT_COLLECTION_PATH).document(project.getProjectId())
                    .collection(Parti.COMMENT_SUBCOLLECTION_PATH).document(user.getUuid())
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            myComment = task.getResult().toObject(ProjectComment.class);
                            String comment = myComment.getComment();
                            activityViewProjectBinding.inputViewProjectAddComment.setText(comment);
                            activityViewProjectBinding.ratingBarViewProjectCommentRating.setRating(myComment.getRating());
                        } else {
                            Toast.makeText(ViewProjectActivity.this, "Failed to download existing comment", Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
        }
    }
}