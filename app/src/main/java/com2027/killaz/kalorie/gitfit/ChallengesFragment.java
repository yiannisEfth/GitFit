package com2027.killaz.kalorie.gitfit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChallengesFragment extends Fragment {

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mViewPager = (ViewPager)  inflater.inflate(R.layout.achievements, container, false);

        mSectionsPageAdapter = new SectionsPageAdapter(getFragmentManager());
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        return mViewPager;
    }


    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "Daily");
        adapter.addFragment(new Tab2Fragment(), "Friends");
        adapter.addFragment(new Tab3Fragment(), "Challenge");
        viewPager.setAdapter(adapter);
    }


}
