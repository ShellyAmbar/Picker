package com.example.picker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class CalenderActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private EditText edit_task,edit_task_title;
    private Button save_btn,set_alarm_btn;
    private String task_text,task_title;
    private int pickedYear,pickedMonth,pickedDay,pickedMinute,pickedHour;
    private Switch switch_timepicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        calendarView=findViewById(R.id.calendarView);
        edit_task=findViewById(R.id.edit_task);
        save_btn=findViewById(R.id.save_btn);
        switch_timepicker=findViewById(R.id.switch_timepicker);
        edit_task_title=findViewById(R.id.edit_task_title);
        set_alarm_btn=findViewById(R.id.alarm_btn);
        Calendar calendar =Calendar.getInstance();
        pickedDay=calendar.get(Calendar.DAY_OF_MONTH);
        pickedHour=calendar.get(Calendar.HOUR_OF_DAY);
        pickedMinute= Calendar.getInstance().getTime().getMinutes();
        pickedMonth=calendar.get(Calendar.MONTH);
        pickedYear=calendar.get(Calendar.YEAR);
        switch_timepicker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    switch_timepicker.setText(R.string.remove_time);
                    Calendar mcurrentTime = Calendar.getInstance();
                    final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(CalenderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            Toast.makeText(CalenderActivity.this, hourOfDay +": "+minute, Toast.LENGTH_SHORT).show();
                            pickedHour=hourOfDay;
                            pickedMinute=minute;

                        }
                    }, hour, minute,true);
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }else{

                    switch_timepicker.setText(R.string.add_time);
                    Calendar calendar1 =Calendar.getInstance();
                    pickedHour=calendar1.get(Calendar.HOUR_OF_DAY);
                    pickedMinute= Calendar.getInstance().getTime().getMinutes();

                }
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                pickedDay=dayOfMonth;
                pickedMonth=month+1;
                pickedYear=year;


            }
        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task_text=edit_task.getText().toString();
                task_title=edit_task_title.getText().toString();

                Calendar calendarBegin = Calendar.getInstance();
                calendarBegin.set(pickedYear,pickedMonth,pickedDay,pickedHour,pickedMinute,0);


             //   setAlarm(calendar.getTimeInMillis());
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendarBegin.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendarBegin.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, task_title)
                        .putExtra(CalendarContract.Events.DESCRIPTION, task_text)
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

                startActivity(intent);

            }
        });
        set_alarm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task_text=edit_task.getText().toString();
                task_title=edit_task_title.getText().toString();
                Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                        .putExtra(AlarmClock.EXTRA_MESSAGE, task_title)
                        .putExtra(AlarmClock.EXTRA_HOUR, pickedHour)
                        .putExtra(AlarmClock.EXTRA_MINUTES, pickedMinute);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }



}
