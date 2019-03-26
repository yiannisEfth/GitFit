package com2027.killaz.kalorie.gitfit;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

class LeaderboardsAdapter implements ListAdapter {

    ArrayList<LeaderboardsData> arrayList;
    Context context;
    public LeaderboardsAdapter(Context context, ArrayList<LeaderboardsData> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
    @Override
    public boolean isEnabled(int position) {
        return true;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LeaderboardsData leaderboardsData = arrayList.get(i);
        if(view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view=layoutInflater.inflate(R.layout.leaderboards_list_layout, null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            TextView leaderboardsRank = view.findViewById(R.id.leaderboards_ranking);
            TextView leaderboardsName = view.findViewById(R.id.leaderboards_name);
            TextView leaderboardsPoints = view.findViewById(R.id.leaderboards_points);
            leaderboardsName.setText(leaderboardsData.leaderboardsName);
            leaderboardsPoints.setText(leaderboardsData.leaderboardsPoints);
        }
        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}
