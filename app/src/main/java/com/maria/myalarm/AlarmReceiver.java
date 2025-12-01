package com.maria.myalarm;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String time = intent.getStringExtra("time");
        String label = intent.getStringExtra("label");
        int alarmId = intent.getIntExtra("alarm_id", -1);

        // 🔔 Full-screen intent
        Intent fullScreenIntent = new Intent(context, AlarmRingActivity.class);
        fullScreenIntent.putExtra("time", time);
        fullScreenIntent.putExtra("label", label);
        fullScreenIntent.putExtra("alarm_id", alarmId);
        fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                context,
                alarmId,
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // ⏰ Стартираме Service за звук
        Intent serviceIntent = new Intent(context, AlarmSoundService.class);
        serviceIntent.putExtra("label", label);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }

        // 🔔 Notification
        String channelId = "alarm_channel";
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Alarm Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle("Аларма: " + time)
                .setContentText(label)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setAutoCancel(true);

        manager.notify(alarmId, builder.build());
    }
}
