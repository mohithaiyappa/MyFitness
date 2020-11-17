package com.example.myfitness;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import com.example.myfitness.ui.main.SectionsPagerAdapter;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TabActivity extends AppCompatActivity {
    public static  int requestCode = 2456;
    private AlarmManager alarmManager;
    private List<PendingIntent> pendingIntentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        EventRepo.getInstance().loadInitialEvents();
        setupEventsAlarm();
    }

    private void setupEventsAlarm(){
        EventRepo.getInstance().loadAlarmEvents();
        EventRepo.getInstance().getAlarmEventsLiveData().observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                removeAlarm();
                if(events==null || events.isEmpty()) return;
                for(Event event : events){
                    setEventAlarm(event);
                }
            }
        });
    }
    private void setEventAlarm(Event e) {

        try {
            Calendar cal = getCalendar(e.getEventDate(),e.getStartTime());
            boolean hasEventTimePassed = Calendar.getInstance().getTime().after(cal.getTime());
            if (!hasEventTimePassed){
                long time = cal.getTimeInMillis();
                setAlarm(time, e);
            }else {
                Log.d("MyFitness", "event time is over: "+ cal.getTime());
            }
        } catch (ParseException parseException) {
            parseException.printStackTrace();
            Log.d("MyFitness", "setupEventsAlarm: parse exception"+parseException);
        }
    }

    private void setAlarm(long time,Event event){
        //getting the alarm manager
        if(alarmManager==null) alarmManager =(AlarmManager) getSystemService(Context.ALARM_SERVICE);

        requestCode++;
        //creating a new intent specifying the broadcast receiver
        Intent i = new Intent(this, EventBroadcastReceiver.class);
        i.putExtra("event", (new Gson()).toJson(event));

        //creating a pending intent using the intent
        PendingIntent pi = PendingIntent.getBroadcast(this, requestCode, i, 0);
        pendingIntentList.add(pi);

        //setting the alarm that will be fired every day
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time,pi);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time,pi);
        }
    }

    private void removeAlarm(){
        if(alarmManager==null) {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }
        for (PendingIntent pi : pendingIntentList) {
            if (alarmManager != null) {
                alarmManager.cancel(pi);
            }
        }
        pendingIntentList.clear();
    }

    public Calendar getCalendar(String stringDate,String stringTime) throws ParseException {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat.parse(stringDate.trim()+" "+stringTime.trim());
        if(date==null) {
            throw new ParseException("error",3);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    @Override
    protected void onDestroy() {
        removeAlarm();
        super.onDestroy();
    }
}