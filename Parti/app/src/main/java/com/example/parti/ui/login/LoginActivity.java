package com.example.parti.ui.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.parti.Parti;
import com.example.parti.adapters.CommentHolder;
import com.example.parti.databinding.ActivityLoginBinding;
import com.example.parti.ui.main.MainActivity;
import com.example.parti.wrappers.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "login-activity";

    private ActivityLoginBinding activityLoginBinding;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        loadLogo();

        activityLoginBinding.buttonLogin.setOnClickListener(v -> {
            activityLoginBinding.progressBarLogin.setVisibility(View.VISIBLE);
            LoginActivity.this.login();
        });

        activityLoginBinding.buttonGoToSignup.setOnClickListener(v -> {
            Intent goToSignupIntent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivityForResult(goToSignupIntent, Parti.SIGN_UP_REQUEST_CODE);
        });

        activityLoginBinding.buttonLoginResetPassword.setOnClickListener(v -> {
            String username = activityLoginBinding.inputSigninUsername.getText().toString();
            if (!isUsernameValid(username)) {
                handleInvalidUsername();
                return;
            }
            firebaseAuth.sendPasswordResetEmail(username)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(
                                    LoginActivity.this,
                                    "Reset email sent. Please check your junk mail box as well.",
                                    Toast.LENGTH_LONG)
                                    .show())
                    .addOnFailureListener(exception ->
                            Toast.makeText(
                                    LoginActivity.this,
                                    "Account not found.",
                                    Toast.LENGTH_LONG)
                                    .show());
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Parti.SIGN_UP_SUCCESS_RESULT_CODE) {
            goToMainActivity();
        }
    }

    private void login() {
        String username = activityLoginBinding.inputSigninUsername.getText().toString().trim();
        String password = activityLoginBinding.inputSigninPassword.getText().toString();

        firebaseAuth.signOut();

        if (!validateUsernameAndPassword(username, password)) {
            activityLoginBinding.progressBarLogin.setVisibility(View.GONE);
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInUserWithEmail:success");
                        Toast.makeText(LoginActivity.this, "Login successful.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Login failed.",
                                Toast.LENGTH_LONG).show();
                        activityLoginBinding.progressBarLogin.setVisibility(View.GONE);
                    }
                })
                .onSuccessTask(authResult -> {
                    String uid = authResult.getUser().getUid();
                    return FirebaseFirestore
                            .getInstance()
                            .collection(Parti.USER_COLLECTION_PATH)
                            .document(uid)
                            .get();
                })
                .addOnSuccessListener(documentSnapshot -> {
                    User user = (User) documentSnapshot.toObject(User.class);
                    ((Parti) LoginActivity.this.getApplication()).setLoggedInUser(user);
                    goToMainActivity();
                })
                .addOnFailureListener(e -> {
                    activityLoginBinding.progressBarLogin.setVisibility(View.GONE);
                    ((Parti) LoginActivity.this.getApplication()).setLoggedInUser(null);
                });
    }

    private boolean validateUsernameAndPassword(String username, String password) {
        if (!isUsernameValid(username)) {
            handleInvalidUsername();
            return false;
        }
        /*
        if (!isPasswordValid(password)) {
            handleInvalidPassword();
            return false;
        }
        */
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

    private void handleInvalidPassword() {
        Toast.makeText(getApplicationContext(),
                        "At least 8 characters with 1 uppercase, 1 digit, and 1 special character",
                        Toast.LENGTH_LONG)
                .show();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        activityLoginBinding.progressBarLogin.setVisibility(View.GONE);
    }

    private void loadLogo() {
        String imageId = User.DEFAULT_PROFILE_IMAGE_ID;
        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child(imageId);
        final long ONE_MEGABYTE = 1024 * 1024;
        imageReference
                .getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(bytes -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    activityLoginBinding.imageLogin.setImageBitmap(bitmap);
                });
    }
}