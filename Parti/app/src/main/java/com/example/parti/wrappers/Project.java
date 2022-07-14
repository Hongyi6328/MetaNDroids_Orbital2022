package com.example.parti.wrappers;

import androidx.annotation.NonNull;

import com.example.parti.Parti;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable, Updatable {

    public static final int SHORT_DESCRIPTION_LENGTH = Parti.SHORT_DESCRIPTION_LENGTH;
    public static final int DEFAULT_RANKING = Parti.DEFAULT_RANKING;

    // [start of field constants]
    public static final String PROJECT_ID_FIELD = "projectId"; //required
    public static final String NAME_FIELD = "name"; //required
    public static final String PROJECT_TYPE_FIELD = "projectType"; //required
    public static final String CONCLUDED_FIELD = "concluded"; //required
    public static final String ADMIN_FIELD = "admin"; //required
    public static final String DEVELOPERS_FIELD = "developers"; //required
    public static final String PARTICIPANTS_FIELD = "participants"; //required
    public static final String NUM_PARTICIPANTS_FIELD = "numParticipants"; //required
    public static final String NUM_PARTICIPANTS_NEEDED_FIELD = "numParticipantsNeeded"; // required
    public static final String RANKING_FIELD = "ranking"; //required
    public static final String DESCRIPTION_FIELD = "description"; //required
    public static final String NUM_COMMENTS_FIELD = "numComments"; //required
    public static final String TOTAL_RATING_FIELD = "totalRating"; //required
    public static final String LAUNCH_DATE_FIELD = "launchDate"; //required
    public static final String IMAGE_ID_FIELD = "imageId";
    public static final String PARTICIPATION_POINTS_FIELD = "participationPoints";
    public static final String PARTICIPATION_POINTS_BALANCE_FIELD = "participationPointsBalance";
    // [end of field constants

    // TODO: Consider String References vs Wrapper Classes
    private String projectId; //required
    private String name; //required
    private ProjectType projectType; //required
    private boolean concluded; //required
    private String admin; //required
    private List<String> developers; //required
    private List<String> participants; //required
    private int numParticipants; //required
    private int numParticipantsNeeded; // required
    private double ranking; //required
    private String description; //required
    private int numComments; //required
    private long totalRating; //required
    private String launchDate; //required
    private String imageId;
    private List<Double> participationPoints;
    private double participationPointsBalance;

    public Project() {}

    public Project(@NonNull String id,
                   @NonNull String name,
                   @NonNull String description,
                   @NonNull String imageId) {
        this(
                id,
                name,
                ProjectType.APP,
                false,
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                List.of(FirebaseAuth.getInstance().getCurrentUser().getUid()),
                new ArrayList<>(),
                0,
                0,
                DEFAULT_RANKING,
                description,
                0,
                0,
                LocalDate.now().toString(),
                imageId,
                new ArrayList<>(),
                0);
    }

    public Project(@NonNull String id,
                   @NonNull String name,
                   @NonNull ProjectType projectType,
                   boolean concluded,
                   @NonNull String admin,
                   @NonNull List<String> developers,
                   @NonNull List<String> participants,
                   int numParticipants,
                   int numParticipantsNeeded,
                   double ranking,
                   @NonNull String description,
                   int numComments,
                   long totalRating, String launchDate,
                   @NonNull String imageId,
                   @NonNull List<Double> participationPoints,
                   double participationPointsBalance) {
        this.projectId = id;
        this.name = name;
        this.projectType = projectType;
        this.concluded = concluded;
        this.admin = admin;
        this.developers = developers;
        this.participants = participants;
        this.numParticipants = numParticipants;
        this.numParticipantsNeeded = numParticipantsNeeded;
        this.ranking = ranking;
        this.description = description;
        this.numComments = numComments;
        this.totalRating = totalRating;
        this.launchDate = launchDate;
        this.imageId = imageId;
        this.participationPoints = participationPoints;
        this.participationPointsBalance = participationPointsBalance;
    }

    public String getProjectId() {return projectId;}
    public String getName() {return name;}
    public ProjectType getProjectType() {return projectType;}
    public boolean isConcluded() {return concluded;}
    //public boolean getConcluded() {return concluded;}
    public String getAdmin() {return admin;}
    public List<String> getDevelopers() {return developers;}
    public List<String> getParticipants() {return participants;}
    public int getNumParticipants() {return numParticipants;}
    public int getNumParticipantsNeeded() {return numParticipantsNeeded;}
    public double getRanking() {return ranking;}
    public String getDescription() {return description;}
    public int getNumComments() {return numComments;}
    public long getTotalRating() {return totalRating;}
    public String getLaunchDate() {return launchDate;}
    public String getImageId() {return imageId;}
    public List<Double> getParticipationPoints() {return participationPoints;}
    public double getParticipationPointsBalance() {return participationPointsBalance;}

    //public void setProjectId(String projectId) {this.projectId = projectId;}
    public void setProjectName(String name) {this.name = name;}
    public void setProjectType(ProjectType projectType) {this.projectType = projectType;}
    public void setConcluded(boolean concluded) {this.concluded = concluded;}
    public void setAdmin(String admin) {this.admin = admin;}
    public void setDevelopers(List<String> developers) {this.developers = developers;}
    public void setParticipants(List<String> participants) {this.participants = participants;}
    public void setNumParticipants(int numParticipants) {this.numParticipants = numParticipants;}
    public void setNumParticipantsNeeded(int numParticipantsNeeded) {this.numParticipantsNeeded = numParticipantsNeeded;}
    public void setRanking(double ranking) {this.ranking = ranking;}
    public void setDescription(String description) {this.description = description;}
    public void setNumComments(int numComments) {this.numComments = numComments;}
    public void setTotalRating(long totalRating) {this.totalRating = totalRating;}
    public void setLaunchDate(String launchDate) {this.launchDate = launchDate;}
    public void setImageId(String imageId) {this.imageId = imageId;}
    public void setParticipationPoints(List<Double> participationPoints) {this.participationPoints = participationPoints;}
    public void setParticipationPointsBalance(double participationPointsBalance) {this.participationPointsBalance = participationPointsBalance;}

    public void increaseParticipationPointsBalance(double offset) {this.participationPointsBalance += offset;}
    public void addParticipant(String participant) {
        if (participants.contains(participant)) return;
        participants.add(participant);
        numParticipants++;
    }

    public String getShortDescription() {
        return description.length() > SHORT_DESCRIPTION_LENGTH
                ? description.substring(0, SHORT_DESCRIPTION_LENGTH) + "...>"
                : description;
    }

    @Override
    public void update() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection(Parti.PROJECT_COLLECTION_PATH).document(projectId);
        documentReference.set(this);
    }
}
