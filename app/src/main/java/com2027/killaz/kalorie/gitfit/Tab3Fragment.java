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

public class Tab3Fragment extends Fragment {
    View v;
    private RecyclerView myrecyclerview;
    private List<Tab3> lstTab3;
    public Tab3Fragment(){

    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = inflater.inflate(R.layout.tab3_head,container,false);
        myrecyclerview = v.findViewById(R.id.tab3_recycler_view);
        RVA_Tab3 recyclerAdapter = new RVA_Tab3(getContext(),lstTab3);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(recyclerAdapter);
        return v;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lstTab3 = new ArrayList<>();
        lstTab3.add(new Tab3("Shaggy Rogers" ));
        lstTab3.add(new Tab3("Freddy Jones" ));
        lstTab3.add(new Tab3("Velma Dinkley"));
        lstTab3.add(new Tab3("Daphne Blake"));
        lstTab3.add(new Tab3("Scooby Doo"));

    }
}

