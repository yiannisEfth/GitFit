package com2027.killaz.kalorie.gitfit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2/28/2017.
 */

public class Tab2Fragment extends Fragment {
    View v;
    private RecyclerView myrecyclerview;
    private List<Tab2> lstTab2;
    public Tab2Fragment(){

    }
    private static final String TAG = "Tab2Fragment";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = inflater.inflate(R.layout.tab2_head,container,false);
        myrecyclerview = v.findViewById(R.id.tab2_recycler_view);
        RVA_Tab2 recyclerAdapter = new RVA_Tab2(getContext(),lstTab2);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(recyclerAdapter);
        return v;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lstTab2 = new ArrayList<>();
        lstTab2.add(new Tab2("Shaggy Rogers" , "Run 5km from the swamp monster"));
        lstTab2.add(new Tab2("Freddy Jones" , "Lay 5 traps on different stop points"));
        lstTab2.add(new Tab2("Velma Dinkley" , "Unmask 3 villains"));
        lstTab2.add(new Tab2("Daphne Blake" , "Kick ass for 10 minutes straight"));
        lstTab2.add(new Tab2("Scooby Doo" , "Find 1 hidden Scooby-Snacks box"));

    }
}

