package com2027.killaz.kalorie.gitfit;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button btn1 = (Button) getView().findViewById(R.id.homeBtn1);
        Button btn2 = (Button) getView().findViewById(R.id.homeBtn2);
        Button btn3 = (Button) getView().findViewById(R.id.homeBtn3);
    }

}
