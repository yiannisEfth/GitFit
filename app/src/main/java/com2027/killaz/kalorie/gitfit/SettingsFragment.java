package com2027.killaz.kalorie.gitfit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class SettingsFragment extends Fragment {

    private FirebaseUser currentUser;
    private TextView mPoints, mEmail, mNickname, mChallengesCompleted;
    private Button mChangePass, mDeleteAccount;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPoints = (TextView) getView().findViewById(R.id.points_field);
        mEmail = (TextView) getView().findViewById(R.id.email_field);
        mNickname = (TextView) getView().findViewById(R.id.nickname_field);
        mChallengesCompleted = (TextView) getView().findViewById(R.id.challenges_field);
        mChangePass = (Button) getView().findViewById(R.id.change_password_btn);
        mDeleteAccount = (Button) getView().findViewById(R.id.delete_acc_btn);
        db = FirebaseFirestore.getInstance();
        mEmail.setText(currentUser.getEmail());
        mNickname.setText(currentUser.getDisplayName());
        setupButtons();
        setValues();
    }

    private void setValues() {
        db.collection("Users").document(currentUser.getDisplayName()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    int points = documentSnapshot.getLong("points").intValue();
                    int challengesCompleted = documentSnapshot.getLong("challenges_completed").intValue();
                    mPoints.setText(String.valueOf(points));
                    mChallengesCompleted.setText(String.valueOf(challengesCompleted));
                }
            }
        });
    }

    private void setupButtons() {
        mChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Change Password");
                final EditText passwordInput = new EditText(getActivity());
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordInput.setHint("Please type a new password");
                builder.setView(passwordInput);
                builder.setPositiveButton("Change Password", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (passwordInput.getText().toString().length() < 6) {
                            Toast.makeText(getActivity(), "Password is not valid. Please try again", Toast.LENGTH_SHORT).show();
                        } else {
                            currentUser.updatePassword(passwordInput.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Password changed successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "An error occurred please try again", Toast.LENGTH_SHORT).show();
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


        mDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete Account?");
                builder.setMessage("Are you sure? You cannot undo this action");
                builder.setPositiveButton("Yes, I am sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("Users").document(currentUser.getDisplayName()).delete();
                        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Account successfully deleted. Returning to login screen", Toast.LENGTH_SHORT).show();
                                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(loginIntent);
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(getActivity(), "An error has occurred", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });

    }
}