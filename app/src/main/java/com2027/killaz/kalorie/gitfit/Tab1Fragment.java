package com2027.killaz.kalorie.gitfit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Map;

public class Tab1Fragment extends Fragment {
    View v;
    private ProgressBar challengeBar;
    private int challengeTotal;
    private int challengeRemaining;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser;
    private DocumentReference userRef;
    private TextView infoText, progressBarTxt;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = inflater.inflate(R.layout.tab1_head, container, false);
        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        challengeBar = (ProgressBar) getView().findViewById(R.id.challenges_persona_progress_bar);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRef = db.collection("Users").document(currentUser.getDisplayName());
        infoText = getView().findViewById(R.id.challenges_personal_challenge_info);
        progressBarTxt = getView().findViewById(R.id.challenges_personal_fetching_view);
        fetchUserChallenge();
    }

    private void fetchUserChallenge() {
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    Map<String, Object> challenge_self = (Map<String, Object>) documentSnapshot.get("current_challenge_self");
                    challengeRemaining = Integer.parseInt(challenge_self.get("remaining").toString());
                    DocumentReference challengeRef = (DocumentReference) challenge_self.get("challenge_ref");
                    fetchChallengeName(challengeRef);
                }
            }
        });
    }

    private void fetchChallengeName(DocumentReference theChallenge) {
        theChallenge.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                             @Override
                                             public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                                 String challenge_type = documentSnapshot.getString("type");
                                                 if (challenge_type.equals("distance")) {
                                                     challengeTotal = documentSnapshot.getLong("distance").intValue();
                                                     infoText.setText("Travel a distance of " + challengeTotal + " metres!");
                                                 } else {
                                                     challengeTotal = documentSnapshot.getLong("steps").intValue();
                                                     infoText.setText("Travel a distance of " + challengeTotal + " steps!");
                                                 }
                                             }
                                         }
        );
//        db.collection(theChallenge).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                for (DocumentSnapshot documentSnapshot : task.getResult()) {
//                    String challenge_type = documentSnapshot.getString("type");
//                    if (challenge_type.equals("distance")) {
//                        challengeTotal = documentSnapshot.getLong("distance").intValue();
//                        infoText.setText("Travel a distance of " + challengeTotal + " metres!");
//                    } else {
//                        challengeTotal = documentSnapshot.getLong("steps").intValue();
//                        infoText.setText("Travel a distance of " + challengeTotal + " steps!");
//                    }
//                }
//            }
//        });
        updateProgressBar();
    }

    private void updateProgressBar() {
        int soFar = challengeTotal - challengeRemaining;
        if (challengeTotal > 0 && challengeRemaining < challengeTotal) {
            progressBarTxt.setText(getResources().getString(R.string.your_progress, soFar, challengeTotal));
            int progress = (int) ((soFar * 100.0f) / challengeTotal);
            ProgressBarAnimation animate = new ProgressBarAnimation(challengeBar, 0, progress);
            animate.setDuration(1000);
            challengeBar.startAnimation(animate);
        }
    }
}

