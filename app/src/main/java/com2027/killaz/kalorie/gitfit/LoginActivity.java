package com2027.killaz.kalorie.gitfit;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Login activity, allows the user to sign in or request a password reset.
 * Using shared preferences also allows to save login information for when the app is re-opened.
 */
public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button mLoginButton;
    private Button mSignUpButton;
    private TextView mForgotPassword;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private CheckBox mRememberCheckBox;
    private SharedPreferences mPrefs = null;
    private ProgressBar mProgressBar;

    private static final String CHANNEL_ID = "NOTIFICATION_CHANNEL_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkLocationPermission();
        mAuth = FirebaseAuth.getInstance();
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mSignUpButton = (Button) findViewById(R.id.signUpButton);
        mForgotPassword = (TextView) findViewById(R.id.forgotPassView);
        mEditTextEmail = (EditText) findViewById(R.id.emailField);
        mEditTextPassword = (EditText) findViewById(R.id.passwordField);
        mRememberCheckBox = (CheckBox) findViewById(R.id.rememberCheckBox);
        mProgressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
        mPrefs = getSharedPreferences(getString(R.string.shared_pref), MODE_PRIVATE);
        if (mPrefs.getBoolean("rememberMe", true)) {
            String rememberedEmail = mPrefs.getString("rememberedEmail", null);
            String rememberedPassword = mPrefs.getString("rememberedPass", null);
            mEditTextEmail.setText(rememberedEmail);
            mEditTextPassword.setText(rememberedPassword);
            mRememberCheckBox.setChecked(true);
        }
        ImageView img = (ImageView) findViewById(R.id.login_img);
        createNotificationChannel();
        userLogin();
        forgotPasswordDialog();
        signUpActivity();
    }

    /**
     * Validates username and password and checks if the user exists in firebase and log them in.
     * If any conditions are not met, such as empty or invalid fields or the account doesn't exist, the appropriate message is shown.
     */
    private void userLogin() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEditTextEmail.getText().toString().trim();
                String password = mEditTextPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    mEditTextEmail.setError("Email is required");
                    mEditTextEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEditTextEmail.setError("Please enter a valid email");
                    mEditTextEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    mEditTextPassword.setError("Password is required");
                    mEditTextPassword.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    mEditTextPassword.setError("Minimum length of password should be 6");
                    mEditTextPassword.requestFocus();
                    return;
                }
                if (mRememberCheckBox.isChecked()) {
                    mPrefs.edit().putBoolean("rememberMe", true).apply();
                    mPrefs.edit().putString("rememberedEmail", email).apply();
                    mPrefs.edit().putString("rememberedPass", password).apply();
                } else if (!mRememberCheckBox.isChecked()) {
                    mPrefs.edit().putBoolean("rememberMe", false).apply();
                }
                mProgressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                Intent mainMenu = new Intent(LoginActivity.this, MainMenu.class);
                                startActivity(mainMenu);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Your email is not verified. Please go to your email to verify it.", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid details or no connection. Please Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    /**
     * If email entered in the alert dialog exists in firebase, a reset email is sent. Appropriate validation is used.
     */
    private void forgotPasswordDialog() {
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Reset Password");
                final EditText emailInput = new EditText(LoginActivity.this);
                emailInput.setHint("Enter email of forgotten account.");
                emailInput.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(emailInput);
                builder.setPositiveButton("Send Reset Instructions", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String email = emailInput.getText().toString().trim();
                        if (email.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Please type an email", Toast.LENGTH_SHORT).show();
                        } else {
                            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Password reset instructions sent", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Invalid email entered. Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });

    }

    /**
     * Send the user to the sign up activity to create an account.
     */
    private void signUpActivity() {
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivity = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerActivity);
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, for API 26+ only
        // Older versions do not require this function
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    99);
        }

    }

}
