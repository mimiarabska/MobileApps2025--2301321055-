package com.maria.myalarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maria.myalarm.data.model.Alarm;
import com.maria.myalarm.data.repository.AlarmAdapter;
import com.maria.myalarm.data.repository.AlarmRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView alarmRecyclerView;
    private Button addAlarmButton;

    private AlarmRepository repository;
    private AlarmAdapter adapter;
    private List<Alarm> alarmList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmRecyclerView = findViewById(R.id.alarmRecyclerView);
        addAlarmButton = findViewById(R.id.addAlarmButton);

        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        repository = new AlarmRepository(this);
        alarmList = repository.getAllAlarms();

        adapter = new AlarmAdapter(alarmList, repository);
        alarmRecyclerView.setAdapter(adapter);

        addAlarmButton.setOnClickListener(v -> openTimePicker());

        adapter.setOnAlarmLongClickListener((alarm, position) -> {
            showDeleteDialog(alarm, position);
        });

        adapter.setOnAlarmClickListener((alarm, position) -> {
            Intent intent = new Intent(MainActivity.this, EditAlarmActivity.class);
            intent.putExtra("alarm_id", alarm.getId());
            startActivity(intent);
        });
    }

    private void openTimePicker() {
        new android.app.TimePickerDialog(
                this,
                (view, hour, minute) -> askForLabel(hour, minute),
                7,
                30,
                true
        ).show();
    }

    private void askForLabel(int hour, int minute) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Име на алармата");

        final EditText input = new EditText(this);
        input.setHint("Пример: Ставане");
        builder.setView(input);

        builder.setPositiveButton("ОК", (dialog, which) -> {
            String label = input.getText().toString().trim();
            if (label.isEmpty()) label = "Аларма";

            createNewAlarm(hour, minute, label);
        });

        builder.setNegativeButton("Отказ", null);
        builder.show();
    }

    private void createNewAlarm(int hour, int minute, String label) {
        String time = String.format("%02d:%02d", hour, minute);

        Alarm alarm = new Alarm(time, label, true);
        repository.insert(alarm);

        alarmList.add(alarm);
        adapter.notifyDataSetChanged();

        scheduleAlarm(alarm);
    }

    @Override
    protected void onResume() {
        super.onResume();
        alarmList.clear();
        alarmList.addAll(repository.getAllAlarms());
        adapter.notifyDataSetChanged();
    }

    private void showDeleteDialog(Alarm alarm, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Изтриване?")
                .setMessage("Сигурни ли сте, че искате да изтриете алармата?")
                .setPositiveButton("Да", (dialog, which) -> {
                    cancelAlarm(alarm);
                    repository.delete(alarm);
                    alarmList.remove(position);
                    adapter.notifyItemRemoved(position);
                })
                .setNegativeButton("Не", null)
                .show();
    }

    private void cancelAlarm(Alarm alarm) {
        Intent intent = new Intent(this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                alarm.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    // ✔ Добавена проверка за Exact Alarm Permission (Android 12+)
    @SuppressLint("ScheduleExactAlarm")
    private void scheduleAlarm(Alarm alarm) {

        // Проверка за разрешение (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            if (!alarmManager.canScheduleExactAlarms()) {

                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);

                Toast.makeText(
                        this,
                        "Моля разрешете „Exact Alarms“ в настройките, за да може приложението да звъни.",
                        Toast.LENGTH_LONG
                ).show();

                return; // прекъсваме и изчакваме разрешение
            }
        }

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

}
