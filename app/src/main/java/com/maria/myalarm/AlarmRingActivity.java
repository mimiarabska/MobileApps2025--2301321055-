package com.maria.myalarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AlarmRingActivity extends AppCompatActivity {

    private Ringtone ringtone;

   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ⚡ Пълноценно показване върху заключен екран
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        setContentView(R.layout.activity_alarm_ring);

        TextView timeText = findViewById(R.id.alarmTimeText);
        TextView labelText = findViewById(R.id.alarmLabelText);
        Button snoozeButton = findViewById(R.id.snoozeButton);
        Button stopButton = findViewById(R.id.stopButton);

        String time = getIntent().getStringExtra("time");
        String label = getIntent().getStringExtra("label");
        int alarmId = getIntent().getIntExtra("alarm_id", -1);

        timeText.setText(time);
        labelText.setText(label);


        // Snooze 10 минути
        snoozeButton.setOnClickListener(v -> {

            stopRingtone();


            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 10);
            long snoozeTime = calendar.getTimeInMillis();

            int snoozeId = (int) System.currentTimeMillis();

            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("time", "След 10 минути");
            intent.putExtra("label", label);
            intent.putExtra("alarm_id", snoozeId);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    snoozeId,  // различен от оригиналната аларма
                    intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    snoozeTime,
                    pendingIntent
            );

            // Затваряме прозореца
            finish();
        });



        // Stop
        stopButton.setOnClickListener(v -> {
            stopRingtone();
            finish();
        });
    }

    private void stopRingtone() {
        stopService(new Intent(this, AlarmSoundService.class));
    }


    @Override
    protected void onDestroy() {
        stopRingtone();
        super.onDestroy();
    }
}
