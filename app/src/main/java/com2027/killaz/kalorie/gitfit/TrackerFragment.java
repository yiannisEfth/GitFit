package com2027.killaz.kalorie.gitfit;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.type.LatLng;

public class TrackerFragment extends Fragment implements OnMapReadyCallback {

    private Button startStop, reset;
    private Chronometer timer;
    private long pauseOffset;
    private boolean runningTimer;
    public GoogleMap mGoogleMap;
    SupportMapFragment mapFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tracker_fragment, container, false);
    }

    public TrackerFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        startStop = getView().findViewById(R.id.tracker_start_stop);
        reset = getView().findViewById(R.id.tracker_reset);
        timer = getView().findViewById(R.id.tracker_timer);
        timer.setBase(SystemClock.elapsedRealtime());

        mapFragment = (SupportMapFragment)getFragmentManager().findFragmentById(R.id.mapView);
        startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!runningTimer) {
                    timer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    timer.start();
                    runningTimer = true;
                }
                else {
                    timer.stop();
                    pauseOffset = SystemClock.elapsedRealtime() - timer.getBase();
                    runningTimer = false;
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.setBase(SystemClock.elapsedRealtime());
                pauseOffset = 0;
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mapFragment.getMapAsync(this);
    }
}
