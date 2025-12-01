package com.maria.myalarm.data.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.maria.myalarm.data.model.Alarm;

import java.util.List;

@Dao
public interface AlarmDao {

    // Вмъкване на нова аларма
    @Insert
    long insert(Alarm alarm);

    // Обновяване на съществуваща аларма
    @Update
    void update(Alarm alarm);

    // Изтриване на аларма
    @Delete
    void delete(Alarm alarm);

    // Изтриване на всички аларми
    @Query("DELETE FROM alarms")
    void deleteAll();

    // Взимане на всички аларми
    @Query("SELECT * FROM alarms ORDER BY time ASC")
    List<Alarm> getAllAlarms();

    @Query("SELECT * FROM alarms WHERE id = :alarmId LIMIT 1")
    Alarm getAlarmById(int alarmId);

}
