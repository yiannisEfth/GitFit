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
            return inflater.inflate(R.layout.achievements, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        mSectionsPageAdapter = new SectionsPageAdapter(getFragmentManager());
        mViewPager = (ViewPager) getView().findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }


    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "Personal");
        adapter.addFragment(new Tab2Fragment(), "Friends");
        adapter.addFragment(new Tab3Fragment(), "Challenge");
        viewPager.setAdapter(adapter);
    }


}
