package com2027.killaz.kalorie.gitfit;

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
    private Button byPointsBtn, byCaloriesBtn, byDistanceBtn, byChallengesBtn;
    private ArrayList<LeaderboardsUser> usersList;
    private static LeaderboardsAdapter listAdapter;
    private ListenerRegistration pointsListener, caloriesListener, distanceListener, challengesListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.leaderboards_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usersCollection = db.collection("Users");

        leaderboardsList = (ListView) getView().findViewById(R.id.leaderboards_list_view);
        byPointsBtn = (Button) getView().findViewById(R.id.leaderboards_button_points);
        byCaloriesBtn = (Button) getView().findViewById(R.id.leaderboards_button_calories);
        byDistanceBtn = (Button) getView().findViewById(R.id.leaderboards_button_distance);
        byChallengesBtn = (Button) getView().findViewById(R.id.leaderboards_button_challenges);
        usersList = new ArrayList<>();

        setupButtonListeners();
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
                    Log.i("USER LIST LENGTH", String.valueOf(usersList.size()));
                }
                listAdapter = new LeaderboardsAdapter(usersList, getContext());
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
                    Log.i("USER LIST LENGTH", String.valueOf(usersList.size()));
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
                    Log.i("USER LIST LENGTH", String.valueOf(usersList.size()));
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
                    Log.i("USER LIST LENGTH", String.valueOf(usersList.size()));
                }
                listAdapter = new LeaderboardsAdapter(usersList, getContext());
                leaderboardsList.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupButtonListeners() {
        byPointsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        byCaloriesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        byDistanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        byChallengesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        });

    }
}