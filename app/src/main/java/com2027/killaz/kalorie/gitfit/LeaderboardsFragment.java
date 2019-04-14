package com2027.killaz.kalorie.gitfit;

import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class LeaderboardsFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<LeaderboardsData> users = new ArrayList<>();

    public static LeaderboardsFragment newInstance(){
        LeaderboardsFragment leaderboardsFragment = new LeaderboardsFragment();
        return leaderboardsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.leaderboards_fragment, container, false);

        db.collection("Users").orderBy("points").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e!= null) {
                    Log.d(TAG, "Error:" + e.getMessage());
                }

                for(DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if(doc.getType() == DocumentChange.Type.ADDED) {
                        LeaderboardsData user = new LeaderboardsData("","","");
                        String user_name = doc.getDocument().getId();
                        String user_points = doc.getDocument().get("points").toString();
                        user.setLeaderboardsName(user_name);
                        user.setLeaderboardsScore(user_points);
                    }
                }
            }
        });
        ListView leaderboardsList = view.findViewById(R.id.leaderboards_list);
        LeaderboardsAdapter leaderboardsAdapter = new LeaderboardsAdapter(this.getContext(), users);
        leaderboardsList.setAdapter(leaderboardsAdapter);

        return view;
    }


}