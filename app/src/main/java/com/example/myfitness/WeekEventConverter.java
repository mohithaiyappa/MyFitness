package com.example.myfitness;

import android.util.Log;

import com.alamkanak.weekview.WeekViewEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class WeekEventConverter {

    private static WeekEventConverter instance;
    private static final long TIME_30_MINUTE = 1800000;

    private void WeekEventConverter(){ }

    public static WeekEventConverter getInstance(){
        if(instance == null) {
            instance = new WeekEventConverter();
        }
        return instance;
    }

    public List<WeekViewEvent> convert(List<Event> events){
        List<WeekViewEvent> weekEvents = new ArrayList<>();
        for (Event event: events) {
            try {
                weekEvents.add(getWeekEventConverter(event));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return weekEvents;
    }

    public WeekViewEvent getWeekEventConverter(Event event) throws ParseException{
        return new WeekViewEvent(event.getE_id(),
                    event.getVideoTitle(),
                    getCalendar(event.getEventDate(), event.getStartTime()),
                    getCalendar(event.getEventDate(),getEndTime(event.getStartTime(),event.getVideoTime())));
    }

    private String getEndTime(String startTime, String videoTime) throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date1 = timeFormat.parse(startTime);
        Date date2 = timeFormat.parse(videoTime);

        long sum;
        if (date2.getTime() < TIME_30_MINUTE)
            sum =date1.getTime() + TIME_30_MINUTE;
        else  sum = date1.getTime() + date2.getTime();

        String date3 = timeFormat.format(new Date(sum));
        return date3;
    }

    public Calendar getCalendar(String stringDate, String stringTime) throws ParseException {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm:ss");
        Date date = dateFormat.parse(stringDate.trim()+" "+stringTime.trim());
        Calendar cal = Calendar.getInstance();
        if(date!=null) cal.setTime(date);
        return cal;
    }
}
