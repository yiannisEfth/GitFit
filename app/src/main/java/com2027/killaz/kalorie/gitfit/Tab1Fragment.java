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

public class Tab1Fragment extends Fragment {
    View v;
    private RecyclerView myrecyclerview;
    private List<Tab1> lstTab1;
    public Tab1Fragment(){

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = inflater.inflate(R.layout.tab1_head,container,false);
        myrecyclerview = v.findViewById(R.id.tab1_recycler_view);
        RVA_Tab1 recyclerAdapter = new RVA_Tab1(getContext(),lstTab1);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(recyclerAdapter);
        return v;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lstTab1 = new ArrayList<>();
        lstTab1.add(new Tab1( "Run 5km from the swamp monster"));
        lstTab1.add(new Tab1( "Lay 5 traps on different stop points"));
        lstTab1.add(new Tab1( "Unmask 3 villains"));
        lstTab1.add(new Tab1("Kick ass for 10 minutes straight"));
        lstTab1.add(new Tab1("Find 1 hidden Scooby-Snacks box"));

    }
}

