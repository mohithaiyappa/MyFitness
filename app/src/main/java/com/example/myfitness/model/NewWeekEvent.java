package com.example.myfitness.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewWeekEvent {
    public String id = "";
    public String eventDisplayName = "";
    public Calendar eventStartCalendar;
    public Calendar eventEndCalendar;
    public List<String> eventString = new ArrayList<>();

    public NewWeekEvent(String id,
                        String eventDisplayName,
                        Calendar eventStartCalendar,
                        Calendar eventEndCalendar) {
        this.id = id;
        this.eventDisplayName = eventDisplayName;
        this.eventStartCalendar = eventStartCalendar;
        this.eventEndCalendar = eventEndCalendar;
        makingEventString();
    }

    public void makingEventString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(eventStartCalendar.getTime());
        int min = getQuadrantInt(calendar.get(Calendar.MINUTE));
        calendar.add(Calendar.MINUTE, -(calendar.get(Calendar.MINUTE)));
        calendar.add(Calendar.MINUTE, min);
        String timeString;
        while (calendar.compareTo(eventEndCalendar) < 0) {
            timeString = String.format("%02d:%02d",
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE));
            eventString.add(timeString);
            Log.d("TestingWeek", "makingEventString: " + timeString);
            calendar.add(Calendar.MINUTE, 15);
        }
    }

    public int getQuadrantInt(int min) {
        if (min > 45) return 45;
        else if (min > 30) return 30;
        else if (min > 15) return 15;
        else return 0;
    }

    //getters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventDisplayName() {
        return eventDisplayName;
    }

    public void setEventDisplayName(String eventDisplayName) {
        this.eventDisplayName = eventDisplayName;
    }

    public Calendar getEventStartCalendar() {
        return eventStartCalendar;
    }

    public void setEventStartCalendar(Calendar eventStartCalendar) {
        this.eventStartCalendar = eventStartCalendar;
    }

    public Calendar getEventEndCalendar() {
        return eventEndCalendar;
    }

    public void setEventEndCalendar(Calendar eventEndCalendar) {
        this.eventEndCalendar = eventEndCalendar;
    }

    public int getStartHour() {
        return eventStartCalendar.get(Calendar.HOUR_OF_DAY);
    }

    //setters

    public int getStartMin() {
        return eventStartCalendar.get(Calendar.MINUTE);
    }

    public int getEndHour() {
        return eventEndCalendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getEndMin() {
        return eventEndCalendar.get(Calendar.MINUTE);
    }

    public int getDayOfWeek() {
        return eventStartCalendar.get(Calendar.DAY_OF_WEEK);
    }
}
