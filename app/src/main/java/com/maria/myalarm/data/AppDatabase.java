package com.maria.myalarm.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.maria.myalarm.data.model.Alarm;
import com.maria.myalarm.data.repository.AlarmDao;

@Database(entities = {Alarm.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract AlarmDao alarmDao();

    // Singleton — гарантира, че имаме само една инстанция на базата
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "alarm_database"
                            ).allowMainThreadQueries() //за тестове
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
