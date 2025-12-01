package com.maria.myalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AlarmRingActivity extends AppCompatActivity {

    private Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);

        TextView timeText = findViewById(R.id.alarmTimeText);
        TextView labelText = findViewById(R.id.alarmLabelText);
        Button snoozeButton = findViewById(R.id.snoozeButton);
        Button stopButton = findViewById(R.id.stopButton);

        // Получаваме данни от intent
        String time = getIntent().getStringExtra("time");
        String label = getIntent().getStringExtra("label");
        int alarmId = getIntent().getIntExtra("alarm_id", -1);

        timeText.setText(time);
        labelText.setText(label);

        // Пускане на звук
        try {
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            ringtone = RingtoneManager.getRingtone(this, alarmUri);
            ringtone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Snooze → 10 минути по-късно
        snoozeButton.setOnClickListener(v -> {
            if (alarmId != -1) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, 10);

                Intent intent = new Intent(this, AlarmReceiver.class);
                intent.putExtra("time", time);
                intent.putExtra("label", label);
                intent.putExtra("alarm_id", alarmId);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        this,
                        alarmId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            }
            stopRingtone();
            finish();
        });

        // Stop
        stopButton.setOnClickListener(v -> {
            stopRingtone();
            finish();
        });
    }

    private void stopRingtone() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRingtone();
    }
}
