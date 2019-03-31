package com2027.killaz.kalorie.gitfit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class NotificationAlarmReceiver extends BroadcastReceiver {

    private String notifText= "";

    @Override
    public void onReceive(Context context, Intent intent) {
        final Context finalContext = context;

        Log.i("NotifAlarmReceiver", "Broadcast received");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference notifRef = db.collection("Notifications").document("WeeklyMessages");

        // Get a random notification string from the database
        notifRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot docSnapshot = task.getResult();
                    Random random = new Random();
                    int messageID = random.nextInt(10) + 1;
                    notifText = docSnapshot.getString(String.valueOf(messageID));

                    // Check that the string was retrieved correctly
                    if (!notifText.equals("") || notifText == null) {

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(finalContext, "NOTIFICATION_CHANNEL_ID");
                        builder.setSmallIcon(R.drawable.login_logo);
                        builder.setContentText(notifText);
                        builder.setContentTitle("GitFit");
                        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(finalContext);
                        notificationManager.notify((int) System.currentTimeMillis(), builder.build());

                        Log.i("NotifAlarmReceiver", "Notification sent");

                    }
                    else {
                        Log.i("NotifAlarmReceiver", "Failed to get notifText");
                    }
                }
            }
        });
    }
}
