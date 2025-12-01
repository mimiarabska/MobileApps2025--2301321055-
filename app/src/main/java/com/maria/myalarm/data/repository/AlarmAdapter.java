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

    // 1) Listener интерфейс
    public interface OnAlarmLongClickListener {
        void onAlarmLongClick(Alarm alarm, int position);
    }

    // 2) Listener поле
    private OnAlarmLongClickListener longClickListener;

    // 3) Setter
    public void setOnAlarmLongClickListener(OnAlarmLongClickListener listener) {
        this.longClickListener = listener;
    }
    public AlarmAdapter(List<Alarm> alarmList, AlarmRepository repository) {
        this.alarmList = alarmList;
        this.repository = repository;
    }

    public interface OnAlarmClickListener {
        void onAlarmClick(Alarm alarm, int position);
    }

    private OnAlarmClickListener clickListener;

    public void setOnAlarmClickListener(OnAlarmClickListener listener) {
        this.clickListener = listener;
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

    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        TextView labelText;

        Switch alarmSwitch;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);

            timeText = itemView.findViewById(R.id.alarmTimeText);
            labelText = itemView.findViewById(R.id.alarmLabel);
            alarmSwitch = itemView.findViewById(R.id.alarmSwitch);

            // Нормален CLICK (за редакция)
            itemView.setOnClickListener(v -> {
                if (clickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    clickListener.onAlarmClick(
                            alarmList.get(getAdapterPosition()),
                            getAdapterPosition()
                    );
                }
            });

            // ДЪЛЪГ CLICK (за триене)
            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    longClickListener.onAlarmLongClick(
                            alarmList.get(getAdapterPosition()),
                            getAdapterPosition()
                    );
                }
                return true;
            });
        }

    }
}
