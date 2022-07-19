package com.example.parti.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parti.Parti;
import com.example.parti.databinding.ActivityLoginBinding;
import com.example.parti.ui.main.MainActivity;
import com.example.parti.wrappers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "login-activity";
    // private static final String USER_COLLECTION_PATH = Parti.USER_COLLECTION_PATH;

    //LoginViewModel is no longer used
    //private LoginViewModel loginViewModel;
    private ActivityLoginBinding activityLoginBinding;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        activityLoginBinding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityLoginBinding.progressBarLogin.setVisibility(View.VISIBLE);
                LoginActivity.this.login();
                //loadingProgressBar.setVisibility(View.INVISIBLE);
            }
        });

        // the following block of code is replaced by goToSignup()
        activityLoginBinding.buttonGoToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSignupIntent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivityForResult(goToSignupIntent, Parti.SIGN_UP_REQUEST_CODE);
            }
        });

        activityLoginBinding.buttonLoginResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = activityLoginBinding.inputSigninUsername.getText().toString();
                if (!isUsernameValid(username)) {
                    handleInvalidUsername();
                    return;
                }
                firebaseAuth.sendPasswordResetEmail(username).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(LoginActivity.this, "Reset email sent.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        //loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
        //        .get(LoginViewModel.class);

        /*
        final EditText usernameEditText = activityLoginBinding.signinUsername;
        final EditText passwordEditText = activityLoginBinding.signinPassword;
        final Button loginButton = activityLoginBinding.login;
        final Button goToSignupButton = activityLoginBinding.goToSignup;
        final ProgressBar loadingProgressBar = activityLoginBinding.loading;
         */

        /*
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }

                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        */
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Parti.SIGN_UP_SUCCESS_RESULT_CODE) {
            goToMainActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        //if (currentUser != null) {
            //((Parti) LoginActivity.this.getApplication()).setLoginStatus(true);
            //((Parti) LoginActivity.this.getApplication()).setUser(currentUser);
            //goToMainActivity();
        //}
    }

    /*
    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }
    */

    /*
    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
    }
     */

    private void login() {
        String username = activityLoginBinding.inputSigninUsername.getText().toString().trim();
        String password = activityLoginBinding.inputSigninPassword.getText().toString();

        firebaseAuth.signOut();

        if (!validateUsernameAndPassword(username, password)) return;

        firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInUserWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Login successful.",
                                    Toast.LENGTH_LONG).show();

                            //((Parti) LoginActivity.this.getApplication()).setLoginStatus(true);
                            //((Parti) LoginActivity.this.getApplication()).setUser(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Login failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .onSuccessTask(new SuccessContinuation<AuthResult, DocumentSnapshot>() {
                    @NonNull
                    @Override
                    public Task<DocumentSnapshot> then(AuthResult authResult) throws Exception {
                        String uid = authResult.getUser().getUid();
                        return FirebaseFirestore
                                .getInstance()
                                .collection(Parti.USER_COLLECTION_PATH)
                                .document(uid)
                                .get();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = (User) documentSnapshot.toObject(User.class);
                        ((Parti) LoginActivity.this.getApplication()).setLoggedInUser(user);
                        goToMainActivity();
                    }
                });
    }

    private boolean validateUsernameAndPassword(String username, String password) {
        if (!isUsernameValid(username)) {
            handleInvalidUsername();
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

    private void handleInvalidPassword() {
        Toast.makeText(getApplicationContext(),
                        "The password has to be at least 8 characters long with at least 1 letter, 1 digit, and 1 special character",
                        Toast.LENGTH_LONG)
                .show();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        CompletableFuture.runAsync(() -> startActivity(intent))
                .thenRun(() -> activityLoginBinding.progressBarLogin.setVisibility(View.GONE));
        //setResult();
        //finish();
    }
}