package com2027.killaz.kalorie.gitfit;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FriendRequestsListAdapter extends RecyclerView.Adapter<FriendRequestsListAdapter.FriendRequestsViewHolder> {

    private List<String> friendReqs;
    private Context context;

    public FriendRequestsListAdapter(List<String> friendReqs, Context context) {
        this.friendReqs = friendReqs;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.friend_request_item, parent, false);
        return new FriendRequestsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestsViewHolder holder, int position) {
        holder.userName.setText(friendReqs.get(position));
    }

    @Override
    public int getItemCount() {
        return friendReqs.size();
    }


    class FriendRequestsViewHolder extends RecyclerView.ViewHolder {

        public TextView userName;
        private ImageView acceptFriend;
        private ImageView rejectFriend;
        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        private FirebaseUser currentUser;
        private DocumentReference userRef;


        public FriendRequestsViewHolder(View itemView) {
            super(itemView);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            userRef = db.collection("Users").document(currentUser.getDisplayName());
            userName = itemView.findViewById(R.id.friend_req_user_name);
            acceptFriend = itemView.findViewById(R.id.accept_friend);
            rejectFriend = itemView.findViewById(R.id.decline_friend);
            setButtons();
        }

        public void setButtons() {
            acceptFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    userRef.update("friend_requests", FieldValue.arrayRemove(userName.getText().toString()));
                }
            });

            rejectFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Friend request declined", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}