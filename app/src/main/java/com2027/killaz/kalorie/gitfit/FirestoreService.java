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
import java.util.Random;

public class FirestoreService extends Service implements SensorEventListener {
    /**
     * Declare database and user references along with the necessary sensors and counters.
     */
    private Map<String, Object> data;
    private Map<String, Object> self_challenge_map;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef;
    private DocumentReference myChallengeReference;
    private DocumentReference newChallenge;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private Intent stepIntent;
    private int stepCounter;
    private int remainingMyChallenge;
    private int totalMyChallenge;
    private String myChallengeType;

    /**
     * Fetch the current user and their tracked variables.
     */
    public FirestoreService() {
        stepIntent = new Intent();
        stepIntent.setAction("com2027.killaz.kalorie.gitfit.STEP_TAKEN");
        data = new HashMap<>();
        self_challenge_map = new HashMap<>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRef = db.collection("Users").document(currentUser.getDisplayName());
        fetchUserData();
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
     * Monitor challenges and if one is completed act accordingly if its either a personal or a friend challenge.
     * Randomly generate a new personal challenge if the previous one is completed.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        stepCounter++;
        remainingMyChallenge--;
        if (remainingMyChallenge <= 0) {
            Random random = new Random();
            int newChallenge = random.nextInt(10) + 1;
            Log.i("New Challenge ID", String.valueOf(newChallenge));
            getNewChallenge(newChallenge);
        }
        self_challenge_map.put("challenge_ref", myChallengeReference);
        self_challenge_map.put("remaining", remainingMyChallenge);
        data.put("total_distance_covered", stepCounter);
        data.put("current_challenge_self", self_challenge_map);
        db.collection("Users").document(currentUser.getDisplayName()).set(data, SetOptions.merge());
        stepIntent.putExtra("steps", stepCounter);
        stepIntent.putExtra("remaining", remainingMyChallenge);
        sendBroadcast(stepIntent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Method to fetch the user's data when the app is started.
     */
    private void fetchUserData() {
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    stepCounter = documentSnapshot.getLong("total_distance_covered").intValue();
                    Map<String, Object> my_challenge = (Map<String, Object>) documentSnapshot.get("current_challenge_self");
                    myChallengeReference = (DocumentReference) my_challenge.get("challenge_ref");
                    fetchUserChallenges();
                    remainingMyChallenge = ((Long) my_challenge.get("remaining")).intValue();
                }
            }
        });
    }

    /**
     * Fetch the user's challenges along with what type of challenges they are and their remaining steps/distance to travel.
     * TODO fetch friend challenge-will finish this week.
     */
    private void fetchUserChallenges() {
        if (myChallengeReference != null) {
            myChallengeReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        myChallengeType = documentSnapshot.getString("type");
                        totalMyChallenge = documentSnapshot.getLong(myChallengeType).intValue();

                        // Send broadcast so home fragment UI can be updated with new value.
                        stepIntent.putExtra("steps", stepCounter);
                        stepIntent.putExtra("remaining", remainingMyChallenge);
                        stepIntent.putExtra("challengeTotal", totalMyChallenge);
                        sendBroadcast(stepIntent);
                    }
                }
            });
        }
    }

    /**
     * Method to fetch a new personal challenge when the previous one is completed.
     * Perform the appropriate variable assignments as well.
     */
    private void getNewChallenge(int challengeNumber) {

        newChallenge = db.document("Challenges/" + challengeNumber);
        newChallenge.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                myChallengeType = documentSnapshot.getString("type");
                totalMyChallenge = documentSnapshot.getLong(myChallengeType).intValue();
                remainingMyChallenge = documentSnapshot.getLong(myChallengeType).intValue();
                self_challenge_map.put("challenge_ref", newChallenge);
                self_challenge_map.put("remaining", totalMyChallenge);
                data.put("current_challenge_self", self_challenge_map);
                db.collection("Users").document(currentUser.getDisplayName()).set(data, SetOptions.merge());
            }
        });
        stepIntent.putExtra("challengeTotal", totalMyChallenge);
        sendBroadcast(stepIntent);
    }
}
