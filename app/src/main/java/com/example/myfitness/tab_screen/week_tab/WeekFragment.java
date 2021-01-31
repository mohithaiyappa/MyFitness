package com.example.myfitness.tab_screen.week_tab;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import com.example.myfitness.R;
import com.example.myfitness.model.Event;
import com.example.myfitness.model.NewWeekEvent;
import com.example.myfitness.network.RetrofitEvent;
import com.example.myfitness.repository.EventRepo;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeekFragment extends Fragment {

    private static final String CANCEL_TEXT = "キャンセル";

    private RecyclerView recyclerView;
    private WeekRecyclerAdapter adapter;
    private RecyclerView.SmoothScroller smoothScroller;
    //private NestedScrollView scrollView;
    private LayoutManager layoutManager;
    private Calendar mCalendar = Calendar.getInstance();

    private Button prevButton, nextButton, todayButton;

    private TextView titleTextView, notificationTextView;
    private TextView monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private TextView wvStartTimeButton, wvEndTimeButton;

    private int selectedHour = 9;

    private CheckBox checkBox;

    private ProgressDialog progressDialog;

    private List<NewWeekEvent> events = new ArrayList<>();

    public MutableLiveData<List<NewWeekEvent>> eventsLiveData = new MutableLiveData<>(events);

    //simple date formats
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat headingFormat = new SimpleDateFormat("yyyy年MM月", Locale.getDefault());
    private SimpleDateFormat dayHeadingFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());

    private int globalCounter = 0;
    private Handler handler = new Handler();

    private View.OnClickListener prevClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("buttonTesting", "onClick: " + mCalendar.getTime().toString());
            mCalendar.add(Calendar.WEEK_OF_YEAR, -1);
            Log.d("buttonTesting", "onClick: " + mCalendar.getTime().toString());
            clearAndLoadData();
        }
    };

    private View.OnClickListener nextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("buttonTesting", "onClick: " + mCalendar.getTime().toString());
            mCalendar.add(Calendar.WEEK_OF_YEAR, 1);
            Log.d("buttonTesting", "onClick: " + mCalendar.getTime().toString());
            clearAndLoadData();
        }
    };

    private View.OnClickListener goToTodayListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCalendar = Calendar.getInstance();
            clearAndLoadData();
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!checkBox.isChecked()) handler.removeCallbacks(this);
            smoothScroller.setTargetPosition(globalCounter);
            layoutManager.startSmoothScroll(smoothScroller);
            globalCounter++;
            if (globalCounter > 14) {
                //checkBox.setChecked(false);
                globalCounter = 0;
                //return;
            }
            handler.postDelayed(this, 1000);
        }
    };

    private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                globalCounter = selectedHour;
                handler.postDelayed(runnable, 1000);
            } else handler.removeCallbacks(runnable);

        }
    };

    private View.OnClickListener startTimeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            handler.removeCallbacks(runnable);
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            selectedHour = hourOfDay;
                            String formattedTime = String.format(Locale.US, "%02d:%02d", hourOfDay, minute);
                            wvStartTimeButton.setText(formattedTime);
                            int endHour = hourOfDay + 9;
                            if (endHour > 23) endHour = 23;
                            String formattedEndTime = String.format(Locale.US, "%02d:%02d", endHour, minute);
                            wvEndTimeButton.setText(formattedEndTime);
                            smoothScroller.setTargetPosition(hourOfDay);
                            layoutManager.startSmoothScroll(smoothScroller);

                        }
                    }, selectedHour, 0, true);
            timePickerDialog.setButton(TimePickerDialog.BUTTON_NEGATIVE, CANCEL_TEXT, timePickerDialog);
            DialogInterface.OnShowListener showListener = new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {
                    timePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.LTGRAY);
                    timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.java));
                }
            };
            timePickerDialog.setOnShowListener(showListener);
            timePickerDialog.show();
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_week, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView = view.findViewById(R.id.recyclerView);
        //scrollView = view.findViewById(R.id.weekViewScroll);
        titleTextView = view.findViewById(R.id.titleTextWeek);
        notificationTextView = view.findViewById(R.id.notificationTextView);
        wvStartTimeButton = view.findViewById(R.id.wvStartTimeButton);
        wvEndTimeButton = view.findViewById(R.id.wvEndTimeButton);
        checkBox = view.findViewById(R.id.checkbox);

        monday = view.findViewById(R.id.mondayLayout);
        tuesday = view.findViewById(R.id.tuesdayLayout);
        wednesday = view.findViewById(R.id.wednesdayLayout);
        thursday = view.findViewById(R.id.thursdayLayout);
        friday = view.findViewById(R.id.fridayLayout);
        saturday = view.findViewById(R.id.saturdayLayout);
        sunday = view.findViewById(R.id.sundayLayout);

        prevButton = view.findViewById(R.id.prevButton);
        nextButton = view.findViewById(R.id.nextButton);
        todayButton = view.findViewById(R.id.goToTodayWeek);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);

        prevButton.setOnClickListener(prevClickListener);
        nextButton.setOnClickListener(nextClickListener);
        todayButton.setOnClickListener(goToTodayListener);

        checkBox.setOnCheckedChangeListener(checkedChangeListener);

        adapter = new WeekRecyclerAdapter(events);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        smoothScroller = new
                LinearSmoothScroller(getContext()) {
                    @Override
                    protected int getVerticalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_START;
                    }
                };

        wvStartTimeButton.setOnClickListener(startTimeListener);


        eventsLiveData.observe(getViewLifecycleOwner(), new Observer<List<NewWeekEvent>>() {
            @Override
            public void onChanged(List<NewWeekEvent> newWeekEvents) {
                if (adapter != null)
                    adapter.updateData(newWeekEvents, mCalendar.get(Calendar.WEEK_OF_YEAR));
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

            }
        });
        EventRepo.getInstance().getNotificationData().observe(getViewLifecycleOwner(), new Observer<Pair<Boolean, Spannable>>() {
            @Override
            public void onChanged(Pair<Boolean, Spannable> booleanSpannablePair) {
                setNotificationText(booleanSpannablePair);
            }
        });

    }

    private void setNotificationText(Pair<Boolean, Spannable> booleanSpannablePair) {

        notificationTextView.setText("");
        Spannable notificationText = booleanSpannablePair.second;
        notificationTextView.setText(notificationText);
    }

    @Override
    public void onResume() {
        super.onResume();

        mCalendar = Calendar.getInstance();
        if (mCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
            clearAndLoadData();
        else loadPrevWeek();

//        scrollView.smoothScrollTo(0,scrollView.getChildAt(0).getHeight());

//        smoothScroller.setTargetPosition(9);
//        layoutManager.startSmoothScroll(smoothScroller);
        smoothScroller.setTargetPosition(selectedHour);
        layoutManager.startSmoothScroll(smoothScroller);
    }

    @Override
    public void onPause() {
        checkBox.setChecked(false);
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    public void loadPrevWeek() {
        Calendar todayCalendar = Calendar.getInstance();
        if (todayCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) return;
        mCalendar.add(Calendar.WEEK_OF_YEAR, -1);
        clearAndLoadData();
    }

    public void clearAndLoadData() {
        events.clear();
        mCalendar.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        mCalendar.clear(Calendar.MINUTE);
        mCalendar.clear(Calendar.SECOND);
        mCalendar.clear(Calendar.MILLISECOND);

        // set start of the week
        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        titleTextView.setText(headingFormat.format(mCalendar.getTime()));
        setDayHeaderText();
        progressDialog.show();
        new Thread() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mCalendar.getTime());
                loadData(calendar);
                eventsLiveData.postValue(events);
            }
        }.start();


    }

    public void setDayHeaderText() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(mCalendar.getTime());
        monday.setText(dayHeadingFormat.format(cal.getTime()) + " (月)");
        cal.add(Calendar.DAY_OF_MONTH, 1);
        tuesday.setText(dayHeadingFormat.format(cal.getTime()) + " (火)");
        cal.add(Calendar.DAY_OF_MONTH, 1);
        wednesday.setText(dayHeadingFormat.format(cal.getTime()) + " (水)");
        cal.add(Calendar.DAY_OF_MONTH, 1);
        thursday.setText(dayHeadingFormat.format(cal.getTime()) + " (木)");
        cal.add(Calendar.DAY_OF_MONTH, 1);
        friday.setText(dayHeadingFormat.format(cal.getTime()) + " (金)");
        cal.add(Calendar.DAY_OF_MONTH, 1);
        saturday.setText(dayHeadingFormat.format(cal.getTime()) + " (土)");
        cal.add(Calendar.DAY_OF_MONTH, 1);
        sunday.setText(dayHeadingFormat.format(cal.getTime()) + " (日)");

    }

    public void loadData(Calendar calendar) {

        for (int i = 0; i < 7; i++) {
            try {
                loadDataOfDaySync(calendar);
            } catch (IOException e) {
                e.printStackTrace();
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void loadDataOfDaySync(Calendar calendar) throws IOException {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        List<Event> normalEvents = RetrofitEvent.getEventApi().getDayEvents(year,
                month,
                EventRepo.userName,
                day).execute().body();
        NewWeekEvent newWeekEvent;
        for (Event event : normalEvents) {
            try {

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(event.getStartTime().substring(0, 5))
                        .append("~")
                        .append(event.getEndTime().substring(0, 5))
                        .append(" ");
                try {
                    stringBuilder.append(event.getVideoArray().get(0).getVideoTitle().substring(0, 7))
                            .append("...");
                } catch (NullPointerException | IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                Calendar startTimeCal = Calendar.getInstance();
                Date startDate = dateFormat.parse(event.getEventDate().trim() + " " +
                        event.getStartTime().trim());
                startTimeCal.setTime(startDate);
                Calendar endTimeCal = Calendar.getInstance();
                Date endDate = dateFormat.parse(event.getEventDate().trim() + " " +
                        event.getEndTime().trim());
                endTimeCal.setTime(endDate);
                newWeekEvent = new NewWeekEvent(event.getE_id(),
                        stringBuilder.toString(),
                        startTimeCal,
                        endTimeCal);
                events.add(newWeekEvent);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
