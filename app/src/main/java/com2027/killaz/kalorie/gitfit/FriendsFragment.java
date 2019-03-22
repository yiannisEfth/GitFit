package com2027.killaz.kalorie.gitfit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
    //TODO: FIX THE FRONT END AND SWIPE VIEWS AND ITEM ROW DESIGN. BACKEND WORKS AS IT SHOULD :D -Yiannis
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser;
    private DocumentReference userRef;
    private RecyclerView friendReqsRecycler;
    private List<String> friendReqsList;
    private FriendRequestsListAdapter friendReqsAdapter;
    private FriendRequestsListAdapter friendsAdapter;
    private RecyclerView friendsRecycler;
    private List<String> friendsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.friends_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        friendReqsRecycler = getView().findViewById(R.id.friends_req_recycler_view);
        friendReqsRecycler.setHasFixedSize(true);
        friendReqsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsRecycler = getView().findViewById(R.id.friends_recycler_view);
        friendsRecycler.setHasFixedSize(true);
        friendsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        friendReqsList = new ArrayList<>();
        friendsList = new ArrayList<>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRef = db.collection("Users").document(currentUser.getDisplayName());
        fetchFriendReqs();
        fetchFriends();
        setupRecyclerActions();
    }

    private void fetchFriendReqs() {
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    friendReqsList = (List<String>) documentSnapshot.get("friend_requests");
                    friendReqsAdapter = new FriendRequestsListAdapter(friendReqsList, getActivity());
                    friendReqsRecycler.setAdapter(friendReqsAdapter);
                    friendReqsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void fetchFriends() {
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    friendsList = (List<String>) documentSnapshot.get("friends");
                    friendsAdapter = new FriendRequestsListAdapter(friendsList, getActivity());
                    friendsRecycler.setAdapter(friendsAdapter);
                    friendsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setupRecyclerActions() {

        ItemTouchHelper.SimpleCallback callBackReqs = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                Log.i("POSITIONNN", String.valueOf(direction));
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    userRef.update("friends", FieldValue.arrayUnion(friendReqsList.get(position)));
                    userRef.update("friend_requests", FieldValue.arrayRemove(friendReqsList.get(position)));
                    friendReqsList.remove(position);
                    friendReqsAdapter.notifyItemRemoved(position);
                    friendReqsAdapter.notifyItemRangeChanged(position, friendReqsAdapter.getItemCount());
                    Toast.makeText(getContext(), "Friend Request Accepted", Toast.LENGTH_SHORT).show();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    userRef.update("friends", FieldValue.arrayUnion(friendReqsList.get(position)));
                    userRef.update("friend_requests", FieldValue.arrayRemove(friendReqsList.get(position)));
                    friendReqsList.remove(position);
                    friendReqsAdapter.notifyItemRemoved(position);
                    friendReqsAdapter.notifyItemRangeChanged(position, friendReqsAdapter.getItemCount());
                    Toast.makeText(getContext(), "Friend Request Declined", Toast.LENGTH_SHORT).show();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBackReqs);
        itemTouchHelper.attachToRecyclerView(friendReqsRecycler);

    }


}
