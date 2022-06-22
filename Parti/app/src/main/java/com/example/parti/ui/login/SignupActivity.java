package com.example.parti.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.R;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.parti.databinding.ActivityMainBinding;
import com.example.parti.databinding.ActivitySignupBinding;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void signUp(View view) {
        String username = binding.signupUsername.getText().toString().trim();
        String password = binding.signupPassword.getText().toString();
        String confirmPassword = binding.signupConfirmPassword.getText().toString();
        if (!validateUsernameAndPassword(username, password, confirmPassword)) return;
        
    }

    private boolean validateUsernameAndPassword(String username, String password, String confirmPassword) {
        if (isUserNameValid(username)) {
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
                        "The password has to be at least 8 characters long with at least 1 letter, 1 digit, and 1 special character",
                        Toast.LENGTH_LONG)
                .show();
    }
}