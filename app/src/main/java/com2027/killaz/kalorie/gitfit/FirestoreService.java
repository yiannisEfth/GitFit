package com2027.killaz.kalorie.gitfit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class FirestoreService extends Service implements SensorEventListener {
    /**
     * Declare database and user references along with the necessary sensors and counters.
     */
    private Map<String, Object> data;
    private Map<String, Object> self_challenge_map;
    private Map<String, Object> friend_challenge_map;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef;
    private DocumentReference myChallengeReference;
    private DocumentReference friendChallengeReference;
    private DocumentReference newChallenge;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private Intent stepIntent;
    private int stepCounter;
    private int stepsToday;
    private int remainingMyChallenge;
    private int remainingFriendChallenge;
    private int totalMyChallenge;
    private int totalFriendChallenge;
    private int completedChallenges;
    private int points;
    private String myChallengeType;
    private String friendChallengeType;
    private String friendChallenger;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private DatabaseHelper dbHelper;
    private Calendar calendar;

    /**
     * Fetch the current user and their tracked variables.
     */
    public FirestoreService() {
        dbHelper = DatabaseHelper.getInstance(this);
        stepIntent = new Intent();
        stepIntent.setAction("com2027.killaz.kalorie.gitfit.STEP_TAKEN");
        data = new HashMap<>();
        self_challenge_map = new HashMap<>();
        friend_challenge_map = new HashMap<>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRef = db.collection("Users").document(currentUser.getDisplayName());
        fetchUserData();
    }

    /**
     * Initialise the necessary sensors and alarm manager to reset steps each day.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = getApplicationContext();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }

        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
        Log.i("SERVICE STARTED - USER:", currentUser.getDisplayName());

        // Get today's date.
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // Get saved steps so far today from local database.
        try {
            stepsToday = dbHelper.getSteps(currentUser.getDisplayName(), calendar.getTime());
        }
        catch (Exception e) {
            stepsToday = 0;
            e.printStackTrace();
        }
        Log.i("STEPS_TODAY_INIT", String.valueOf(stepsToday));

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent innerIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, innerIntent, 0);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

        return START_NOT_STICKY;
    }

    /**
     * Triggers the on receive method  at midnight (See above).
     */
    private class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());

            // Initialise new daily step record.
            dbHelper.newRecord(currentUser.getDisplayName(), cal.getTime(), 0);

            // Save steps from yesterday into database.
            cal.add(Calendar.DATE, -1);
            dbHelper.updateRecordSteps(currentUser.getDisplayName(), cal.getTime(), stepsToday);

            // Reset variable.
            stepsToday = 0;
        }
    }

    /**
     * Unregister the sensors when service ends
     */
    @Override
    public void onDestroy() {
        // Stop tracking steps
        try {
            sensorManager.unregisterListener(this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
        stepsToday++;
        remainingMyChallenge--;
        remainingFriendChallenge--;

        if (remainingMyChallenge <= 0) {
            completedChallenges++;
            data.put("challenges_completed", completedChallenges);
            Random random = new Random();
            int newChallenge = random.nextInt(10) + 1;
            Log.i("New Challenge ID", String.valueOf(newChallenge));
            getNewChallenge(newChallenge);
        }

        if (friendChallengeReference != null && remainingFriendChallenge!=0) {
            friend_challenge_map.put("remaining", remainingFriendChallenge);
            data.put("current_challenge_friend", friend_challenge_map);
            stepIntent.putExtra("friend_remaining", remainingFriendChallenge);
        }
        if (remainingFriendChallenge == 0) {
            completedChallenges++;
            friend_challenge_map.put("remaining", null);
            friend_challenge_map.put("challenge_ref", null);
            friend_challenge_map.put("user_ref", null);
            data.put("current_challenge_friend", friend_challenge_map);
            data.put("challenges_completed", completedChallenges);
            friendChallengeReference = null;

            //TODO Friend challenge finished...let user know. Front end stuff.
        }
        self_challenge_map.put("remaining", remainingMyChallenge);
        points = (int) (stepCounter * 0.65 + 300 + (completedChallenges*1.35));
        stepIntent.putExtra("challenges_completed", completedChallenges);
        stepIntent.putExtra("points", points);
        data.put("points", points);
        data.put("total_distance_covered", stepCounter);
        data.put("current_challenge_self", self_challenge_map);
        db.collection("Users").document(currentUser.getDisplayName()).set(data, SetOptions.merge());

        //TODO Change steps data sent to change depending on option selected in homeFragment (daily/weekly/monthly)
        // currently send daily for testing purposes
        stepIntent.putExtra("steps", stepsToday);
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
                    Map<String, Object> friend_challenge = (Map<String, Object>) documentSnapshot.get("current_challenge_friend");
                    myChallengeReference = (DocumentReference) my_challenge.get("challenge_ref");
                    friendChallengeReference = (DocumentReference) friend_challenge.get("challenge_ref");
                    completedChallenges = documentSnapshot.getLong("challenges_completed").intValue();
                    points = documentSnapshot.getLong("points").intValue();

                    stepIntent.putExtra("challenges_completed", completedChallenges);
                    stepIntent.putExtra("points", points);
                    sendBroadcast(stepIntent);

                    fetchUserChallenges();
                    remainingMyChallenge = ((Long) my_challenge.get("remaining")).intValue();
                    if (friendChallengeReference != null) {
                        remainingFriendChallenge = ((Long) friend_challenge.get("remaining")).intValue();
                        friendChallenger = friend_challenge.get("user_ref").toString();
                    }
                }
            }
        });
    }

    /**
     * Fetch the user's challenges along with what type of challenges they are and their remaining steps/distance to travel.
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
                        stepIntent.putExtra("steps", stepsToday);
                        stepIntent.putExtra("remaining", remainingMyChallenge);
                        stepIntent.putExtra("challengeTotal", totalMyChallenge);
                        sendBroadcast(stepIntent);
                    }
                }
            });
        }

        if (friendChallengeReference != null) {
            friendChallengeReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        friendChallengeType = documentSnapshot.getString("type");
                        totalFriendChallenge = documentSnapshot.getLong(friendChallengeType).intValue();

                        // Send broadcast for friend's challenge to update the home fragment ui.
                        stepIntent.putExtra("friend_remaining", remainingFriendChallenge);
                        stepIntent.putExtra("friend_challenge_total", totalFriendChallenge);
                        stepIntent.putExtra("friend_challenger", friendChallenger);
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
                stepIntent.putExtra("challengeTotal", totalMyChallenge);
                sendBroadcast(stepIntent);
            }
        });
    }
}
