package com2027.killaz.kalorie.gitfit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirestoreService extends Service {

    private FirebaseUser currentUser;

    public FirestoreService() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("SERVICE STARTED-USER:", currentUser.getDisplayName());
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
