package com2027.killaz.kalorie.gitfit;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class LeaderboardsAdapter extends ArrayAdapter<LeaderboardsUser> {

    private ArrayList<LeaderboardsUser> userList;
    private Context mContext;
    private FirebaseUser currentUser;

    private static class ViewHolder {
        TextView userRank;
        TextView userName;
        TextView userPoints;
        TextView userCalories;
        TextView userChallenges;
        TextView userDistance;
    }

    public LeaderboardsAdapter(ArrayList<LeaderboardsUser> userList, Context context) {
        super(context, R.layout.leaderboards_row_item, userList);
        this.userList = userList;
        this.mContext = context;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Get the data item for this position
        LeaderboardsUser aUser = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;// view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.leaderboards_row_item, parent, false);

            viewHolder.userRank = (TextView) convertView.findViewById(R.id.leaderboards_rank);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.leaderboards_name);
            viewHolder.userCalories = (TextView) convertView.findViewById(R.id.leaderboards_calories);
            viewHolder.userChallenges = (TextView) convertView.findViewById(R.id.leaderboards_challenges);
            viewHolder.userDistance = (TextView) convertView.findViewById(R.id.leaderboards_distance);
            viewHolder.userPoints = (TextView) convertView.findViewById(R.id.leaderboards_points);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.userRank.setText(String.valueOf(position+1));
        viewHolder.userName.setText(aUser.getName());
        viewHolder.userCalories.setText(String.valueOf(aUser.getCaloriesBurned()));
        viewHolder.userChallenges.setText(String.valueOf(aUser.getChallengesCompleted()));
        viewHolder.userDistance.setText(String.valueOf(aUser.getDistanceTraveled()));
        viewHolder.userPoints.setText(String.valueOf(aUser.getPoints()));
        if (currentUser.getDisplayName().equals(aUser.getName())){
            result.setBackgroundColor(Color.CYAN);
        }
        else {
            result.setBackgroundColor(Color.TRANSPARENT);
        }
        return result;
    }
}
