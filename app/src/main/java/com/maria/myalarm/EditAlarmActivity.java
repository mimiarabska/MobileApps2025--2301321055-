package com.maria.myalarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.maria.myalarm.data.model.Alarm;
import com.maria.myalarm.data.repository.AlarmRepository;

import java.util.Calendar;

public class EditAlarmActivity extends AppCompatActivity {

    private TextView editAlarmTime;
    private EditText editLabel;
    private Button changeTimeButton;
    private Button saveAlarmButton;
    private Button shareAlarmButton;

    private AlarmRepository repository;
    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        int alarmId = getIntent().getIntExtra("alarm_id", -1);

        repository = new AlarmRepository(this);
        alarm = repository.getAlarmById(alarmId);

        // Свързване към XML елементите
        editAlarmTime = findViewById(R.id.editAlarmTime);
        editLabel = findViewById(R.id.editLabel);
        changeTimeButton = findViewById(R.id.changeTimeButton);
        saveAlarmButton = findViewById(R.id.saveAlarmButton);
        shareAlarmButton = findViewById(R.id.shareAlarmButton);

        // Попълване с текущите данни
        editAlarmTime.setText(alarm.getTime());
        editLabel.setText(alarm.getLabel());

        // Избор на нов час
        changeTimeButton.setOnClickListener(v -> openTimePicker());

        // Запис
        saveAlarmButton.setOnClickListener(v -> saveChanges());

        // Споделяне
        shareAlarmButton.setOnClickListener(v -> shareAlarm());
    }

    private void openTimePicker() {
        String[] parts = alarm.getTime().split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        new TimePickerDialog(
                this,
                (view, h, m) -> editAlarmTime.setText(String.format("%02d:%02d", h, m)),
                hour,
                minute,
                true
        ).show();
    }

    private void saveChanges() {
        alarm.setTime(editAlarmTime.getText().toString());
        alarm.setLabel(editLabel.getText().toString());

        // Проверка за разрешение Exact Alarm (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);

                Toast.makeText(this,
                        "Моля разрешете „Exact Alarms“ в настройките, за да работи алармата.",
                        Toast.LENGTH_LONG).show();

                return;
            }
        }

        repository.update(alarm);
        scheduleAlarm(alarm);
        finish();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleAlarm(Alarm alarm) {
        String[] parts = alarm.getTime().split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("time", alarm.getTime());
        intent.putExtra("label", alarm.getLabel());
        intent.putExtra("alarm_id", alarm.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                alarm.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );
    }

    private void shareAlarm() {
        String time = editAlarmTime.getText().toString();
        String label = editLabel.getText().toString();

        String shareText = "Аларма: " + time + "\nЕтикет: " + label;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        startActivity(Intent.createChooser(shareIntent, "Сподели аларма чрез:"));
    }
}
