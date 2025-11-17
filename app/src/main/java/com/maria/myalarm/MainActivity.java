package com.maria.myalarm;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maria.myalarm.data.model.Alarm;
import com.maria.myalarm.data.repository.AlarmAdapter;
import com.maria.myalarm.data.repository.AlarmRepository;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView alarmRecyclerView;
    private AlarmRepository repository;
    private AlarmAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1) свързваме RecyclerView от XML
        alarmRecyclerView = findViewById(R.id.alarmRecyclerView);
        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2) създаваме Room репозитори
        repository = new AlarmRepository(this);

        // 3) взимаме всички аларми от базата
        List<Alarm> alarmList = repository.getAllAlarms();

        // 4) даваме ги на Adapter
        adapter = new AlarmAdapter(alarmList);

        // 5) свързваме Adapter с RecyclerView
        alarmRecyclerView.setAdapter(adapter);
    }
}

