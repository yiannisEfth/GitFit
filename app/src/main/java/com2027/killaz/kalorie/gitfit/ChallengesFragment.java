package com2027.killaz.kalorie.gitfit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChallengesFragment extends Fragment {

    private ProgressBar personalChallengeBar, friendChallengeBar;
    private int personalChallengeTotal;
    private int personalChallengeRemaining;
    private int friendChallengeTotal;
    private int friendChallengeRemaining;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser;
    private DocumentReference userRef;
    private Button challengeFriendBtn;
    private TextView personalInfoTxt, personalProgressBarTxt, friendInfoTxt, friendProgressBarTxt, noChallengesText;
    private Map<String, Object> challenge_friend;
    private List<String> friends;
    private List<String> challengerNames;
    private ListView challengeRequests;
    private ArrayAdapter<String> adapter;
    private Context mContext;
    private boolean hasFriendChallenge;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.challenges_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        personalChallengeBar = (ProgressBar) getView().findViewById(R.id.challenges_persona_progress_bar);
        friendChallengeBar = (ProgressBar) getView().findViewById(R.id.challenges_friend_progress_bar);
        personalInfoTxt = (TextView) getView().findViewById(R.id.challenges_personal_challenge_info);
        personalProgressBarTxt = (TextView) getView().findViewById(R.id.challenges_personal_fetching_view);
        friendInfoTxt = (TextView) getView().findViewById(R.id.challenges_friend_challenge_info);
        friendProgressBarTxt = (TextView) getView().findViewById(R.id.challenges_friend_fetching_view);
        challengeFriendBtn = (Button) getView().findViewById(R.id.challenges_challenge_btn);
        challengeRequests = (ListView) getView().findViewById(R.id.challenge_reqs_list);
        noChallengesText = (TextView) getView().findViewById(R.id.challenges_ohnoes);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRef = db.collection("Users").document(currentUser.getDisplayName());

        challengerNames = new ArrayList<>();

        fetchUserChallenges();
        fetchChallengeRequests();
        setupChallengeBtn();
        setupChallengeAcceptance();
    }

    private void fetchUserChallenges() {
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    Map<String, Object> challenge_self = (Map<String, Object>) documentSnapshot.get("current_challenge_self");
                    challenge_friend = (Map<String, Object>) documentSnapshot.get("current_challenge_friend");
                    personalChallengeRemaining = Integer.parseInt(challenge_self.get("remaining").toString());
                    if (challenge_friend.get("challenge_ref") != null) {
                        friendChallengeRemaining = Integer.parseInt(challenge_friend.get("remaining").toString());
                        hasFriendChallenge = true;
                    }
                    else {
                        hasFriendChallenge = false;
                    }
                    DocumentReference friendChallengeRef = (DocumentReference) challenge_friend.get("challenge_ref");
                    fetchFriendChallengeName(friendChallengeRef);
                    if (friendChallengeRef != null) {
                        updateFriendProgressBar();
                    }
                    DocumentReference personalChallengeRef = (DocumentReference) challenge_self.get("challenge_ref");
                    fetchPersonalChallengeName(personalChallengeRef);
                    updatePersonalProgressBar();
                }
            }
        });
    }

    private void fetchPersonalChallengeName(DocumentReference theChallenge) {
        theChallenge.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                             @Override
                                             public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                                 String challenge_type = documentSnapshot.getString("type");
                                                 if (challenge_type.equals("distance")) {
                                                     personalChallengeTotal = documentSnapshot.getLong("distance").intValue();
                                                     personalInfoTxt.setText("Travel a distance of " + personalChallengeTotal + " metres!");
                                                     updatePersonalProgressBar();
                                                 } else {
                                                     personalChallengeTotal = documentSnapshot.getLong("steps").intValue();
                                                     personalInfoTxt.setText("Travel a distance of " + personalChallengeTotal + " steps!");
                                                     updatePersonalProgressBar();
                                                 }
                                             }
                                         }
        );
    }

    private void updatePersonalProgressBar() {
        int soFar = personalChallengeTotal - personalChallengeRemaining;
        if (personalChallengeTotal > 0 && personalChallengeRemaining < personalChallengeTotal) {
            personalProgressBarTxt.setText( "Your Progress:" + soFar + " / " + personalChallengeTotal);
            int progress = (int) ((soFar * 100.0f) / personalChallengeTotal);
            ProgressBarAnimation animate = new ProgressBarAnimation(personalChallengeBar, 0, progress);
            animate.setDuration(1000);
            personalChallengeBar.startAnimation(animate);
        }
    }

    private void fetchFriendChallengeName(DocumentReference theChallenge) {
        if (theChallenge == null) {
            friendInfoTxt.setText("You don't have a challenge from a friend currently active :(");
        } else {
            theChallenge.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                 @Override
                                                 public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                                     String challenge_type = documentSnapshot.getString("type");
                                                     if (challenge_type.equals("distance")) {
                                                         friendChallengeTotal = documentSnapshot.getLong("distance").intValue();
                                                         friendInfoTxt.setText("Challenged by " + challenge_friend.get("user_ref") + " to travel a distance of " + friendChallengeTotal + " metres!");
                                                         updateFriendProgressBar();
                                                     } else {
                                                         friendChallengeTotal = documentSnapshot.getLong("steps").intValue();
                                                         friendInfoTxt.setText("Challenged by " + challenge_friend.get("user_ref") + " to travel a distance of " + friendChallengeTotal + " steps!");
                                                        updateFriendProgressBar();
                                                     }
                                                 }
                                             }
            );
        }
    }

    private void updateFriendProgressBar() {
        int soFar = friendChallengeTotal - friendChallengeRemaining;
        if (friendChallengeTotal > 0 && friendChallengeRemaining < friendChallengeTotal) {
            friendProgressBarTxt.setText(getResources().getString(R.string.your_progress, soFar, friendChallengeTotal));
            int progress = (int) ((soFar * 100.0f) / friendChallengeTotal);
            ProgressBarAnimation animate = new ProgressBarAnimation(friendChallengeBar, 0, progress);
            animate.setDuration(1000);
            friendChallengeBar.startAnimation(animate);
        }
    }

    private void setupChallengeBtn() {
        challengeFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            friends = (List<String>) task.getResult().get("friends");
                            CharSequence[] cs = friends.toArray(new CharSequence[friends.size()]);
                            initiateDialog(cs);
                        } else {
                            Toast.makeText(getContext(), "Error! Cannot Fetch Friend List", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void initiateDialog(CharSequence[] cs) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Select A Friend To Challenge!")
                .setItems(cs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        validateChallengeReq(friends.get(i));
                    }
                });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.create().show();
    }

    private void validateChallengeReq(final String toChallenge) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Challenge Request")
                .setMessage("Send challenge request to " + toChallenge + "?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("Users").document(toChallenge).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    List<String> currentsChallengeReqs = (List<String>) task.getResult().get("challenge_requests");
                                    if (currentsChallengeReqs != null) {
                                        if (currentsChallengeReqs.contains(currentUser.getDisplayName())) {
                                            Toast.makeText(getContext(), "Challenge Request Failed! You already have a pending challenge request to " + toChallenge, Toast.LENGTH_SHORT).show();
                                        } else {
                                            sendChallengeRequest(toChallenge);
                                            Toast.makeText(getContext(), "Successfully sent a challenge request to " + toChallenge, Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Log.i("currentChallengeReqs", "NULL");
                                    }
                                }
                            }
                        });
                    }
                });

        dialog.show();

    }

    private void sendChallengeRequest(String toChallenge) {
        db.collection("Users").document(toChallenge).update("challenge_requests", FieldValue.arrayUnion(currentUser.getDisplayName()));
    }

    private void fetchChallengeRequests() {
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                challengerNames = (List<String>) documentSnapshot.get("challenge_requests");
                if (challengerNames != null) {
                    if (challengerNames.isEmpty()) {
                        noChallengesText.setVisibility(View.VISIBLE);
                    } else {
                        noChallengesText.setVisibility(View.GONE);
                    }
                    adapter = new ArrayAdapter<String>(mContext, R.layout.challenge_requests_row, challengerNames);
                    challengeRequests.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.i("challengerNames", "NULL");
                }
            }
        });
    }

    private void setupChallengeAcceptance() {
        challengeRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                acceptanceDialog(challengerNames.get(i));
            }
        });
    }

    private void acceptanceDialog(final String challenger) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Accept Or Remove Challenge Request")
                .setMessage("Accept or remove challenge request from " + challenger + "?")
                .setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        userRef.update("challenge_requests", FieldValue.arrayRemove(challenger));
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (hasFriendChallenge) {
                            Toast.makeText(getContext(), "Challenge Acceptance Failed! You already have an active friend challenge", Toast.LENGTH_SHORT).show();
                        } else {
                            setupAcceptedChallenge(challenger);
                        }
                    }
                });

        dialog.show();
    }

    private void setupAcceptedChallenge(final String challenger) {
        userRef.update("challenge_requests", FieldValue.arrayRemove(challenger));

        final Map<String, Object> friend_challenge_map = new HashMap<>();
        final Map<String, Object> data = new HashMap<>();
        Random random = new Random();
        int challengeNumber = random.nextInt(10) + 1;
        final DocumentReference newChallenge = db.document("Challenges/" + challengeNumber);
        newChallenge.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    String myChallengeType = documentSnapshot.getString("type");
                    int totalMyChallenge = documentSnapshot.getLong(myChallengeType).intValue();

                    friend_challenge_map.put("challenge_ref", newChallenge);
                    friend_challenge_map.put("remaining", totalMyChallenge);
                    friend_challenge_map.put("user_ref", challenger);

                    data.put("current_challenge_friend", friend_challenge_map);
                    db.collection("Users").document(currentUser.getDisplayName()).set(data, SetOptions.merge());
                    Toast.makeText(getContext(), "Successfully accepted challenge from " + challenger, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error! Couldn't accept challenge", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext =context;
    }
}
