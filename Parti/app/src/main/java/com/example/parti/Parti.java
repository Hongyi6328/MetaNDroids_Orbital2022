package com.example.parti;

import android.app.Application;

import com.example.parti.wrappers.Major;
import com.example.parti.wrappers.ProjectType;
import com.example.parti.wrappers.User;

// An Application class that is used to deal with global variables
public class Parti extends Application {

    // [start of global constants]
    // directories
    public static final String PROJECT_COLLECTION_PATH = "projects";
    public static final String USER_COLLECTION_PATH = "users";
    public static final String PROJECT_IMAGE_COLLECTION_PATH = "project_images";
    public static final String PROFILE_IMAGE_COLLECTION_PATH = "profile_images";
    public static final String VERIFICATION_CODE_OBJECT_COLLECTION_PATH = "verification_code";
    public static final String VERIFICATION_CODE_ID_COLLECTION_PATH = "verification_code_id";
    public static final String EMAIL_COLLECTION_PATH = "mails";
    public static final String COMMENT_COLLECTION_PATH = "comments";
    public static final String COMMENT_SUBCOLLECTION_PATH = "comments";

    // constants
    public static final String DIVIDER_HEIGHT = "20dp";
    public static final Major[] MAJORS = Major.class.getEnumConstants();
    public static final ProjectType[] PROJECT_TYPES = ProjectType.class.getEnumConstants();
    public static final int SHORT_DESCRIPTION_LENGTH = 300;
    public static final int PROJECT_COMMENT_LENGTH = 2000;

    // request codes
    public static final int PICK_IMAGE_REQUEST_CODE = 1010;
    public static final int SIGN_UP_REQUEST_CODE = 1011;
    public static final int SIGN_UP_SUCCESS_RESULT_CODE = 1012;
    public static final int SIGN_UP_FAILURE_RESULT_CODE = 1013;

    // default values
    public static final int DEFAULT_NUM_ACTIONS_NEEDED = 20;
    public static final double DEFAULT_PP_PER_ACTION = 50.0;
    public static final int EARLIEST_YEAR_OF_MATRIC = 2000;
    public static final int MAX_SELF_DESCRIPTION_LENGTH = 5000;
    public static final int DEFAULT_RANKING = 0;
    public static final String DEFAULT_PROFILE_IMAGE_ID = "profile_images/default_profile_image.jpg";
    public static final String DEFAULT_PROJECT_IMAGE_ID = "";
    public static final String PROJECT_MASK = "mask";
    public static final String DEFAULT_YEAR_OF_MATRIC = "2022";
    public static final long DEFAULT_PARTICIPATION_POINTS = 5000;

    // default strings
    public static final String DEFAULT_EMAIL_SUBJECT = "[Parti.] Your verification code list for the project: ";
    public static final String DEFAULT_EMAIL_TEXT = "Hi! \nThank you for using Parti. This is the list of redeemable verification code of your project.\n";
    public static final String DEFAULT_USER_SELF_DESCRIPTION = "This user hasn't left any self description";
    public static final String DEFAULT_COMMENT_HINT = "This user didn't leave any words.";
    public static final String DEFAULT_USER_ALIAS = "Unknown";
    // [end of global constants]

    private User loggedInUser;

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public User getLoggedInUser() {return this.loggedInUser;}

    public static double calculatePPCost(int numOfParticipants, double _PPperParticipant, double balance) {
        return (1.0 * numOfParticipants * _PPperParticipant - balance) / 10;
    }

    public static double calculatePPRefund(double cost) {
        return - cost / 10;
    }

    //private boolean loginStatus;
    //private FirebaseUser user = null;

    /*
    public boolean getLoginStatus() {
        return loginStatus;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void setLoginStatus(boolean loginStatus) {
        this.loginStatus = loginStatus;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }
     */
}
