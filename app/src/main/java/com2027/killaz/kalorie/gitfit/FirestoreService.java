package com2027.killaz.kalorie.gitfit;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class FirestoreService extends Service implements SensorEventListener {
    /**
     * Declare database and user references along with the necessary sensors and counters.
     */
    private Map<String, Object> data;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int stepCounter;

    /**
     * Fetch the current user and their tracked variables.
     */
    public FirestoreService() {
        data = new HashMap<>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRef = db.collection("Users").document(currentUser.getDisplayName());
        fetchUserSteps();
    }

    /**
     * Initialise the necessary sensors.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }

        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.i("SERVICE STARTED-USER:", currentUser.getDisplayName());
        return START_NOT_STICKY;
    }


    /**
     * Unregister the sensors when service ends
     */
    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Track user steps and update the database appropriately in real-time.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        stepCounter++;
        data.put("total_distance_covered", stepCounter);
        db.collection("Users").document(currentUser.getDisplayName()).set(data, SetOptions.merge());

        // Send broadcast so home fragment UI can be updated with new value.
        Intent stepIntent = new Intent();
        stepIntent.setAction("com2027.killaz.kalorie.gitfit.STEP_TAKEN");
        stepIntent.putExtra("steps", stepCounter);
        sendBroadcast(stepIntent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Method to fetch the user steps when the app is started.
     */
    private void fetchUserSteps() {
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    stepCounter = documentSnapshot.getLong("total_distance_covered").intValue();
                }
            }
        });
    }
}
