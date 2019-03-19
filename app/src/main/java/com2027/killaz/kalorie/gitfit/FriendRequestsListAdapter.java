package com2027.killaz.kalorie.gitfit;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        TextView userName;


        FriendRequestsViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.friend_req_user_name);
        }

    }
}