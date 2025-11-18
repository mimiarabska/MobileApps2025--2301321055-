package com.maria.myalarm;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maria.myalarm.data.model.Alarm;
import com.maria.myalarm.data.repository.AlarmAdapter;
import com.maria.myalarm.data.repository.AlarmRepository;

import java.util.ArrayList;
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

        //Зареждаме adapter
        adapter = new AlarmAdapter(alarmList);
        alarmRecyclerView.setAdapter(adapter);

        // 5) Listener за "Добави аларма"
        addAlarmButton.setOnClickListener(v -> openTimePicker());
    }

    //Избиране на час-
    private void openTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hour, minute) -> askForLabel(hour, minute),
                7,
                30,
                true
        );

        timePickerDialog.show();
    }

    // Диалог за label-а
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

        builder.setNegativeButton("Отказ", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    //Създаване на аларма
    private void createNewAlarm(int hour, int minute, String label) {
        String time = String.format("%02d:%02d", hour, minute);

        Alarm alarm = new Alarm(time, label, true);

        repository.insert(alarm);   // запис в базата
        alarmList.add(alarm);       // добавяне в списъка

        adapter.notifyDataSetChanged();  // обновяване на екрана
    }
}
