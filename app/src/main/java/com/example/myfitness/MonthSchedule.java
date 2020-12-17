package com.example.myfitness;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MonthSchedule extends Fragment {

    private TextView titleText;
    private Button prevButton, nextButton;
    private CalendarAdapter mCalendarAdapter;
    private DayHeadingTextAdapter dayHeadingTextAdapter;
    private GridView calendarGridView, headingGridView;
    private boolean initialLoad = true;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_month_schedule, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        titleText = view.findViewById(R.id.titleText);
        prevButton = view.findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarAdapter.prevMonth();
                titleText.setText(mCalendarAdapter.getTitle());
            }
        });
        nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarAdapter.nextMonth();
                titleText.setText(mCalendarAdapter.getTitle());
            }
        });
        headingGridView = view.findViewById(R.id.calendarHeadingGridView);
        dayHeadingTextAdapter = new DayHeadingTextAdapter(this.getActivity());
        headingGridView.setAdapter(dayHeadingTextAdapter);
        calendarGridView = view.findViewById(R.id.calendarGridView);
        mCalendarAdapter = new CalendarAdapter(this.getActivity());
        calendarGridView.setAdapter(mCalendarAdapter);
        titleText.setText(mCalendarAdapter.getTitle());
        calendarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Date selectedDate = (Date) mCalendarAdapter.getItem(position);
                if (mCalendarAdapter.getDateManager().isCurrentMonth(selectedDate)) {
                    mCalendarAdapter.clickedDate(view, selectedDate);
                    EventRepo.getInstance().setSelectedDate(selectedDate);
                }
            }
        });
        EventRepo.getInstance().getEventsLiveData().observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                mCalendarAdapter.updateList(events);
                if (initialLoad) {
                    EventRepo.getInstance().setSelectedDate(Calendar.getInstance().getTime());
                    initialLoad = false;
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mCalendarAdapter.reloadEvents();
    }
}

