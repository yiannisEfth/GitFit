package com2027.killaz.kalorie.gitfit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
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
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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
    private TextView stepsTakenText, distanceTraveledText, pointsText, paceText, caloriesText;
    private SupportMapFragment mapFragment;
    private DecimalFormat roundKms;
    private DecimalFormat roundPace;
    private LocationManager locManager;
    private LocationListener locListener;
    private Criteria locCriteria;
    private String locProvider;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser;
    private DocumentReference userRef;
    private int collectedPoints;
    private float averagePace;
    private int burnedCalories;
    private int completedChallenges;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tracker_fragment, null, false);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        return view;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRef = db.collection("Users").document(currentUser.getDisplayName());
        startStop = getView().findViewById(R.id.tracker_start_stop);
        reset = getView().findViewById(R.id.tracker_reset);
        timer = getView().findViewById(R.id.tracker_timer);
        stepsTakenText = getView().findViewById(R.id.tracker_steps_counter);
        distanceTraveledText = getView().findViewById(R.id.tracker_distance_traveled);
        pointsText = getView().findViewById(R.id.tracker_points);
        paceText = getView().findViewById(R.id.tracker_pace);
        caloriesText = getView().findViewById(R.id.tracker_calories);

        roundKms = new DecimalFormat("#.###");
        roundPace = new DecimalFormat("#.##");
        timer.setBase(SystemClock.elapsedRealtime());
        sManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        locCriteria = new Criteria();
        locCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        locCriteria.setPowerRequirement(Criteria.POWER_HIGH);
        setupLocationListener();
        locManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locProvider = locManager.getBestProvider(locCriteria, false);
        locManager.requestLocationUpdates(locProvider, 2000, 10, locListener);

        setButtonListeners();
        isLocationEnabled();
        setFirebaseFetch();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mapFragment.setMenuVisibility(false);
            Toast.makeText(getContext(), "Map cannot be used unless location permissions are granted.", Toast.LENGTH_SHORT).show();
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
            float elapsedSeconds = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000);
            float elapsesHours = elapsedSeconds / 3600;
            collectedPoints = (int) (steps * 0.65 + 300 + (completedChallenges * 1.35));
            distanceRan = (float) (steps * 74) / (float) 100000;
            distanceRan = Float.valueOf(roundKms.format(distanceRan));
            averagePace = Float.valueOf(roundPace.format(distanceRan / elapsesHours));
            burnedCalories += (int) Math.round(0.0175 * averagePace * 70);
            String setPaceText = String.valueOf(averagePace) + " km/h";
            paceText.setText(setPaceText);
            caloriesText.setText(String.valueOf(burnedCalories));
            pointsText.setText(String.valueOf(collectedPoints));
            stepsTakenText.setText(String.valueOf(steps));
            distanceTraveledText.setText(String.valueOf(distanceRan));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void setButtonListeners() {
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
                collectedPoints = 0;
                distanceRan = 0;
                stepsTakenText.setText("0");
                pointsText.setText("0");
                String set0km = "0 km";
                distanceTraveledText.setText(set0km);
            }
        });
    }

    private void setupLocationListener() {
        locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                PolylineOptions polyOptions = new PolylineOptions().width(3).color(Color.RED);
                LatLng latLng = new LatLng(lat, lon);
                polyOptions.add(latLng);
                mGoogleMap.addPolyline(polyOptions);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }

    private void isLocationEnabled() {
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
        }

    }

    private void setFirebaseFetch() {
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                completedChallenges = documentSnapshot.getLong("challenges_completed").intValue();
            }
        });
    }
}
