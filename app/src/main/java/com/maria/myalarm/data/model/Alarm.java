package com.maria.myalarm.data.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarms")
public class Alarm {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String time; // формат: "07:30"
    private String label; // например "Ставане"
    private boolean isEnabled;

    public Alarm(String time, String label, boolean isEnabled) {
        this.time = time;
        this.label = label;
        this.isEnabled = isEnabled;
    }

    // --- Getters и Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }
}
