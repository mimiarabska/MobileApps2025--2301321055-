package com.maria.myalarm.data.repository;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.maria.myalarm.R;
import com.maria.myalarm.data.model.Alarm;
import com.maria.myalarm.data.repository.AlarmRepository;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private final List<Alarm> alarmList;
    private final AlarmRepository repository;

    public AlarmAdapter(List<Alarm> alarmList, AlarmRepository repository) {
        this.alarmList = alarmList;
        this.repository = repository;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = alarmList.get(position);

        // Показваме час и label
        holder.timeText.setText(alarm.getTime());
        holder.labelText.setText(alarm.getLabel());

        // За да не тригерираме listener при повторно bind
        holder.alarmSwitch.setOnCheckedChangeListener(null);
        holder.alarmSwitch.setChecked(alarm.isEnabled());

        // Listener за включване/изключване
        holder.alarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            alarm.setEnabled(isChecked);   // обновяваме обекта
            repository.update(alarm);      // записваме в базата
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        TextView labelText;
        Switch alarmSwitch;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.alarmTimeText);
            labelText = itemView.findViewById(R.id.alarmLabel);
            alarmSwitch = itemView.findViewById(R.id.alarmSwitch);
        }
    }
}
