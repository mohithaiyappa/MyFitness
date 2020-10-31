package com.example.myfitness;

import com.alamkanak.weekview.WeekViewEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;

public class WeekEventConverter {

    private static WeekEventConverter instance;

    private  WeekEventConverter(){ }

    public static WeekEventConverter getInstance(){
        //todo testing concurrence issue so provide new instance always
        /*if(instance == null) {
            instance = new WeekEventConverter();
        }
        return instance;*/
        return new WeekEventConverter();
    }

    public List<WeekViewEvent> convert(List<Event> events){
        List<WeekViewEvent> weekEvents = new CopyOnWriteArrayList<>();
        for (Event event: events) {
            try {
                weekEvents.add(getWeekEventConverter(event));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return weekEvents;
    }

    public WeekViewEvent getWeekEventConverter(Event event) throws ParseException{
        String endTimeStr = getEndTime(event.getStartTime(),event.getVideoTime());
        return new WeekViewEvent(event.getE_id(),
                    getFormattedEventString(event,endTimeStr),
                    getCalendar(event.getEventDate(), event.getStartTime()),
                    getCalendar(event.getEventDate(),endTimeStr));
    }

    private String getEndTime(String startTime, String videoTime) throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date1 = timeFormat.parse(startTime);
        Date date2 = timeFormat.parse(videoTime);

        long sum;
        sum = date1.getTime() + date2.getTime();

        String date3 = timeFormat.format(new Date(sum));
        return date3;
    }

    public Calendar getCalendar(String stringDate, String stringTime) throws ParseException {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat.parse(stringDate.trim()+" "+stringTime.trim());
        Calendar cal = Calendar.getInstance();
        if(date!=null) cal.setTime(date);
        return cal;
    }

    public String getFormattedEventString(Event event,String endTimeString){
        return event.getVideoTitle()
                +"\n"
                +event.getStartTime()
                +" ~ "
                +endTimeString
                +"\n"
                +event.getIrName();
    }
}
