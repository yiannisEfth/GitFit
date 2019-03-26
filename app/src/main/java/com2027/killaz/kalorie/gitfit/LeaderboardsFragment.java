package com2027.killaz.kalorie.gitfit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class LeaderboardsFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<LeaderboardsData> users = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.leaderboards_fragment, container, false);

//        db.collection("Users").orderBy("points").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                if(e!= null) {
//                    Log.d(TAG, "Error:" + e.getMessage());
//                }
//
//                for(DocumentChange doc : documentSnapshots.getDocumentChanges()) {
//                    if(doc.getType() == DocumentChange.Type.ADDED) {
//                        LeaderboardsData user = new LeaderboardsData("","","");
//                        String user_name = doc.getDocument().getId();
//                        String user_points = doc.getDocument().get("points").toString();
//                        user.setLeaderboardsName(user_name);
//                        user.setLeaderboardsPoints(user_points);
//                    }
//                }
//            }
//        });
//        ListView leaderboardsList = view.findViewById(R.id.leaderboards_list);
//        LeaderboardsAdapter leaderboardsAdapter = new LeaderboardsAdapter(this.getContext(), users);
//        leaderboardsList.setAdapter(leaderboardsAdapter);
//
        return view;
    }



}