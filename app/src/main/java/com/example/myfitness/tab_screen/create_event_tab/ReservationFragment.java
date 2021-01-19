package com.example.myfitness.tab_screen.create_event_tab;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfitness.R;
import com.example.myfitness.customdialog.AcknowledgementDialog;
import com.example.myfitness.model.Event;
import com.example.myfitness.model.EventVideoDetails;
import com.example.myfitness.network.RetrofitEvent;
import com.example.myfitness.repository.EventRepo;
import com.example.myfitness.tab_screen.TabActivity;
import com.example.myfitness.utils.CustomViewPager;
import com.example.myfitness.utils.EventAlarmManager;
import com.example.myfitness.utils.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private static final String CANCEL_TEXT = "キャンセル";
    private static final String TAB_NAME = "スケジュール作成";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private final Calendar calendar = Calendar.getInstance();
    private TextView startDateTextView, endDateTextView, startTimeTextView, endTimeTextView, videoTotalTime, submitEvent;
    private CheckBox monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private final EventRepo eventRepo = EventRepo.getInstance();
    private RecyclerView recyclerView;
    private CreateEventsVideoDetailsAdapter adapter;

    private String mode = StringUtils.EVENT_MODE_SINGLE;

    private String videoIdsInOrder = "";
    private String selectedDateString;

    private Event currentEditingEvent;

    private SetVideoTime setVideoTimeInterface = new SetVideoTime() {
        @Override
        public void calculateAndSetTime(List<EventVideoDetails> videoArray) {
            calculateTotalTime(videoArray);
        }
    };

    private final Observer<Event> editEventObserver = new Observer<Event>() {
        @Override
        public void onChanged(Event event) {
            if (event == null) {
                currentEditingEvent = null;
                adapter.submitList(Collections.emptyList());
                return;
            }
            currentEditingEvent = event;
            adapter.submitList(event.getVideoArray());
            calculateTotalTime(event.getVideoArray());
            initData(event);
            String[] days = event.getDaysOnly().split(",");
            initCheckBox(days);
        }
    };

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

        initData(null);
        calculateTotalTime(Collections.emptyList());
    }

    @Override
    public void onResume() {
        super.onResume();

        //populate editing data after event repo loads the event with full details
        //observe a liveData of editEvent and on Change submit new list to recycler view
        setLiveDataListeners();
        populateEditingEventData();
    }

    @Override
    public void onPause() {
        removeLiveDataListeners();

        super.onPause();
    }

    private void bindViews(View view) {
        startDateTextView = view.findViewById(R.id.eventStartDate);
        endDateTextView = view.findViewById(R.id.eventEndDate);
        startTimeTextView = view.findViewById(R.id.eventStartTime);
        endTimeTextView = view.findViewById(R.id.eventEndTime);
        videoTotalTime = view.findViewById(R.id.videoTotalTime);
        recyclerView = view.findViewById(R.id.videoItems);
        submitEvent = view.findViewById(R.id.submitEvent);

        //checkBox views
        monday = view.findViewById(R.id.mondayCheckBox);
        tuesday = view.findViewById(R.id.tuesdayCheckBox);
        wednesday = view.findViewById(R.id.wednesdayCheckBox);
        thursday = view.findViewById(R.id.thursdayCheckBox);
        friday = view.findViewById(R.id.fridayCheckBox);
        saturday = view.findViewById(R.id.saturdayCheckBox);
        sunday = view.findViewById(R.id.sundayCheckBox);

        //setting up recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CreateEventsVideoDetailsAdapter(getActivity(), Collections.emptyList(), setVideoTimeInterface);
        recyclerView.setAdapter(adapter);

        //decide if you want to attach and detach observers in onResume and onPause or only once
        //setLiveDataListeners();
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
                showEndTimePickerDialog((TextView) v);
            }
        });

        //submit button
        submitEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentEditingEvent == null) return;
                int eId = currentEditingEvent.getE_id();
                if (currentEditingEvent.getE_id() == -1) {
                    uploadEvent();
                } else {
                    String id = Integer.toString(eId);
                    editExistingEvent(id);
                }


            }
        });

    }

    private void setLiveDataListeners() {
        eventRepo.getCreateOrEditEventLiveData().observe(getViewLifecycleOwner(), editEventObserver);
    }

    private void removeLiveDataListeners() {
        eventRepo.getCreateOrEditEventLiveData().removeObserver(editEventObserver);
    }

    private void initData(Event event) {
        if (event == null) {
            startDateTextView.setText(dateFormat.format(calendar.getTime()));
            endDateTextView.setText(dateFormat.format(calendar.getTime()));
            startTimeTextView.setText(timeFormat.format(calendar.getTime()));
            endTimeTextView.setText(timeFormat.format(calendar.getTime()));
            endTimeTextView.setText(StringUtils.TIME_EMPTY_STRING);
            setTabName(0);
        } else {
            startDateTextView.setText(event.getEventStartDate());
            endDateTextView.setText(event.getEventEndDate());
            startTimeTextView.setText(event.getStartTime());
            if (event.getEndTime() == null
                    || event.getEndTime().trim().equals("")
                    || event.getEndTime().equals(StringUtils.TIME_EMPTY_STRING)
                    || event.getMode().equals(StringUtils.EVENT_MODE_SINGLE))
                endTimeTextView.setText(StringUtils.TIME_EMPTY_STRING);
            else
                endTimeTextView.setText(event.getEndTime());
            setTabName(event.getVideoArray().size());
        }
    }

    private void populateEditingEventData() {

    }

    private void calculateTotalTime(List<EventVideoDetails> videoArray) {
        if (videoArray == null || videoArray.isEmpty())
            videoTotalTime.setText("00:00:00");
        else {

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            long sumOfTime = 0;
            Date date;
            for (EventVideoDetails videoDetails : videoArray) {
                try {
                    date = timeFormat.parse(videoDetails.getVideoTime());
                    sumOfTime += date.getTime();

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            videoTotalTime.setText(timeFormat.format(new Date(sumOfTime)));
        }
    }

    private void setTabName(int size) {
        if (size <= 0) {
            ((TabActivity) getActivity()).updateEditOrCreateTabHeading(TAB_NAME);
        } else {
            ((TabActivity) getActivity()).updateEditOrCreateTabHeading(TAB_NAME + " [" + size + "]");
        }
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
                        String formattedTime = String.format(Locale.US, "%02d:%02d:00", hourOfDay, minute);
                        textView.setText(formattedTime);
                    }
                }, mHour, mMinute, true);
        timePickerDialog.setButton(TimePickerDialog.BUTTON_NEGATIVE, CANCEL_TEXT, timePickerDialog);
        timePickerDialog.show();
    }

    private void showEndTimePickerDialog(TextView textView) {
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
                        String formattedTime = String.format(Locale.US, "%02d:%02d:00", hourOfDay, minute);
                        textView.setText(formattedTime);
                    }
                }, mHour, mMinute, true);
        timePickerDialog.setButton(TimePickerDialog.BUTTON_NEGATIVE, CANCEL_TEXT, timePickerDialog);
        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                textView.setText(StringUtils.TIME_EMPTY_STRING);
            }
        });
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

    private void initCheckBox(String[] days) {
        disableAllCheckBoxes();

        for (String day : days) {
            switch (day) {
                case "月":
                    monday.setChecked(true);
                    break;
                case "火":
                    tuesday.setChecked(true);
                    break;
                case "水":
                    wednesday.setChecked(true);
                    break;
                case "木":
                    thursday.setChecked(true);
                    break;
                case "金":
                    friday.setChecked(true);
                    break;
                case "土":
                    saturday.setChecked(true);
                    break;
                case "日":
                    sunday.setChecked(true);
                    break;
            }
        }
    }

    private void enableAllCheckBoxes() {
        monday.setChecked(true);
        tuesday.setChecked(true);
        wednesday.setChecked(true);
        thursday.setChecked(true);
        friday.setChecked(true);
        saturday.setChecked(true);
        sunday.setChecked(true);
    }

    private void disableAllCheckBoxes() {
        monday.setChecked(false);
        tuesday.setChecked(false);
        wednesday.setChecked(false);
        thursday.setChecked(false);
        friday.setChecked(false);
        saturday.setChecked(false);
        sunday.setChecked(false);
    }

    private void requirementsToMeet() {
        //TODO
        //at least one checkBox is true
        //determine mode :
        // if sum of video length is same as endTime then it is SINGLE EVENT comp hr and min
        // if the hr and min don't match then the event is REPEAT mode
        // also check event mode in event class if it in not null keep same event mode
    }

    private void submitEvent() {

    }

    //todo clear create event and all text views and disable submit button
    private void clearScreen() {

    }

    //return selected days
    private String getSelectedDaysText() {
        String dayText = "";
        if (monday.isChecked()) dayText += "月 ";
        if (tuesday.isChecked()) dayText += "火 ";
        if (wednesday.isChecked()) dayText += "水 ";
        if (thursday.isChecked()) dayText += "木 ";
        if (friday.isChecked()) dayText += "金 ";
        if (saturday.isChecked()) dayText += "土 ";
        if (sunday.isChecked()) dayText += "日 ";
        dayText = dayText.trim();
        dayText = dayText.replace(" ", ",");
        return dayText;
    }

    //todo return video ids in selected Order - DONE
    private String getVideoIdsInOrder() {
        return adapter.getVideoIds();
    }

    private String getEndTimeString() {
        String endTimeString = endTimeTextView.getText().toString().trim();
        if (endTimeString.isEmpty() || endTimeString.equals(StringUtils.TIME_EMPTY_STRING)) {
            mode = StringUtils.EVENT_MODE_SINGLE;
            endTimeString = addTotalVideoTimeToStartTimeAndReturn();
        } else {
            mode = StringUtils.EVENT_MODE_REPEAT;
        }
        return endTimeString;
    }

    //todo add functionality - little done
    private String addTotalVideoTimeToStartTimeAndReturn() {

        try {
            String time1 = startTimeTextView.getText().toString().trim();
            String time2 = videoTotalTime.getText().toString().trim();

            return getEndTime(time1, time2);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
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

    //todo check if videos are empty
    private void uploadEvent() {

        selectedDateString = startDateTextView.getText().toString().trim();

        RetrofitEvent.getEventApi().addEvent(EventRepo.userName,
                startDateTextView.getText().toString().trim(),
                endDateTextView.getText().toString().trim(),
                startTimeTextView.getText().toString().trim(),
                getEndTimeString(),
                getVideoIdsInOrder(),
                getSelectedDaysText(),
                mode).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    EventAlarmManager.getInstance().resetAlarm(getActivity());
                    clearScreen();
                    AcknowledgementDialog acknowledgementDialog = new AcknowledgementDialog(getContext(),
                            StringUtils.MESSAGE_EVENT_ADDED);
                    OnDismissListener dismissListener = new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            try {
                                Date date = dateFormat.parse(selectedDateString);
                                EventRepo.getInstance().setSelectedDate(date);
                                ((CustomViewPager) ((TabActivity) getActivity()).findViewById(R.id.view_pager)).moveTo(0);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }
                    };
                    acknowledgementDialog.setOnDismissListener(dismissListener);
                    acknowledgementDialog.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void editExistingEvent(String eId) {

        selectedDateString = startDateTextView.getText().toString().trim();
        Log.d("TestingUpload", "startDateTextView: " + startDateTextView.getText().toString().trim());
        Log.d("TestingUpload", "endDateTextView: " + endDateTextView.getText().toString().trim());
        Log.d("TestingUpload", "startTimeTextView: " + startTimeTextView.getText().toString().trim());
        Log.d("TestingUpload", "getEndTimeString: " + getEndTimeString());
        Log.d("TestingUpload", "getVideoIdsInOrder: " + getVideoIdsInOrder());
        Log.d("TestingUpload", "getSelectedDaysText: " + getSelectedDaysText());
        Log.d("TestingUpload", "mode: " + mode);
        Log.d("TestingUpload", "eId: " + eId);

        RetrofitEvent.getEventApi().editEvent(EventRepo.userName,
                startDateTextView.getText().toString().trim(),
                endDateTextView.getText().toString().trim(),
                startTimeTextView.getText().toString().trim(),
                getEndTimeString(),
                getVideoIdsInOrder(),
                getSelectedDaysText(),
                mode,
                eId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    EventAlarmManager.getInstance().resetAlarm(getActivity());
                    clearScreen();
                    AcknowledgementDialog acknowledgementDialog = new AcknowledgementDialog(getContext(),
                            StringUtils.MESSAGE_EVENT_ADDED);
                    OnDismissListener dismissListener = new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            try {
                                Date date = dateFormat.parse(selectedDateString);
                                EventRepo.getInstance().setSelectedDate(date);
                                ((CustomViewPager) ((TabActivity) getActivity()).findViewById(R.id.view_pager)).moveTo(0);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }
                    };
                    acknowledgementDialog.setOnDismissListener(dismissListener);
                    acknowledgementDialog.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            buttonView.setTextColor(Color.WHITE);
        } else {
            buttonView.setTextColor(Color.BLACK);
        }
    }

    interface SetVideoTime {
        void calculateAndSetTime(List<EventVideoDetails> videoArray);
    }
}
