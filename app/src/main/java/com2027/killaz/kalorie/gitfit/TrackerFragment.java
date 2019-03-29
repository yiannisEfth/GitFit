package com2027.killaz.kalorie.gitfit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.type.LatLng;

public class TrackerFragment extends Fragment implements OnMapReadyCallback {

    private Button startStop, reset;
    private Chronometer timer;
    private long pauseOffset;
    private boolean runningTimer;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFragment;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tracker_fragment, null, false);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        startStop = getView().findViewById(R.id.tracker_start_stop);
        reset = getView().findViewById(R.id.tracker_reset);
        timer = getView().findViewById(R.id.tracker_timer);
        timer.setBase(SystemClock.elapsedRealtime());
        startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!runningTimer) {
                    timer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    timer.start();
                    runningTimer = true;
                } else {
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
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
    }
}
