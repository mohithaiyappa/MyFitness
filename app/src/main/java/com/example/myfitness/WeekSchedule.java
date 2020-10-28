package com.example.myfitness;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WeekSchedule extends Fragment {
    private final String TAG ="MyFitness229";
    private WeekView mWeekView;
    private Button prevButton,nextButton;
    private Date date = new Date();
    private Calendar mCalendar = Calendar.getInstance();
    private TextView titleText;

    private MutableLiveData<List<WeekViewEvent>> weekEventsLiveData = new MutableLiveData<>();
    private int counter =0;


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        List<WeekViewEvent> events = new ArrayList<>();
        weekEventsLiveData.setValue(events);
        return inflater.inflate(R.layout.activity_week_schedule,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
        setCalendar();
        mWeekView = (WeekView) view.findViewById(R.id.weekView);
        prevButton = (Button) view.findViewById(R.id.prevButton);
        nextButton = (Button) view.findViewById(R.id.nextButton);
        titleText = (TextView) view.findViewById(R.id.titleTextWeek);
        mWeekView.canScrollHorizontally(-1);
        mWeekView.canScrollHorizontally(1);
        mWeekView.setDefaultEventColor(Color.parseColor("#F5DEB3"));
        setListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        mWeekView.goToDate(mCalendar);
        mWeekView.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        setHeadingDate();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    public void setCalendar(){
        mCalendar.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        mCalendar.clear(Calendar.MINUTE);
        mCalendar.clear(Calendar.SECOND);
        mCalendar.clear(Calendar.MILLISECOND);

        // set start of the week
        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    }

    public void setListeners(){
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                mCalendar.add(Calendar.WEEK_OF_YEAR, -1);
                //titleText.setText(mCalendar.get(Calendar.WEEK_OF_YEAR));
                mWeekView.goToDate(mCalendar);
                mWeekView.setFirstDayOfWeek(Calendar.MONDAY);
                setHeadingDate();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar.add(Calendar.WEEK_OF_YEAR, 1);
               // titleText.setText(mCalendar.get(Calendar.WEEK_OF_YEAR));
                mWeekView.goToDate(mCalendar);
                mWeekView.setFirstDayOfWeek(Calendar.MONDAY);
                setHeadingDate();
            }
        });

        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {

            }
        });

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        /*Runnable runnable = new Runnable(){

            @Override
            public void run() {
                mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
                    @Override
                    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                        return getEvents(newYear,newMonth);
                    }
                });

            }
        };
        new Thread(runnable);*/
        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                EventRepo.getInstance().loadWeekViewEvents(newYear, newMonth);
                return EventRepo.getInstance().getMatchedEvents(newYear,newMonth);
                //return getEvents(newYear,newMonth);
            }
        });

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(new WeekView.EventLongPressListener() {
            @Override
            public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

            }
        });

        mWeekView.setXScrollingSpeed(0f);

        //todo complete date time interceptor
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                return getDay(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                if(hour > 12){
                    hour = hour-12;
                    return (hour)+ " PM   ";
                }
                return (hour)+ " AM   ";
            }
        });

        mWeekView.setHeaderRowBackgroundColor(Color.parseColor("#ffffff"));

        EventRepo.getInstance().allEventsLiveData.observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                EventRepo.getInstance().updateWeekViewEvents();
                Log.d("MyFitness2298", "getAllWeekViewEvents: "+EventRepo.getInstance().allWeekViewEvents.size());
                Log.d("MyFitness2298", "observing all events live data. size of event list"+events.size());
            }
        });

        EventRepo.getInstance().allWeekViewEventsLiveData.observe(this, new Observer<List<WeekViewEvent>>() {
            @Override
            public void onChanged(List<WeekViewEvent> weekViewEvents) {
                Log.d("MyFitness2298", "getAllWeekViewEvents: "+EventRepo.getInstance().allWeekViewEvents.size());
                mWeekView.notifyDatasetChanged();
            }
        });
    }



    public Calendar getCalendar(String stringDate,String stringTime) throws ParseException {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm:ss");
        Date date = dateFormat.parse(stringDate.trim()+" "+stringTime.trim());
        Calendar cal = Calendar.getInstance();
        if(date!=null) cal.setTime(date);
        return cal;
    }

    private String getDay(Date date) {
        String dateString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        dateString = dateFormat.format(date) + "\n";
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int str =cal.get(Calendar.DAY_OF_WEEK);
        switch (str){
            case Calendar.SUNDAY : dateString= dateString+ "\n(日)"; break;
            case Calendar.MONDAY : dateString= dateString+ "\n(月)";break;
            case Calendar.TUESDAY : dateString= dateString+ "\n(火)";break;
            case Calendar.WEDNESDAY : dateString= dateString+ "\n(水)";break;
            case Calendar.THURSDAY : dateString= dateString+ "\n(木)";break;
            case Calendar.FRIDAY : dateString= dateString+ "\n(⾦)";break;
            case Calendar.SATURDAY : dateString= dateString+ "\n(土)";break;
        }
        return dateString;
    }

    public void setHeadingDate(){
        Date d = mCalendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM", Locale.US);
        titleText.setText(dateFormat.format(d));
    }

    interface Notifier{
        public void onUpdateFinished();
    }
}