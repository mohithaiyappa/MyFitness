package com.example.myfitness;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeekSchedule extends Fragment {
    private final Calendar  mCalendar = Calendar.getInstance();
    private WeekView    mWeekView;
    private Button      prevButton, nextButton;
    private TextView    titleText;


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_week_schedule,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCalendar();
        mWeekView   = view.findViewById(R.id.weekView);
        prevButton  = view.findViewById(R.id.prevButton);
        nextButton  = view.findViewById(R.id.nextButton);
        titleText   = view.findViewById(R.id.titleTextWeek);
        mWeekView.setDefaultEventColor(Color.parseColor("#F5DEB3"));
        setListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        mWeekView.goToDate(mCalendar);
        mWeekView.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        setHeadingDate();
        mWeekView.notifyDatasetChanged();
    }

    private void setCalendar(){
        mCalendar.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        mCalendar.clear(Calendar.MINUTE);
        mCalendar.clear(Calendar.SECOND);
        mCalendar.clear(Calendar.MILLISECOND);

        // set start of the week
        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    }

    private void setListeners(){

        setButtonClickListeners();

        setWeekViewListeners();

        setLiveDataObservers();
    }

    private void setButtonClickListeners(){

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar.add(Calendar.WEEK_OF_YEAR, -1);
                mWeekView.goToDate(mCalendar);
                mWeekView.setFirstDayOfWeek(Calendar.MONDAY);
                setHeadingDate();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar.add(Calendar.WEEK_OF_YEAR, 1);
                mWeekView.goToDate(mCalendar);
                mWeekView.setFirstDayOfWeek(Calendar.MONDAY);
                setHeadingDate();
            }
        });
    }

    private void setWeekViewListeners(){

        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {
                Intent createEventActivityIntent = new Intent(getContext(),CreateEventActivity.class);
                startActivity(createEventActivityIntent);
            }
        });

        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                EventRepo.getInstance().loadWeekViewEvents(newYear, newMonth);
                return EventRepo.getInstance().getMatchedEvents(newYear,newMonth);
            }
        });

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(new WeekView.EventLongPressListener() {
            @Override
            public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

            }
        });

        mWeekView.setXScrollingSpeed(0f);

        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                return getFormattedDayText(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return (hour)+ "    ";
            }
        });

        mWeekView.setHeaderRowBackgroundColor(Color.parseColor("#ffffff"));
    }

    private void setLiveDataObservers(){

        EventRepo.getInstance().allEventsLiveData.observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                EventRepo.getInstance().updateWeekViewEvents();
            }
        });

        EventRepo.getInstance().allWeekViewEventsLiveData.observe(this, new Observer<List<WeekViewEvent>>() {
            @Override
            public void onChanged(List<WeekViewEvent> weekViewEvents) {
                mWeekView.notifyDatasetChanged();
            }
        });
    }

    private String getFormattedDayText(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        String dateString = dateFormat.format(date) + "\n";
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int str =cal.get(Calendar.DAY_OF_WEEK);
        switch (str){
            case Calendar.SUNDAY    : dateString= dateString+ "\n(日)";  break;
            case Calendar.MONDAY    : dateString= dateString+ "\n(月)";  break;
            case Calendar.TUESDAY   : dateString= dateString+ "\n(火)";  break;
            case Calendar.WEDNESDAY : dateString= dateString+ "\n(水)";  break;
            case Calendar.THURSDAY  : dateString= dateString+ "\n(木)";  break;
            case Calendar.FRIDAY    : dateString= dateString+ "\n(⾦)";  break;
            case Calendar.SATURDAY  : dateString= dateString+ "\n(土)";  break;
        }
        return dateString;
    }

    private void setHeadingDate(){
        Date d = mCalendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月", Locale.US);
        titleText.setText(dateFormat.format(d));
    }

}