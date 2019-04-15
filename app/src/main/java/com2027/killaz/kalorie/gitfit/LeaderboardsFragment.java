package com2027.killaz.kalorie.gitfit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class LeaderboardsFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersCollection;
    private ListView leaderboardsList;
    private Button sortingBtn, goToMeBtn;
    private FirebaseUser currentUser;
    private ArrayList<LeaderboardsUser> usersList;
    private static LeaderboardsAdapter listAdapter;
    private ListenerRegistration pointsListener, caloriesListener, distanceListener, challengesListener;
    private TextView personalSortInfo;
    private String userName;
    private int userPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.leaderboards_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        usersCollection = db.collection("Users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userName = currentUser.getDisplayName();

        personalSortInfo = (TextView) getView().findViewById(R.id.leaderboards_sorted_rank);
        leaderboardsList = (ListView) getView().findViewById(R.id.leaderboards_list_view);
        sortingBtn = (Button) getView().findViewById(R.id.leaderboards_sorting_button);
        goToMeBtn = (Button) getView().findViewById(R.id.leaderboards_btn_gotome);

        usersList = new ArrayList<>();

        setupButtons();
        fetchByPoints();
    }

    private void fetchByPoints() {
        pointsListener = usersCollection.orderBy("points", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                usersList.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    LeaderboardsUser user = new LeaderboardsUser(doc.getId(), doc.getDouble("points").intValue(), doc.getDouble("calories").intValue(),
                            doc.getDouble("challenges_completed").intValue(), doc.getDouble("total_distance_covered").intValue());
                    usersList.add(user);
                    if (user.getName().equals(userName)) {
                        String toSet = "You rank at #" + usersList.size() + " for points collected!";
                        userPosition = usersList.size();
                        personalSortInfo.setText(toSet);
                    }
                }
                listAdapter = new LeaderboardsAdapter(usersList, getActivity());
                leaderboardsList.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchByCalories() {
        caloriesListener = usersCollection.orderBy("calories", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                usersList.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    LeaderboardsUser user = new LeaderboardsUser(doc.getId(), doc.getDouble("points").intValue(), doc.getDouble("calories").intValue(),
                            doc.getDouble("challenges_completed").intValue(), doc.getDouble("total_distance_covered").intValue());
                    usersList.add(user);
                    if (user.getName().equals(userName)) {
                        String toSet = "You rank at #" + usersList.size() + " for calories burned!";
                        userPosition = usersList.size();
                        personalSortInfo.setText(toSet);
                    }
                }
                listAdapter = new LeaderboardsAdapter(usersList, getContext());
                leaderboardsList.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchBySteps() {
        distanceListener = usersCollection.orderBy("total_distance_covered", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                usersList.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    LeaderboardsUser user = new LeaderboardsUser(doc.getId(), doc.getDouble("points").intValue(), doc.getDouble("calories").intValue(),
                            doc.getDouble("challenges_completed").intValue(), doc.getDouble("total_distance_covered").intValue());
                    usersList.add(user);
                    if (user.getName().equals(userName)) {
                        String toSet = "You rank at #" + usersList.size() + " for steps taken!";
                        userPosition = usersList.size();
                        personalSortInfo.setText(toSet);
                    }
                }
                listAdapter = new LeaderboardsAdapter(usersList, getContext());
                leaderboardsList.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchByChallenges() {
        challengesListener = usersCollection.orderBy("challenges_completed", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                usersList.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    LeaderboardsUser user = new LeaderboardsUser(doc.getId(), doc.getDouble("points").intValue(), doc.getDouble("calories").intValue(),
                            doc.getDouble("challenges_completed").intValue(), doc.getDouble("total_distance_covered").intValue());
                    usersList.add(user);
                    if (user.getName().equals(userName)) {
                        String toSet = "You rank at #" + usersList.size() + " for challenges completed!";
                        userPosition = usersList.size();
                        personalSortInfo.setText(toSet);
                    }
                }
                listAdapter = new LeaderboardsAdapter(usersList, getActivity());
                leaderboardsList.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupButtons() {
        sortingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] options = {"By Points", "By Calories Burned", "By Steps Taken", "By Challenges Completed"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("Select Sorting")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    sortByPoints();
                                } else if (i == 1) {
                                    sortByCalories();
                                } else if (i == 2) {
                                    sortBySteps();
                                } else {
                                    sortByChallenges();
                                }
                            }
                        });

                dialog.create().show();
            }
        });

        goToMeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaderboardsList.smoothScrollToPosition(userPosition - 1);
                //Handler to allow time for the item to be displayed in the UI. Else error since view is null.
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        leaderboardsList.getChildAt((userPosition-1)-leaderboardsList.getFirstVisiblePosition()).setBackgroundColor(Color.CYAN);
//
//                    }
//                }, 500);
            }
        });
    }

    private void sortByPoints() {
        if (distanceListener != null) {
            distanceListener.remove();
        }
        if (challengesListener != null) {
            challengesListener.remove();
        }
        if (caloriesListener != null) {
            caloriesListener.remove();
        }
        fetchByPoints();
    }

    private void sortByCalories() {
        if (distanceListener != null) {
            distanceListener.remove();
            distanceListener = null;
        }
        if (pointsListener != null) {
            pointsListener.remove();
            pointsListener = null;
        }
        if (challengesListener != null) {
            challengesListener.remove();
            challengesListener = null;
        }
        fetchByCalories();
    }

    private void sortBySteps() {
        if (challengesListener != null) {
            challengesListener.remove();
            challengesListener = null;
        }
        if (pointsListener != null) {
            pointsListener.remove();
            pointsListener = null;
        }
        if (caloriesListener != null) {
            caloriesListener.remove();
            caloriesListener = null;
        }
        fetchBySteps();
    }

    private void sortByChallenges() {
        if (distanceListener != null) {
            distanceListener.remove();
            distanceListener = null;
        }
        if (pointsListener != null) {
            pointsListener.remove();
            pointsListener = null;
        }
        if (caloriesListener != null) {
            caloriesListener.remove();
            caloriesListener = null;
        }
        fetchByChallenges();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("ON STOP CALLED","here");
        if (distanceListener != null) {
            distanceListener.remove();
            distanceListener = null;
        }
        if (pointsListener != null) {
            pointsListener.remove();
            pointsListener = null;
        }
        if (caloriesListener != null) {
            caloriesListener.remove();
            caloriesListener = null;
        }
        if (challengesListener != null) {
            challengesListener.remove();
            challengesListener = null;
        }
    }
}