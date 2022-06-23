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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private FirebaseAuth mAuth;
    private static final String TAG = "Sign-up";

    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        Button signUpButton = binding.signup;
        loadingProgressBar = binding.loading;

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                SignupActivity.this.signUp(v);
                loadingProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //((Parti) SignupActivity.this.getApplication()).setLoginStatus(true);
            //((Parti) SignupActivity.this.getApplication()).setUser(currentUser);
            goToLoginActivity();
        }
    }

    public void signUp(View view) {
        String username = binding.signupUsername.getText().toString().trim();
        String password = binding.signupPassword.getText().toString();
        String confirmPassword = binding.signupConfirmPassword.getText().toString();
        if (!validateUsernameAndPassword(username, password, confirmPassword)) return;

        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignupActivity.this, "Sign-up successful.",
                                    Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                            updateUser();

                            goToLoginActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Sign-up failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    public void updateUser() {
                        
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


}