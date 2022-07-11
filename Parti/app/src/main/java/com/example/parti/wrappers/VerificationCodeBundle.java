package com.example.parti.wrappers;

import java.util.List;

public class VerificationCodeBundle {
    // [start of field constants]
    public static final String PROJECT_FIELD = "project";
    public static final String VERIFICATION_CODE_LIST_FIELD = "verificationCodeList";
    // [end of field constants]

    private String project;
    private List<VerificationCode> verificationCodeList;

    public VerificationCodeBundle() {}

    public VerificationCodeBundle(String project, List<VerificationCode> verificationCodeList) {
        this.project = project;
        this.verificationCodeList = verificationCodeList;
    }

    public String getProject() {return project;}
    public List<VerificationCode> getVerificationCodeList() {return verificationCodeList;}

    public void setProject(String project) {this.project = project;}
    public void setVerificationCodeList(List<VerificationCode> verificationCodeList) {this.verificationCodeList = verificationCodeList;}
}
