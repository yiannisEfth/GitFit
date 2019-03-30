package com2027.killaz.kalorie.gitfit;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.text.DecimalFormat;

public class TrackerFragment extends Fragment implements OnMapReadyCallback, SensorEventListener {

    private Button startStop, reset;
    private Chronometer timer;
    private long pauseOffset;
    private boolean runningTimer;
    private GoogleMap mGoogleMap;
    private SensorManager sManager;
    private Sensor stepSensor;
    private long steps;
    private float distanceRan;
    private TextView stepsTakenText, distanceTraveledText;
    private SupportMapFragment mapFragment;
    private DecimalFormat roundKms;


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
        stepsTakenText = getView().findViewById(R.id.tracker_steps_counter);
        distanceTraveledText = getView().findViewById(R.id.tracker_distance_traveled);

        roundKms = new DecimalFormat("#.###");
        timer.setBase(SystemClock.elapsedRealtime());
        sManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        setButtonListeners();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //TODO DISPLAY WARNING-CANT USE THIS FUNCTIONALITY UNTIL PERMISSIONS ARE GIVEN
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        float[] values = sensorEvent.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }

        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            steps++;
            distanceRan = (float) (steps * 74) / (float) 100000;
            distanceRan = Float.valueOf(roundKms.format(distanceRan));
            stepsTakenText.setText(String.valueOf(steps));
            distanceTraveledText.setText(String.valueOf(distanceRan));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void setButtonListeners() {
        startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!runningTimer) {
                    sManager.registerListener(TrackerFragment.this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
                    timer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    timer.start();
                    runningTimer = true;
                } else {
                    sManager.unregisterListener(TrackerFragment.this, stepSensor);
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
                steps = 0;
                distanceRan = 0;
                stepsTakenText.setText("0");
                String set0km = "0 km";
                distanceTraveledText.setText(set0km);
            }
        });
    }
}
