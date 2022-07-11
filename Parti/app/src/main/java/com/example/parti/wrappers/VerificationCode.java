package com.example.parti.wrappers;

public class VerificationCode {

    // [start of field constants]
    public static final String CODE_FIELD = "code";
    //public static final String PROJECT_FIELD = "project";
    public static final String PARTICIPANT_FIELD = "participant";
    public static final String PARTICIPATION_POINTS_FIELD = "participationPoints";
    public static final String REDEEMED_FIELD = "redeemed";
    public static final String VALID_FIELD = "redeemable";
    // [start of field constant]

    private String code;
    //private String project;
    private String participant;
    private double participationPoints;
    private boolean redeemed;
    private boolean redeemable;

    public VerificationCode() {};

    public VerificationCode(String code, double participationPoints) {
        this(code, null, participationPoints, false, true);
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
    //public String getProject() {return project;}
    public double getParticipationPoints() {return participationPoints;}
    public String getParticipant() {return participant;}
    public boolean isRedeemed() {return redeemed;}
    public boolean isRedeemable() {return redeemable;}

    public void setCode(String code) {this.code = code;}
    //public void setProject(String project) {this.project = project;}
    public void setParticipationPoints(double participationPoints) {this.participationPoints = participationPoints;}
    public void setParticipant(String participant) {this.participant = participant;}
    public void setRedeemed(boolean redeemed) {this.redeemed = redeemed;}
    public void setRedeemable(boolean redeemable) {this.redeemable = redeemable;}
}
