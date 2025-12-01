
package com.maria.myalarm;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

    public class AlarmSoundService extends Service {

        private Ringtone ringtone;
        private final String CHANNEL_ID = "alarm_sound_service";

        @Override
        public void onCreate() {
            super.onCreate();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Alarm Sound Service",
                        NotificationManager.IMPORTANCE_HIGH
                );
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {

            String label = intent.getStringExtra("label");

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Аларма")
                    .setContentText(label)
                    .setSmallIcon(R.drawable.ic_alarm)
                    .build();

            startForeground(1, notification);

            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }

            ringtone = RingtoneManager.getRingtone(this, alarmUri);
            ringtone.play();

            return START_NOT_STICKY;
        }

        @Override
        public void onDestroy() {
            if (ringtone != null && ringtone.isPlaying()) {
                ringtone.stop();
            }
            super.onDestroy();
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

