package com.example.parti.wrappers;

public class VerificationCode {

    // [start of field constants]
    public static final String CODE_FIELD = "code";
    public static final String PROJECT_FIELD = "project";
    public static final String PARTICIPANT_FIELD = "participant";
    public static final String REDEEMED_FIELD = "redeemed";
    public static final String VALID_FIELD = "valid";
    // [start of field constant]

    private String code;
    private String project;
    private String participant;
    private boolean redeemed;
    private boolean valid;

    public VerificationCode() {};

    public VerificationCode(String code, String project) {
        this(code, project, null, false, true);
    }

    public VerificationCode(String code, String project, String participant, boolean redeemed, boolean valid) {
        this.code = code;
        this.project = project;
        this.participant = participant;
        this.redeemed = redeemed;
        this.valid = valid;
    }

    public String getCode() {return code;}
    public String getProject() {return project;}
    public String getParticipant() {return participant;}
    public boolean isRedeemed() {return redeemed;}
    public boolean isValid() {return valid;}

    public void setCode(String code) {this.code = code;}
    public void setProject(String project) {this.project = project;}
    public void setParticipant(String participant) {this.participant = participant;}
    public void setRedeemed(boolean redeemed) {this.redeemed = redeemed;}
    public void setValid(boolean valid) {this.valid = valid;}
}
