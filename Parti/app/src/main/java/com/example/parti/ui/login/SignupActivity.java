package com.example.parti.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.parti.Parti;
import com.example.parti.databinding.ActivitySignupBinding;
import com.example.parti.wrappers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding activitySignupBinding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private static final String TAG = "Sign-up";

    public static final String USER_COLLECTION_PATH = Parti.USER_COLLECTION_PATH;

    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(activitySignupBinding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();

        Button signUpButton = activitySignupBinding.signup;
        loadingProgressBar = activitySignupBinding.loading;

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                SignupActivity.this.signUp(v);
                loadingProgressBar.setVisibility(View.INVISIBLE);
            }
        });

        activitySignupBinding.buttonResendVerificationEmail.setClickable(false);
        activitySignupBinding.buttonResendVerificationEmail.setVisibility(View.INVISIBLE);
        activitySignupBinding.buttonResendVerificationEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationEmail(firebaseAuth.getCurrentUser());
            }
        });

        activitySignupBinding.buttonVerified.setClickable(false);
        activitySignupBinding.buttonVerified.setVisibility(View.INVISIBLE);
        activitySignupBinding.buttonVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (user.isEmailVerified()) {
                            Toast.makeText(SignupActivity.this, "Email verified!", Toast.LENGTH_LONG).show();
                            FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
                                @Override
                                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                    if (firebaseAuth.getCurrentUser() == null) {
                                        goToLoginActivity();
                                    }
                                }
                            };
                            firebaseAuth.addAuthStateListener(authStateListener);
                            firebaseAuth.signOut();

                        } else {
                            Toast.makeText(SignupActivity.this, "Email not verified. Please also check your spam box.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            //((Parti) SignupActivity.this.getApplication()).setLoginStatus(true);
            //((Parti) SignupActivity.this.getApplication()).setUser(currentUser);
            goToLoginActivity();
        }
    }

    public void signUp(View view) {
        String username = activitySignupBinding.signupUsername.getText().toString().trim();
        String password = activitySignupBinding.signupPassword.getText().toString();
        String confirmPassword = activitySignupBinding.signupConfirmPassword.getText().toString();
        if (!validateUsernameAndPassword(username, password, confirmPassword)) return;

        Task<AuthResult> task = firebaseAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(SignupActivity.this, "Sign-up successful.",
                                    Toast.LENGTH_LONG).show();

                            // Prevent user from signing up another account before verified
                            activitySignupBinding.signupUsername.setEnabled(false);
                            activitySignupBinding.signupPassword.setEnabled(false);
                            activitySignupBinding.signupConfirmPassword.setEnabled(false);

                            //updateUser(user);

                            firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                                @Override
                                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if ((user != null) && user.isEmailVerified()) {
                                        firebaseAuth.signOut();
                                        goToLoginActivity();
                                    }
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Sign-up failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

        task
                .onSuccessTask(new SuccessContinuation<AuthResult, Void>() {
                    @NonNull
                    @Override
                    public Task<Void> then(AuthResult authResult) throws Exception {
                        FirebaseUser signedUser = firebaseAuth.getCurrentUser();
                        User newUser = new User(signedUser.getUid(), signedUser.getEmail());
                        sendVerificationEmail(signedUser);
                        return firebaseFirestore.collection(USER_COLLECTION_PATH).document(signedUser.getUid()).set(newUser)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(SignupActivity.this, "Updated user successfully",
                                                Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignupActivity.this, "Failed to update user",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });
    }

    private boolean validateUsernameAndPassword(String username, String password, String confirmPassword) {
        if (!isUserNameValid(username)) {
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
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        }
        //else {
        //    return !username.trim().isEmpty();
        //}
        return false;
    }


    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        if (password == null) return false;
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$");
        //1 number, 1 letter and 1 special char

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
                        "The password has to be at least 8 characters long with at least 1 uppercase letter, 1 digit, and 1 special character",
                        Toast.LENGTH_LONG)
                .show();
    }

    private void goToLoginActivity() {
        finish();
    }

    /*
    public void updateUser(FirebaseUser signedUser) {
        User newUser = new User(signedUser.getUid(), signedUser.getEmail());
        firebaseFirestore.collection(USER_COLLECTION_PATH).document(signedUser.getUid()).set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(SignupActivity.this, "Updated user successfully",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupActivity.this, "Failed to update user",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
     */
    
    public void sendVerificationEmail(FirebaseUser user) {
        activitySignupBinding.signup.setClickable(false);
        activitySignupBinding.buttonVerified.setVisibility(View.VISIBLE);
        activitySignupBinding.buttonVerified.setClickable(true);
        activitySignupBinding.buttonResendVerificationEmail.setVisibility(View.VISIBLE);
        activitySignupBinding.buttonResendVerificationEmail.setClickable(false);
        user.sendEmailVerification().addOnCompleteListener(SignupActivity.this,
                new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        // Re-enable button
                        //findViewById(R.id.verify_email_button).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(SignupActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_LONG).show();
                        }
                        activitySignupBinding.buttonResendVerificationEmail.setClickable(true);
                    }
                });
    }
}