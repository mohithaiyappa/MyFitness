package com.example.myfitness;

import com.alamkanak.weekview.WeekViewEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        try {
            for (Event event : events) {
                try {
                    weekEvents.add(getWeekEventConverter(event));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return weekEvents;
    }

    public WeekViewEvent getWeekEventConverter(Event event) throws ParseException{
        //todo handle  endTime
        //todo decide between using end time in video length or use the end time
        //String endTimeStr = getEndTime(event.getStartTime(),event.getEndTime());//todo remove this if not necessary
        String endTimeStr = event.getEndTime();
        return new WeekViewEvent(event.getE_id(),
                    getFormattedEventString(event,endTimeStr),
                    getCalendar(event.getEventStartDate(), event.getStartTime()),
                    getCalendar(event.getEventStartDate(),endTimeStr));
    }

    private String getEndTime(String startTime, String videoTime) throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date1 = timeFormat.parse(startTime);
        Date date2 = timeFormat.parse(videoTime);

        long sum;
        sum = date1.getTime() + date2.getTime();

        return timeFormat.format(new Date(sum));
    }

    public Calendar getCalendar(String stringDate, String stringTime) throws ParseException {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat.parse(stringDate.trim()+" "+stringTime.trim());
        Calendar cal = Calendar.getInstance();
        if(date!=null) cal.setTime(date);
        return cal;
    }

    //todo remove endTime string if not necessary
    public String getFormattedEventString(Event event,String endTimeString){
        //todo make for loop and build the string
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(event.getStartTime().substring(0,5));
        stringBuilder.append(" ");
        stringBuilder.append(event.getMode());
        stringBuilder.append("\n\n");
        for(EventVideoDetails videoDetails : event.getVideoArray()){
            stringBuilder.append(videoDetails.getVideoTitle());
            stringBuilder.append("\n");
            stringBuilder.append(videoDetails.getIrName());
            stringBuilder.append("\n");
            stringBuilder.append(videoDetails.getVideoTime());
            stringBuilder.append("\n\n");
        }
        stringBuilder.append(event.getEndTime().substring(0,5));
        return stringBuilder.toString();


//        return event.getVideoArray().get(0).getVideoTitle()
//                +"\n"
//                +event.getStartTime()
//                +" ~ "
//                +endTimeString
//                +"\n"
//                +event.getVideoArray().get(0).getIrName();

    }
}
