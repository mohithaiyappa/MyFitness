package com.example.myfitness;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DayFragment extends Fragment {

    private DayEventListAdapter dayEventListAdapter;
    private ListView listView;
    private TextView dayFragmentHeadingText;
    private FloatingActionButton fabButton;


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
        listView = view.findViewById(R.id.dayEventsListView);
        dayFragmentHeadingText = view.findViewById(R.id.dayFragmentHeading);
        fabButton = view.findViewById(R.id.fab);
        dayEventListAdapter = new DayEventListAdapter(this.getActivity());
        listView.setAdapter(dayEventListAdapter);
        EventRepo.getInstance().getDayEventsLiveData().observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                dayEventListAdapter.updateList(events);
            }
        });
        EventRepo.getInstance().getSelectedDate().observe(this, new Observer<Date>() {
            @Override
            public void onChanged(Date date) {
                loadDayFragmentHeadingText(date);
            }
        });
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),CreateEventActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadDayFragmentHeadingText(Date date) {
        dayFragmentHeadingText.setText("予約日 "+" "+getDate(date)+" "+getDay(date));
    }

    private String getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int str =cal.get(Calendar.DAY_OF_WEEK);
        switch (str){
            case Calendar.SUNDAY : return " (日)";
            case Calendar.MONDAY : return " (月)";
            case Calendar.TUESDAY : return " (火)";
            case Calendar.WEDNESDAY : return " (水)";
            case Calendar.THURSDAY : return " (木)";
            case Calendar.FRIDAY : return " (⾦)";
            case Calendar.SATURDAY : return " (土)";
        }
        return "";
    }

    private String getDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        return dateFormat.format(date);

    }
}