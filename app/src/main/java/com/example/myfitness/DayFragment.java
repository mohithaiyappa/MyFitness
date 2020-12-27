package com.example.myfitness;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DayFragment extends Fragment {


    private DayEventsAdapter dayEventsAdapter;
    private RecyclerView recyclerView;
    private TextView dayFragmentHeadingText;


    public DayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_day, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.dayEventsRecyclerView);
        dayFragmentHeadingText = view.findViewById(R.id.dayFragmentHeading);
        dayEventsAdapter = new DayEventsAdapter(this.getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setAdapter(dayEventsAdapter);
        recyclerView.setLayoutManager(layoutManager);

        EventRepo.getInstance().getDayEventsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                dayEventsAdapter.submitList(events);
                recyclerView.invalidate();
            }
        });
        EventRepo.getInstance().getSelectedDate().observe(getViewLifecycleOwner(), new Observer<Date>() {
            @Override
            public void onChanged(Date date) {
                loadDayFragmentHeadingText(date);
                EventRepo.getInstance().loadSelectedDayEvents(date);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Date date = EventRepo.getInstance().getSelectedDate().getValue();
        if (date != null)
            EventRepo.getInstance().loadSelectedDayEvents(date);

    }

    private void loadDayFragmentHeadingText(Date date) {
        dayFragmentHeadingText.setText(getDate(date) + " " + getDay(date));
    }

    private String getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int str = cal.get(Calendar.DAY_OF_WEEK);
        switch (str) {
            case Calendar.SUNDAY:
                return " (日)";
            case Calendar.MONDAY:
                return " (月)";
            case Calendar.TUESDAY:
                return " (火)";
            case Calendar.WEDNESDAY:
                return " (水)";
            case Calendar.THURSDAY:
                return " (木)";
            case Calendar.FRIDAY:
                return " (⾦)";
            case Calendar.SATURDAY:
                return " (土)";
        }
        return "";
    }

    private String getDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日", Locale.US);
        return dateFormat.format(date);
    }

    private boolean hasSelectedDayPassed() {
        boolean dayPassed = false;
        Date selectedDate = EventRepo.getInstance().getSelectedDate().getValue();
        Calendar cal = Calendar.getInstance();
        if (selectedDate == null) return true;
        cal.setTime(selectedDate);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        boolean hasEventTimePassed = Calendar.getInstance().getTime().after(cal.getTime());
        if (hasEventTimePassed) dayPassed = true;
        return dayPassed;
    }
}