package com.example.parti.wrappers;

import androidx.annotation.NonNull;

import com.example.parti.Parti;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class VerificationCodeBundle {

    // [start of field constants]
    public static final String PROJECT_ID_FIELD = "projectId";
    public static final String NUM_REDEEMED_FIELD = "numRedeemed";
    public static final String NUM_REDEEMABLE_FIELD = "numRedeemable";
    public static final String VERIFICATION_CODE_LIST_FIELD = "verificationCodeList";
    // [end of field constants]

    private String projectId;
    private int numRedeemed;
    private int numRedeemable;
    private List<VerificationCode> verificationCodeList;

    public VerificationCodeBundle() {}

    public VerificationCodeBundle(String projectId) {
        this(projectId, new ArrayList<>());
    }

    public VerificationCodeBundle(String projectId, List<VerificationCode> verificationCodeList) {
        this(projectId, 0, 0, verificationCodeList);
    }

    public VerificationCodeBundle(String projectId, int numRedeemed, int numRedeemable, List<VerificationCode> verificationCodeList) {
        this.projectId = projectId;
        this.numRedeemed = numRedeemed;
        this.numRedeemable = numRedeemable;
        this.verificationCodeList = verificationCodeList;
    }

    public String getProjectId() {return projectId;}
    public int getNumRedeemed() {return numRedeemed;}
    public int getNumRedeemable() {return numRedeemable;}
    public List<VerificationCode> getVerificationCodeList() {return verificationCodeList;}

    public void setProjectId(String projectId) {this.projectId = projectId;}
    public void setNumRedeemed(int numRedeemed) {this.numRedeemed = numRedeemed;}
    public void setNumRedeemable(int numRedeemable) {this.numRedeemable = numRedeemable;}
    public void setVerificationCodeList(List<VerificationCode> verificationCodeList) {this.verificationCodeList = verificationCodeList;}

    public void adjustList(int targetNum, double participationPoints) {
        //int currentNum = verificationCodeList.size();
        int diff = targetNum - numRedeemed;
        int numRedeemable = getNumRedeemable();
        if (diff < numRedeemable) {
            int i = diff;
            for (VerificationCode code: verificationCodeList) {
                if ((!code.isRedeemed()) && code.isRedeemable()) {
                    code.setRedeemable(false);
                    i++;
                    if (i == numRedeemable) break;
                }
            }
        } else if (diff > numRedeemable) {
            int i = numRedeemable;
            for (VerificationCode code: verificationCodeList) {
                if (!(code.isRedeemed() || code.isRedeemable())) {
                    code.setRedeemable(true);
                    i++;
                    if (i == diff) break;
                }
            }
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = firebaseFirestore.collection(Parti.VERIFICATION_CODE_ID_COLLECTION_PATH);
            addVerificationCode(i, diff, participationPoints, collectionReference);
        }
        updateParticipationPoints(participationPoints);
        setNumRedeemable(diff);
    }

    private void addVerificationCode(int i, int limit, double participationPoints, CollectionReference collectionReference) {
        if (i == limit) {
            updateParticipationPoints(participationPoints);
            return;
        }
        String id = collectionReference.getId();
        collectionReference.document(id).set(0).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    verificationCodeList.add(new VerificationCode(id, participationPoints));
                    addVerificationCode(i + 1, limit, participationPoints, collectionReference);
                } else {
                    addVerificationCode(i, limit, participationPoints, collectionReference);
                }
            }
        });
    }

    private void addVerificationCode(int limit, double participationPoints) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection(Parti.VERIFICATION_CODE_ID_COLLECTION_PATH);
        addVerificationCode(0, limit, participationPoints, collectionReference);
    }

    private void updateParticipationPoints(double participationPoints) {
        for (VerificationCode code: verificationCodeList) {
            if (!code.isRedeemed()) code.setParticipationPoints(participationPoints);
        }
    }
}
