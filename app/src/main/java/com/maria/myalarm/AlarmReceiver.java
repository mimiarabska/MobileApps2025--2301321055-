package com.maria.myalarm;

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

        // ===== FULL SCREEN INTENT =====
        Intent alarmIntent = new Intent(context, AlarmRingActivity.class);
        alarmIntent.putExtra("time", time);
        alarmIntent.putExtra("label", label);
        alarmIntent.putExtra("alarm_id", alarmId);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                context,
                alarmId,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // ===== START SOUND SERVICE =====
        Intent serviceIntent = new Intent(context, AlarmSoundService.class);
        serviceIntent.putExtra("label", label);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }

        // ===== NOTIFICATION CHANNEL =====
        String channelId = "alarm_channel";
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Alarm Notifications",
                    NotificationManager.IMPORTANCE_MAX
            );
            channel.setDescription("Алармено известие");
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(channel);
        }

        // ===== NOTIFICATION BUILDER =====
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle("Аларма: " + time)
                .setContentText(label)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setOngoing(true)  // Keeps it active until user interacts
                .setAutoCancel(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        manager.notify(alarmId, builder.build());
    }
}
