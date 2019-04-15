package com2027.killaz.kalorie.gitfit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class RegisterActivity extends AppCompatActivity {

    /**
     * Register activity, allows the user to sign up.
     */
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button mSignUpButton;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private EditText mEditTextNickname;
    private ProgressBar mProgressBar;
    private ArrayList<String> takenNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mSignUpButton = (Button) findViewById(R.id.createAccBtn);
        mEditTextEmail = (EditText) findViewById(R.id.emailField);
        mEditTextPassword = (EditText) findViewById(R.id.passwordField);
        mEditTextNickname = (EditText) findViewById(R.id.nicknameField);
        mProgressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
        takenNames = new ArrayList<>();
        fetchNames();
        createAccount();
    }


    /**
     * Validates inputted fields and then ask for a nickname that is also validated and checks for duplication.
     * Upon successful sign up, user is saved in the Authenticated Users list and the Firestore database and can login.
     */
    private void createAccount() {
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEditTextEmail.getText().toString().trim();
                final String password = mEditTextPassword.getText().toString().trim();

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

                if (!mEditTextNickname.getText().toString().trim().matches("^[a-zA-Z0-9_-]{3,20}$")) {
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Invalid nickname. Nicknames can contain letters, numbers and special symbols (- and _) and must be between 3 and 20 characters", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (takenNames.contains(mEditTextNickname.getText().toString().trim())) {
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Nickname already exists. Try a different one.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mProgressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(mEditTextNickname.getText().toString().trim()).build());
                            createFirestoreUser(mEditTextNickname.getText().toString().trim());
                            mProgressBar.setVisibility(View.GONE);
                            mAuth.getCurrentUser().sendEmailVerification();
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                mProgressBar.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, "You're already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                mProgressBar.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });


            }
        });
    }

    /**
     * Method to create the user in the collection of Users found on the Firestore database.
     *
     * @param username The nickname of the user.
     */
    private void createFirestoreUser(String username) {
        Map<String, Object> docData = new HashMap<>();
        docData.put("calories", 0);
        docData.put("challenges_completed", 0);
        docData.put("points", 0);
        //Blank friends list.
        docData.put("friends", Arrays.asList());
        docData.put("friend_requests", Arrays.asList());
        docData.put("challenge_requests", Arrays.asList());
        docData.put("total_distance_covered", 0);

        //Creates the blank map of friend challenge.
        Map<String, Object> friend_challenge_map = new HashMap<>();
        friend_challenge_map.put("challenge_ref", null);
        friend_challenge_map.put("remaining", null);
        friend_challenge_map.put("user_ref", null);
        docData.put("current_challenge_friend", friend_challenge_map);

        //Creates a personal challenge. Taking the easiest for the start.
        DocumentReference firstChallenge = db.document("Challenges/1");
        Map<String, Object> self_challenge_map = new HashMap<>();
        self_challenge_map.put("challenge_ref", firstChallenge);
        self_challenge_map.put("remaining", 20);
        docData.put("current_challenge_self", self_challenge_map);

        db.collection("Users").document(username)
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegisterActivity.this, "Account created successfully. Please check your email for the authentication email.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Account creation failed, please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Fetches all names from the database in order to make sure the nickname the user chooses is not a duplicate.
     */
    private void fetchNames() {
        db.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    takenNames.add(document.getId());
                }
            }
        });
    }
}
