package com2027.killaz.kalorie.gitfit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class NotificationAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("NOTIFICATION", "BEING CONSTRUCTED");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "NOTIFICATION_CHANNEL_ID");
        builder.setSmallIcon(R.drawable.login_logo);
        builder.setContentText("Test Notification! You're doing great!");
        builder.setContentTitle("GitFit");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        Log.i("NOTIFICATION", "SENT");
    }
}
