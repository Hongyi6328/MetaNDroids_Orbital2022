package com.example.parti.ui.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.parti.Parti;
import com.example.parti.databinding.ActivityViewProjectBinding;
import com.example.parti.adapters.CommentRecyclerAdapter;
import com.example.parti.wrappers.Project;
import com.example.parti.wrappers.ProjectComment;
import com.example.parti.wrappers.ProjectType;
import com.example.parti.wrappers.User;
import com.example.parti.wrappers.VerificationCodeBundle;
import com.example.parti.wrappers.util.LinearLayoutManagerWrapper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;
import java.util.Map;

public class ViewProjectActivity extends AppCompatActivity /*implements CommentAdapter.OnCommentSelectedListener, BrowseProjectsAdapter.OnProjectSelectedListener*/ {

    private enum ParticipationStatus {
        DEFAULT,
        UNKNOWN,
        ADMIN,
        NOT_PARTICIPATED,
        PARTICIPATED,
        COMMENTED,
    }

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
    //private CommentAdapter commentAdapter;

    /*
    private static final int PARTICIPATION_STATUS_ADMIN = 0;
    private static final int PARTICIPATION_STATUS_UNKNOWN = 1;
    private static final int PARTICIPATION_STATUS_NOT_PARTICIPATED = 2;
    private static final int PARTICIPATION_STATUS_PARTICIPATED = 3;
    private static final int PARTICIPATION_STATUS_COMMENTED = 4;
     */

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

