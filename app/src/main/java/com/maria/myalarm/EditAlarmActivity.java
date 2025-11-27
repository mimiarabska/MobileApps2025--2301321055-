package com.maria.myalarm;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.maria.myalarm.data.model.Alarm;
import com.maria.myalarm.data.repository.AlarmRepository;

public class EditAlarmActivity extends AppCompatActivity {

    private TextView editAlarmTime;
    private EditText editLabel;
    private Button changeTimeButton;
    private Button saveAlarmButton;

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

        // Попълване с текущите данни
        editAlarmTime.setText(alarm.getTime());
        editLabel.setText(alarm.getLabel());

        // Избор на нов час
        changeTimeButton.setOnClickListener(v -> openTimePicker());

        // Запис
        saveAlarmButton.setOnClickListener(v -> saveChanges());

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

        repository.update(alarm);
        finish(); // връща се към MainActivity
    }
}
