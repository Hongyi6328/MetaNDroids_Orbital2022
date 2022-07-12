package com.example.parti.wrappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Deprecated
public class VerificationCodeBundleBox {
    // [start of field constants]
    public static final String PROJECT_ID_FIELD = "projectId";
    public static final String NUM_REDEEMED_FIELD = "numRedeemed";
    public static final String NUM_REDEEMABLE_FIELD = "numRedeemable";
    public static final String VERIFICATION_CODE_LIST_FIELD = "verificationCodeList";
    // [end of field constants]

    private String projectId;
    private int numRedeemed;
    private int numRedeemable;
    private List<Map<String, Object>> verificationCodeList;

    public VerificationCodeBundleBox() {}

    public VerificationCodeBundleBox(String projectId) {
        this(projectId, new ArrayList<>());
    }

    public VerificationCodeBundleBox(String projectId, List<Map<String, Object>> verificationCodeList) {
        this(projectId, 0, 0, verificationCodeList);
    }

    public VerificationCodeBundleBox(String projectId, int numRedeemed, int numRedeemable, List<Map<String, Object>> verificationCodeList) {
        this.projectId = projectId;
        this.numRedeemed = numRedeemed;
        this.numRedeemable = numRedeemable;
        this.verificationCodeList = verificationCodeList;
    }

    public String getProjectId() {return projectId;}
    public int getNumRedeemed() {return numRedeemed;}
    public int getNumRedeemable() {return numRedeemable;}
    public List<Map<String, Object>> getVerificationCodeList() {return verificationCodeList;}

    public void setProjectId(String projectId) {this.projectId = projectId;}
    public void setNumRedeemed(int numRedeemed) {this.numRedeemed = numRedeemed;}
    public void setNumRedeemable(int numRedeemable) {this.numRedeemable = numRedeemable;}
    public void setVerificationCodeList(List<Map<String, Object>> verificationCodeList) {this.verificationCodeList = verificationCodeList;}

    public VerificationCodeBundle convertToCustomObject() {
        List<VerificationCode> verificationCodeList = new ArrayList<>();
        VerificationCodeBundle verificationCodeBundle = new VerificationCodeBundle(projectId, numRedeemed, numRedeemable, verificationCodeList);
        for (Map<String, Object> map: this.verificationCodeList) {
            VerificationCode verificationCode = VerificationCode.mapToVerificationCode(map);
            verificationCodeList.add(verificationCode);
        }
        return verificationCodeBundle;
    }
}
