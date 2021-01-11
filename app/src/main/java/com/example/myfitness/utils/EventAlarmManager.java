package com.example.myfitness.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.myfitness.model.Event;
import com.example.myfitness.repository.EventRepo;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventAlarmManager {

    private static EventAlarmManager instance;
    private final List<PendingIntent> pendingIntentList = new ArrayList<>();
    private AlarmManager alarmManager;

    private EventAlarmManager() {
    }

    /**
     * PUBLIC METHODS
     */

    public static EventAlarmManager getInstance() {
        if (instance == null) {
            instance = new EventAlarmManager();
        }
        return instance;
    }

    public void resetAlarm(Context context) {
        EventRepo.getInstance().loadAlarmEvents(getAlarmCallback(context));
    }

    public void removeAlarm(Context context) {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        for (PendingIntent pi : pendingIntentList) {
            if (alarmManager != null) {
                alarmManager.cancel(pi);
            }
        }
        pendingIntentList.clear();
    }

    /**
     * PRIVATE METHODS
     */

    private void setAllEventsAlarm(List<Event> events, Context context) {
        removeAlarm(context);
        if (events == null || events.isEmpty()) return;
        for (Event event : events) {
            setEventAlarm(event, context);
        }
    }

    private void setEventAlarm(Event e, Context context) {

        try {
            Calendar cal = getCalendar(e.getEventDate(), e.getStartTime());
            boolean hasEventTimePassed = Calendar.getInstance().getTime().after(cal.getTime());
            if (!hasEventTimePassed) {
                long time = cal.getTimeInMillis();
                setAlarm(time, e, context);
            } else {
                Log.d("MyFitness", "event time is over: " + cal.getTime());
            }
        } catch (ParseException parseException) {
            parseException.printStackTrace();
            Log.d("MyFitness", "setupEventsAlarm: parse exception" + parseException);
        }
    }

    private void setAlarm(long time, Event event, Context context) {
        //getting the alarm manager
        if (alarmManager == null)
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //creating a new intent specifying the broadcast receiver
        Intent i = new Intent(context, EventBroadcastReceiver.class);
        i.putExtra("event", (new Gson()).toJson(event));

        //creating a pending intent using the intent
        PendingIntent pi = PendingIntent.getBroadcast(context, event.getE_id(), i, 0);
        pendingIntentList.add(pi);

        //setting the alarm that will be fired every day
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pi);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pi);
        }
    }

    private Calendar getCalendar(String stringDate, String stringTime) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat.parse(stringDate.trim() + " " + stringTime.trim());

        if (date == null) {
            throw new ParseException("error", 3);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    private Callback<List<Event>> getAlarmCallback(Context context) {
        return new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                setAllEventsAlarm(response.body(), context);
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {

            }
        };
    }
}
