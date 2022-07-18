package com.example.parti.wrappers;

import androidx.annotation.NonNull;

import com.example.parti.Parti;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Comment;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Project implements Serializable, Updatable {

    /*
    public static final int SHORT_DESCRIPTION_LENGTH = Parti.SHORT_DESCRIPTION_LENGTH;
    public static final int DEFAULT_RANKING = Parti.DEFAULT_RANKING;
    */

    public static final String CLASS_ID = "project";

    // [start of field constants]
    public static final String PROJECT_ID_FIELD = "projectId";
    public static final String NAME_FIELD = "name";
    public static final String PROJECT_TYPE_FIELD = "projectType";
    public static final String CONCLUDED_FIELD = "concluded";
    public static final String ADMIN_FIELD = "admin";
    public static final String DEVELOPERS_FIELD = "developers";
    public static final String PARTICIPANTS_FIELD = "participants";
    public static final String NUM_ACTIONS_FIELD = "numActions";
    public static final String NUM_ACTIONS_NEEDED_FIELD = "numActionsNeeded";
    public static final String NUM_PARTICIPANTS_FIELD = "numParticipants";
    public static final String NUM_PARTICIPANTS_NEEDED_FIELD = "numParticipantsNeeded";
    public static final String NUM_COMMENTS_FIELD = "numComments";
    public static final String COMMENTS_FIELD = "comments";
    public static final String RANKING_FIELD = "ranking";
    public static final String DESCRIPTION_FIELD = "description";
    public static final String TOTAL_RATING_FIELD = "totalRating";
    public static final String LAUNCH_DATE_FIELD = "launchDate";
    public static final String IMAGE_ID_FIELD = "imageId";
    public static final String PARTICIPATION_POINTS_FIELD = "participationPoints";
    public static final String PARTICIPATION_POINTS_BALANCE_FIELD = "participationPointsBalance";
    public static final String DONORS_FIELD = "donors";
    // [end of field constants

    private String projectId;
    private String name;
    private ProjectType projectType;
    private boolean concluded;
    private String admin;
    private List<String> developers;
    private List<String> participants;
    private int numActions;
    private int numActionsNeeded;
    private int numParticipants;
    private int numParticipantsNeeded;
    private double ranking;
    private String description;
    private List<String> comments; // id of comment posters
    private int numComments;
    private long totalRating;
    private String launchDate;
    private String imageId;
    private List<Double> participationPoints;
    private double participationPointsBalance;
    private double donatedParticipationPoints;
    private Map<String, Double> donors;

    public Project() {}

    public Project(@NonNull String projectId,
                   @NonNull String name,
                   @NonNull String description,
                   @NonNull String imageId) {
        this(
                projectId,
                name,
                ProjectType.APP,
                false,
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                List.of(FirebaseAuth.getInstance().getCurrentUser().getUid()),
                new ArrayList<>(),
                0,
                0,
                0,
                0,
                Parti.DEFAULT_RANKING,
                description,
                0,
                new ArrayList<>(),
                0,
                LocalDate.now().toString(),
                imageId,
                new ArrayList<>(),
                0,
                0,
                new HashMap<>());
    }

    public Project(@NonNull String projectId,
                   @NonNull String name,
                   @NonNull ProjectType projectType,
                   boolean concluded,
                   @NonNull String admin,
                   @NonNull List<String> developers,
                   @NonNull List<String> participants,
                   int numActions,
                   int numActionsNeeded,
                   int numParticipants,
                   int numParticipantsNeeded,
                   double ranking,
                   @NonNull String description,
                   int numComments,
                   List<String> comments,
                   long totalRating, String launchDate,
                   @NonNull String imageId,
                   @NonNull List<Double> participationPoints,
                   double participationPointsBalance,
                   double donatedParticipationPoints,
                   @NonNull Map<String, Double> donors) {
        this.projectId = projectId;
        this.name = name;
        this.projectType = projectType;
        this.concluded = concluded;
        this.admin = admin;
        this.developers = developers;
        this.participants = participants;
        this.numActions = numActions;
        this.numActionsNeeded = numActionsNeeded;
        this.numParticipants = numParticipants;
        this.numParticipantsNeeded = numParticipantsNeeded;
        this.ranking = ranking;
        this.description = description;
        this.numComments = numComments;
        this.comments = comments;
        this.totalRating = totalRating;
        this.launchDate = launchDate;
        this.imageId = imageId;
        this.participationPoints = participationPoints;
        this.participationPointsBalance = participationPointsBalance;
        this.donatedParticipationPoints = donatedParticipationPoints;
        this.donors = donors;
    }

    public String getProjectId() {return projectId;}
    public String getName() {return name;}
    public ProjectType getProjectType() {return projectType;}
    public boolean isConcluded() {return concluded;}
    //public boolean getConcluded() {return concluded;}
    public String getAdmin() {return admin;}
    public List<String> getDevelopers() {return developers;}
    public List<String> getParticipants() {return participants;}
    public int getNumActions() {return numActions;}
    public int getNumActionsNeeded() {return numActionsNeeded;}
    public int getNumParticipantsNeeded() {return numParticipantsNeeded;}
    public int getNumParticipants() {return numParticipants;}
    public double getRanking() {return ranking;}
    public String getDescription() {return description;}
    public int getNumComments() {return numComments;}
    public List<String> getComments() {return comments;}
    public long getTotalRating() {return totalRating;}
    public String getLaunchDate() {return launchDate;}
    public String getImageId() {return imageId;}
    public List<Double> getParticipationPoints() {return participationPoints;}
    public double getParticipationPointsBalance() {return participationPointsBalance;}
    public double getDonatedParticipationPoints() {return donatedParticipationPoints;}
    public Map<String, Double> getDonors() {return donors;}

    //public void setProjectId(String projectId) {this.projectId = projectId;}
    public void setProjectName(String name) {this.name = name;}
    public void setProjectType(ProjectType projectType) {this.projectType = projectType;}
    public void setConcluded(boolean concluded) {this.concluded = concluded;}
    public void setAdmin(String admin) {this.admin = admin;}
    public void setDevelopers(List<String> developers) {this.developers = developers;}
    public void setParticipants(List<String> participants) {this.participants = participants;}
    public void setNumActions(int numActions) {this.numActions = numActions;}
    public void setNumActionsNeeded(int numActionsNeeded) {this.numActionsNeeded = numActionsNeeded;}
    public void setNumParticipants(int numParticipants) {this.numParticipants = numParticipants;}
    public void setNumParticipantsNeeded(int numParticipantsNeeded) {this.numParticipantsNeeded = numParticipantsNeeded;}
    public void setRanking(double ranking) {this.ranking = ranking;}
    public void setDescription(String description) {this.description = description;}
    public void setComments(List<String> comments) {this.comments = comments;}
    public void setNumComments(int numComments) {this.numComments = numComments;}
    public void setTotalRating(long totalRating) {this.totalRating = totalRating;}
    public void setLaunchDate(String launchDate) {this.launchDate = launchDate;}
    public void setImageId(String imageId) {this.imageId = imageId;}
    public void setParticipationPoints(List<Double> participationPoints) {this.participationPoints = participationPoints;}
    public void setParticipationPointsBalance(double participationPointsBalance) {this.participationPointsBalance = participationPointsBalance;}
    public void setDonatedParticipationPoints(double donatedParticipationPoints) {this.donatedParticipationPoints = donatedParticipationPoints;}
    public void setDonors(Map<String, Double> donors) {this.donors = donors;}

    public String shortDescription() {
        return description.length() > Parti.SHORT_DESCRIPTION_LENGTH
                ? description.substring(0, Parti.SHORT_DESCRIPTION_LENGTH) + "...>"
                : description;
    }

    public void increaseParticipationPointsBalance(double offset) {this.participationPointsBalance += offset;}
    public void increaseTotalRating(long offset) {this.totalRating += offset;}
    public void addParticipant(User user) {
        if (participants.contains(user.getUuid())) return;
        participants.add(user.getUuid());
        numParticipants++;
    }
    public void addAction(User user) {
        numActions++;
        increaseParticipationPointsBalance(-participationPoints.get(0));
        addParticipant(user);
        calculateRanking();
    }
    public void addComment(ProjectComment newComment, ProjectComment oldComment) {
        if (!comments.contains(newComment.getSenderId())) {
            numComments++;
            comments.add(newComment.getSenderId());
        }
        int ratingOffset = newComment.getRating();
        if (oldComment != null) ratingOffset -= oldComment.getRating();
        increaseTotalRating(ratingOffset);
        calculateRanking();
    }
    public void removeComment(ProjectComment oldComment) {
        if (!comments.contains(oldComment.getSenderId())) {
            numComments--;
            comments.remove(oldComment.getSenderId());
        }
        int ratingOffset = oldComment.getRating();
        increaseTotalRating(-ratingOffset);
        calculateRanking();
    }
    public void addDonation(User user, double pp) {
        Double cumulatedPp = donors.getOrDefault(user.getUuid(), 0.0);
        if (cumulatedPp == null) cumulatedPp = 0.0;
        cumulatedPp += pp;
        donors.put(user.getUuid(), cumulatedPp);
        donatedParticipationPoints += pp;
        calculateRanking();
    }
    public void calculateRanking() {}

    @Override
    public void update() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection(Parti.PROJECT_COLLECTION_PATH).document(projectId);
        documentReference.set(this);
    }
}
