package com.example.facebook;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.facebook.databinding.FragmentAlarmBinding;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmFragment extends Fragment {
    private FragmentAlarmBinding binding;
    private MaterialTimePicker picker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAlarmBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        binding.selectedTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });
        binding.setAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlarm();
            }
        });
        binding.cancelAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlarm();
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentDateandTime = sdf.format(new Date());
        binding.selectedTime.setText(currentDateandTime);
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void cancelAlarm() {
        //untuk menggagalkan alarm yang sudah disetel
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0,
                intent, 0);
        if (alarmManager == null) {
            alarmManager = (AlarmManager)
                    getActivity().getSystemService(Context.ALARM_SERVICE);
        }
        alarmManager.cancel(pendingIntent);
        Toast.makeText(getActivity(), "Alarm Cancelled",
                Toast.LENGTH_SHORT).show();
    }
    private void setAlarm() {
        //untuk menjalankan alarm yang sudah disetel
        alarmManager = (AlarmManager)
                getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0,
                intent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(getActivity(), "Alarm Set Successfully",
                Toast.LENGTH_SHORT).show();

    }
    private void showTimePicker() {
        //memunculkan dialog timepicker menggunakan library dari android
        picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();
        picker.show(getActivity().getSupportFragmentManager(), "AlarmManager");
        //mengeset waktu didalam view
        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (picker.getHour() > 12) {
                    binding.selectedTime.setText( String.format("%02d : %02d", picker.getHour(), picker.getMinute()));
                } else {
                    binding.selectedTime.setText(picker.getHour()+ " : " + picker.getMinute() + " ");
                }
                //menangkap inputan jam kalian lalu memulai alarm
                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                calendar.set(Calendar.MINUTE, picker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            }
        });
    }
    private void createNotificationChannel() {
        //mendeskripsikan channel notifikasi yang akan dibangun
        CharSequence name = "INI ALARM MANAGER";
        String description = "2018010 Arum Tri Pamungkas";
        //tingkat importance = high ( penting sekali )
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new
                NotificationChannel("AlarmManager", name, importance);
        channel.setDescription(description);
        //membuka izin pengaturan dari aplikasi untuk memulai service notifikasi
        NotificationManager notificationManager =
                getActivity().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
