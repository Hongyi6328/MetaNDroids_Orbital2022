package com.example.parti.ui.login;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parti.Parti;
import com.example.parti.databinding.ActivitySignupBinding;
import com.example.parti.wrappers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "signup-activity";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)");
    //1 number, 1 letter and 1 special char

    private ActivitySignupBinding activitySignupBinding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private boolean success = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(activitySignupBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        success = false;

        activitySignupBinding.buttonSignup.setOnClickListener(v -> {
            signUp();
        });

        activitySignupBinding.buttonSignupResendVerificationEmail.setClickable(false);
        activitySignupBinding.buttonSignupResendVerificationEmail.setVisibility(View.INVISIBLE);
        activitySignupBinding.buttonSignupResendVerificationEmail.setOnClickListener(v -> sendVerificationEmail(firebaseAuth.getCurrentUser()));

        activitySignupBinding.buttonSignupEmailVerified.setClickable(false);
        activitySignupBinding.buttonSignupEmailVerified.setVisibility(View.INVISIBLE);
        activitySignupBinding.buttonSignupEmailVerified.setOnClickListener(v -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            user.reload().addOnSuccessListener(unused -> {
                if (user.isEmailVerified()) {
                    Toast.makeText(SignupActivity.this, "Email verified!", Toast.LENGTH_LONG).show();

                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String uuid = firebaseUser.getUid();
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    DocumentReference documentReference =
                            firebaseFirestore.collection(Parti.USER_COLLECTION_PATH).document(uuid);
                    documentReference.get().addOnSuccessListener(documentSnapshot -> {
                        User loggedInUser = documentSnapshot.toObject(User.class);
                        ((Parti) SignupActivity.this.getApplication()).setLoggedInUser(loggedInUser);
                    });
                    success = true;
                    goToLoginActivity();
                } else {
                    Toast.makeText(SignupActivity.this, "Email not verified. \nPlease also check your junk mail box.", Toast.LENGTH_LONG).show();
                }
            });
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            success = true;
            goToLoginActivity();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        int resultCode = success ? Parti.SIGN_UP_SUCCESS_RESULT_CODE : Parti.SIGN_UP_FAILURE_RESULT_CODE;
        if (!success) {
            firebaseAuth.signOut();
            ((Parti) getApplication()).setLoggedInUser(null);
        }
        setResult(resultCode);
    }

    private void signUp() {
        String username = activitySignupBinding.inputSignupUsername.getText().toString().trim();
        String password = activitySignupBinding.inputSignupPassword.getText().toString();
        String confirmPassword = activitySignupBinding.inputSignupConfirmPassword.getText().toString();
        if (!validateUsernameAndPassword(username, password, confirmPassword)) return;

        Task<AuthResult> task = firebaseAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task1 -> {
                    if (task1.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Toast.makeText(SignupActivity.this, "Sign-up successful.",
                                Toast.LENGTH_LONG).show();

                        // Prevent user from signing up another account before verified
                        activitySignupBinding.inputSignupUsername.setEnabled(false);
                        activitySignupBinding.inputSignupPassword.setEnabled(false);
                        activitySignupBinding.inputSignupConfirmPassword.setEnabled(false);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task1.getException());
                        Toast.makeText(SignupActivity.this, "Sign-up failed.",
                                Toast.LENGTH_LONG).show();
                    }
                });

        task
                .onSuccessTask(authResult -> {
                    FirebaseUser signedUser = firebaseAuth.getCurrentUser();
                    User newUser = new User(signedUser.getUid(), signedUser.getEmail());
                    ((Parti) getApplication()).setLoggedInUser(newUser);
                    sendVerificationEmail(signedUser);
                    return firebaseFirestore.collection(Parti.USER_COLLECTION_PATH).document(signedUser.getUid()).set(newUser)
                            .addOnFailureListener(e ->
                                    Toast.makeText(SignupActivity.this, "Failed to update user", Toast.LENGTH_LONG).show());
                });
    }

    private boolean validateUsernameAndPassword(String username, String password, String confirmPassword) {
        if (!isUsernameValid(username)) {
            handleInvalidUsername();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            handleUnmatchedPasswords();
            return false;
        }
        if (!isPasswordValid(password)) {
            handleInvalidPassword();
            return false;
        }
        return true;
    }

    // A placeholder username validation check
    private boolean isUsernameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        }
        return false;
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        if (password == null) return false;
        return password.trim().length() >= 8 && PASSWORD_PATTERN.matcher(password).matches();
    }

    private void handleInvalidUsername() {
        Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_LONG)
                .show();
    }

    private void handleUnmatchedPasswords() {
        Toast.makeText(getApplicationContext(), "The two passwords do not match", Toast.LENGTH_LONG)
                .show();
    }

    private void handleInvalidPassword() {
        Toast.makeText(getApplicationContext(),
                        "The password has to be at least 8 characters long \nwith at least 1 uppercase letter, 1 digit, and 1 special character",
                        Toast.LENGTH_LONG)
                .show();
    }

    private void goToLoginActivity() {
        finish();
    }
    
    private void sendVerificationEmail(FirebaseUser user) {
        activitySignupBinding.buttonSignup.setClickable(false);
        activitySignupBinding.buttonSignupEmailVerified.setVisibility(View.VISIBLE);
        activitySignupBinding.buttonSignupEmailVerified.setClickable(true);
        activitySignupBinding.buttonSignupResendVerificationEmail.setVisibility(View.VISIBLE);
        activitySignupBinding.buttonSignupResendVerificationEmail.setClickable(false);
        user.sendEmailVerification().addOnCompleteListener((OnCompleteListener<Void>) task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignupActivity.this,
                        "Verification email sent to " + user.getEmail() + "  Please also check your junk mail box.",
                        Toast.LENGTH_LONG).show();
            } else {
                Log.e(TAG, "sendEmailVerification", task.getException());
                Toast.makeText(SignupActivity.this,
                        "Failed to send verification email.",
                        Toast.LENGTH_LONG).show();
            }
            activitySignupBinding.buttonSignupResendVerificationEmail.setClickable(true);
        });
    }
}