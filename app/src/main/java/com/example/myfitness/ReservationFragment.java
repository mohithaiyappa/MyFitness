package com.example.myfitness;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReservationFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private static final String CANCEL_TEXT = "キャンセル";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private final Calendar calendar = Calendar.getInstance();
    private TextView startDateTextView, endDateTextView, startTimeTextView, endTimeTextView, videoTotalTime;
    private CheckBox monday, tuesday, wednesday, thursday, friday, saturday, sunday;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);

        setListeners();

        initData();
        calculateTotalTime();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void bindViews(View view) {
        startDateTextView = view.findViewById(R.id.eventStartDate);
        endDateTextView = view.findViewById(R.id.eventEndDate);
        startTimeTextView = view.findViewById(R.id.eventStartTime);
        endTimeTextView = view.findViewById(R.id.eventEndTime);
        videoTotalTime = view.findViewById(R.id.videoTotalTime);

        //checkBox views
        monday = view.findViewById(R.id.mondayCheckBox);
        tuesday = view.findViewById(R.id.tuesdayCheckBox);
        wednesday = view.findViewById(R.id.wednesdayCheckBox);
        thursday = view.findViewById(R.id.thursdayCheckBox);
        friday = view.findViewById(R.id.fridayCheckBox);
        saturday = view.findViewById(R.id.saturdayCheckBox);
        sunday = view.findViewById(R.id.sundayCheckBox);
    }

    private void setListeners() {

        //checkBox Listeners
        monday.setOnCheckedChangeListener(this);
        tuesday.setOnCheckedChangeListener(this);
        wednesday.setOnCheckedChangeListener(this);
        thursday.setOnCheckedChangeListener(this);
        friday.setOnCheckedChangeListener(this);
        saturday.setOnCheckedChangeListener(this);
        sunday.setOnCheckedChangeListener(this);

        //datePickers
        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog((TextView) v);
            }
        });
        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog((TextView) v);
            }
        });

        //timePickers
        startTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog((TextView) v);
            }
        });
        endTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog((TextView) v);
            }
        });

    }

    private void initData() {
        startDateTextView.setText(dateFormat.format(calendar.getTime()));
        endDateTextView.setText(dateFormat.format(calendar.getTime()));
        startTimeTextView.setText(timeFormat.format(calendar.getTime()));
        endTimeTextView.setText(timeFormat.format(calendar.getTime()));
    }

    private void calculateTotalTime() {
        videoTotalTime.setText("00:00:00");
    }

    private void showTimePickerDialog(TextView textView) {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String formattedTime = String.format(Locale.US, "%02d:%02d", hourOfDay, minute);
                        textView.setText(formattedTime);
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    private void showDatePickerDialog(TextView textView) {
        Calendar cal = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DAY_OF_MONTH);

        //return if getActivity returns null
        Context context = getActivity();
        if (context == null) return;

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);

                textView.setText(dateFormat.format(calendar.getTime()));
            }
        }, year, month, date);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, CANCEL_TEXT, datePickerDialog);
        datePickerDialog.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            buttonView.setTextColor(Color.WHITE);
        } else {
            buttonView.setTextColor(Color.BLACK);
        }
    }
}
