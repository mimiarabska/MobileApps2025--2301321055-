package com.maria.myalarm.data.repository;

import android.content.Context;

import com.maria.myalarm.data.AppDatabase;
import com.maria.myalarm.data.model.Alarm;

import java.util.List;

public class AlarmRepository {

    private final AlarmDao alarmDao;

    public AlarmRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        alarmDao = database.alarmDao();
    }

    // Добавяне на аларма
    public void insert(Alarm alarm) {
        alarmDao.insert(alarm);
    }

    // Обновяване на аларма
    public void update(Alarm alarm) {
        alarmDao.update(alarm);
    }

    // Изтриване на аларма
    public void delete(Alarm alarm) {
        alarmDao.delete(alarm);
    }

    // Изтриване на всички аларми
    public void deleteAll() {
        alarmDao.deleteAll();
    }

    // Връщане на всички аларми
    public List<Alarm> getAllAlarms() {
        return alarmDao.getAllAlarms();
    }
}