        activityViewProjectBinding.buttonViewProjectEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProjectActivity.this, EditProjectActivity.class);
                intent.putExtra(Project.CLASS_ID, project);
                intent.putExtra(VerificationCodeBundle.CLASS_ID, verificationCodeBundle);
                startActivity(intent);
            }
        });

        activityViewProjectBinding.buttonViewProjectBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        activityViewProjectBinding.buttonViewProjectSubmitVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        activityViewProjectBinding.buttonViewProjectAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentBody = activityViewProjectBinding.inputViewProjectAddComment.getText().toString();
                if (commentBody.length() > Parti.PROJECT_COMMENT_LENGTH) {
                    String toast = String.format(Locale.ENGLISH, "The length of comment cannot exceed %d characters.", Parti.PROJECT_COMMENT_LENGTH);
                    Toast.makeText(ViewProjectActivity.this, toast, Toast.LENGTH_LONG).show();
                    return;
                }
                int rating = (int) activityViewProjectBinding.ratingBarViewProjectCommentRating.getRating();
                ProjectComment comment = new ProjectComment(user.getUuid(), commentBody, rating);
                DocumentReference documentReference = firebaseFirestore
                        .collection(Parti.COMMENT_COLLECTION_PATH).document(project.getProjectId())
                        .collection(Parti.COMMENT_SUBCOLLECTION_PATH).document(user.getUuid());
                documentReference.set(comment)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ViewProjectActivity.this, "Uploaded comment.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(ViewProjectActivity.this, "Failed to upload comment.", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .onSuccessTask(new SuccessContinuation<Void, Void>() {
                            @NonNull
                            @Override
                            public Task<Void> then(Void unused) throws Exception {
                                user.addComment(project.getProjectId());
                                project.addComment(comment, myComment);
                                displayRating();
                                myComment = comment;
                                handleParticipationStatus(ParticipationStatus.COMMENTED);
                                setUpCommentRecyclerView();
                                return updateUpdatables();
                            }
                        });
            }
        });

        activityViewProjectBinding.buttonViewProjectDeleteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = firebaseFirestore
                        .collection(Parti.COMMENT_COLLECTION_PATH).document(project.getProjectId())
                        .collection(Parti.COMMENT_SUBCOLLECTION_PATH).document(user.getUuid());
                documentReference.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ViewProjectActivity.this, "Deleted comment.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(ViewProjectActivity.this, "Failed to delete comment.", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .onSuccessTask(new SuccessContinuation<Void, Void>() {
                            @NonNull
                            @Override
                            public Task<Void> then(Void unused) throws Exception {
                                user.removeComment(project.getProjectId());
                                project.removeComment(myComment);
                                displayRating();
                                myComment = null;
                                handleParticipationStatus(ParticipationStatus.PARTICIPATED);
                                displayAddComment();
                                setUpCommentRecyclerView();
                                return updateUpdatables();
                            }
                        });
            }
        });

        activityViewProjectBinding.buttonViewProjectDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = activityViewProjectBinding.inputViewProjectDonation.getText().toString();
                if (input.isEmpty()) {
                    Toast.makeText(ViewProjectActivity.this, "Failed to donate: empty input.", Toast.LENGTH_LONG).show();
                    return;
                }
                double amount = Double.parseDouble(input);
                if (amount <= 0.0) {
                    Toast.makeText(ViewProjectActivity.this, "Failed to donate: negative amount.", Toast.LENGTH_LONG).show();
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
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        downloadImage();
        initialise();
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
                activityViewProjectBinding.constraintLayoutViewProjectDonation.setVisibility(View.GONE);
                break;
            case NOT_PARTICIPATED:
                activityViewProjectBinding.buttonViewProjectEdit.setVisibility(View.INVISIBLE);
                activityViewProjectBinding.constraintLayoutViewProjectVerificationCode.setVisibility(View.VISIBLE);
                activityViewProjectBinding.constraintLayoutViewProjectAddComment.setVisibility(View.GONE);
                activityViewProjectBinding.buttonViewProjectDeleteComment.setVisibility(View.GONE);
                activityViewProjectBinding.constraintLayoutViewProjectDonation.setVisibility(View.VISIBLE);
                break;
            case PARTICIPATED:
                activityViewProjectBinding.buttonViewProjectEdit.setVisibility(View.INVISIBLE);
                activityViewProjectBinding.constraintLayoutViewProjectVerificationCode.setVisibility(View.VISIBLE);
                activityViewProjectBinding.constraintLayoutViewProjectAddComment.setVisibility(View.VISIBLE);
                activityViewProjectBinding.buttonViewProjectDeleteComment.setVisibility(View.GONE);
                activityViewProjectBinding.constraintLayoutViewProjectDonation.setVisibility(View.VISIBLE);

                String buttonCommentText = "Comment";
                activityViewProjectBinding.buttonViewProjectAddComment.setText(buttonCommentText);
                break;
            case COMMENTED:
                activityViewProjectBinding.buttonViewProjectEdit.setVisibility(View.INVISIBLE);
                activityViewProjectBinding.constraintLayoutViewProjectVerificationCode.setVisibility(View.VISIBLE);
                activityViewProjectBinding.constraintLayoutViewProjectAddComment.setVisibility(View.VISIBLE);
                activityViewProjectBinding.buttonViewProjectDeleteComment.setVisibility(View.VISIBLE);
                activityViewProjectBinding.constraintLayoutViewProjectDonation.setVisibility(View.VISIBLE);

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

        /*
        query = firebaseFirestore.collection(Parti.COMMENT_COLLECTION_PATH).document(project.getProjectId()).collection(Parti.COMMENT_SUBCOLLECTION_PATH);
        commentAdapter = new CommentAdapter(query, this);
        activityViewProjectBinding.projectCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityViewProjectBinding.projectCommentsRecyclerView.setAdapter(commentAdapter);

        /*
        query = firebaseFirestore.collection(Parti.PROJECT_COLLECTION_PATH);
        BrowseProjectsAdapter adapter = new BrowseProjectsAdapter(query, this);
        activityViewProjectBinding.projectCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityViewProjectBinding.projectCommentsRecyclerView.setAdapter(adapter);
         */

        /*
        //Only for testing purposes
        Project[] projects = new Project[] {
                new Project("" + 1, "Email", "This is a short description about the project", ""),
                new Project("" + 2, "Info", "This is a short description about the project", ""),
                new Project("" + 3, "Delete", "This is a short description about the project", ""),
                new Project("" + 3, "Dialer", "This is a short description about the project", ""),
                new Project("" + 4, "Alert", "This is a short description about the project", ""),
                new Project("" + 5, "Map", "This is a short description about the project", ""),
                new Project("" + 6, "Email", "This is a short description about the project", ""),
                new Project("" + 7, "Info", "This is a short description about the project", ""),
                new Project("" + 8, "Delete", "This is a short description about the project", ""),
                new Project("" + 9, "Dialer", "This is a short description about the project", ""),
                new Project("" + 10, "Alert", "This is a short description about the project", ""),
                new Project("" + 11, "Map", "This is a short description about the project", ""),
        };

        RecyclerView recyclerView = activityViewProjectBinding.projectCommentsRecyclerView;
        BrowseProjectsRecyclerViewListAdapter adapter = new BrowseProjectsRecyclerViewListAdapter(projects);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        */

    }

    private void downloadImage() {
        //Download image
        StorageReference imageReference = firebaseStorage.getReference().child(project.getImageId());
        final long ONE_MEGABYTE = 1024 * 1024;
        imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                activityViewProjectBinding.imageViewProject.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ViewProjectActivity.this, "Failed to download project image", Toast.LENGTH_LONG).show();
                //If failed, load the default local image;
                Glide.with(activityViewProjectBinding.imageViewProject.getContext())
                        .load(android.R.drawable.ic_dialog_info)
                        .into(activityViewProjectBinding.imageViewProject);
            }
        });
    }

    private void initialise() {
        //Set the displayed values
        displayProjectTitle();
        displayProjectType();
        displayProgress();
        displayProjectDescription();
        displayRating();
        displayPpsEarned();
        displayDonations();
        displayAddComment();
    }

    private void displayProjectTitle() {
        activityViewProjectBinding.inputViewProjectTitle.setText(project.getName());
    }

    private void displayProjectType() {
        ProjectType type = project.getProjectType();
        int index = 0;
        for (; index < Parti.PROJECT_TYPES.length; index++) if (Parti.PROJECT_TYPES[index] == type) break;
        activityViewProjectBinding.spinnerViewProjectType.setSelection(index);
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
            detail += String.format(Locale.ENGLISH, "\nYou have donated %.2f PPs to this project. Thank you for your support.", donors.get(userId));
        } else {
            detail += "\nYou have donated 0.00 PPs to this project.";
        }
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
            activityViewProjectBinding.inputViewProjectAddComment.setHint(hint);
        }
    }

    private void downloadVerificationCodeBundle() {
        firebaseFirestore.collection(Parti.VERIFICATION_CODE_OBJECT_COLLECTION_PATH)
                .document(project.getProjectId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            verificationCodeBundle = task.getResult().toObject(VerificationCodeBundle.class);
                        } else {
                            Toast.makeText(ViewProjectActivity.this, "Failed to download verification code bundle.", Toast.LENGTH_LONG)
                                    .show();
                        }
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
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
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
                    }
                });
    }

    private void downloadMyComment() {
        if (participationStatus == ParticipationStatus.COMMENTED) {
            firebaseFirestore.collection(Parti.COMMENT_COLLECTION_PATH).document(project.getProjectId())
                    .collection(Parti.COMMENT_SUBCOLLECTION_PATH).document(user.getUuid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                myComment = task.getResult().toObject(ProjectComment.class);
                                String comment = myComment.getComment();
                                activityViewProjectBinding.inputViewProjectAddComment.setText(comment);
                            } else {
                                Toast.makeText(ViewProjectActivity.this, "Failed to download existing comment", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
        }
    }

    /*
    private void uploadVerificationCodeBundle() {
        firebaseFirestore.collection(Parti.VERIFICATION_CODE_OBJECT_COLLECTION_PATH)
                .document(project.getProjectId())
                .set(verificationCodeBundle).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful())
                            Toast.makeText(ViewProjectActivity.this, "Failed to upload verification code bundle.", Toast.LENGTH_LONG)
                                    .show();
                    }
                });
    }
    */

    /*
    @Override
    public void onCommentSelected(DocumentSnapshot comment) {

    }

    */
    /*
    @Override
    public void onProjectSelected(DocumentSnapshot project) {

    }
    */
}