package com.example.parti.wrappers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class VerificationCode implements Serializable {

    // [start of field constants]
    public static final String CODE_FIELD = "code";
    //public static final String PROJECT_FIELD = "project";
    public static final String PARTICIPANT_FIELD = "participant";
    public static final String PARTICIPATION_POINTS_FIELD = "participationPoints";
    public static final String REDEEMED_FIELD = "redeemed";
    public static final String REDEEMABLE_FIELD = "redeemable";
    // [start of field constant]

    private String code;
    //private String project;
    private String participant;
    private double participationPoints;
    private boolean redeemed;
    private boolean redeemable;

    public VerificationCode() {};

    public VerificationCode(String code, double participationPoints) {
        this(code, "", participationPoints, false, true);
    }

    public VerificationCode(String code, String participant, double participationPoints, boolean redeemed, boolean redeemable) {
        this.code = code;
        //this.project = project;
        this.participant = participant;
        this.participationPoints = participationPoints;
        this.redeemed = redeemed;
        this.redeemable = redeemable;
    }

    public String getCode() {return code;}
    public String getParticipant() {return participant;}
    //public String getProject() {return project;}
    public double getParticipationPoints() {return participationPoints;}
    public boolean isRedeemed() {return redeemed;}
    public boolean isRedeemable() {return redeemable;}

    public void setCode(String code) {this.code = code;}
    public void setParticipant(String participant) {this.participant = participant;}
    //public void setProject(String project) {this.project = project;}
    public void setParticipationPoints(double participationPoints) {this.participationPoints = participationPoints;}
    public void setRedeemed(boolean redeemed) {this.redeemed = redeemed;}
    public void setRedeemable(boolean redeemable) {this.redeemable = redeemable;}

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(CODE_FIELD, code);
        map.put(PARTICIPANT_FIELD, participant);
        map.put(PARTICIPATION_POINTS_FIELD, participationPoints);
        map.put(REDEEMED_FIELD, redeemed);
        map.put(REDEEMABLE_FIELD, redeemable);
        return map;
    }

    public static VerificationCode mapToVerificationCode(Map<String, Object> map) {
        String code = (String) map.getOrDefault(CODE_FIELD, "");
        String participant = (String) map.getOrDefault(PARTICIPANT_FIELD, "");
        double participationPoints = (Double) map.getOrDefault(PARTICIPATION_POINTS_FIELD, 0.0);
        boolean redeemed = (Boolean) map.getOrDefault(REDEEMED_FIELD, false);
        boolean redeemable = (Boolean) map.getOrDefault(REDEEMABLE_FIELD, true);
        return new VerificationCode(code, participant, participationPoints, redeemed, redeemable);
    }
}
