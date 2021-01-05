package com.example.myfitness;

import android.app.Instrumentation;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Spannable;
import android.util.Log;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//init v3
public class WeekSchedule extends Fragment implements View.OnTouchListener {
    private final Calendar mCalendar = Calendar.getInstance();
    private WeekView mWeekView;
    private Button prevButton, nextButton, todayButton, startTimeButton, endTimeButton;
    private TextView titleText;
    private boolean hasMoved = false;
    private TextView notificationTextView;
    private ScrollView notificationScrollView;
    private MaterialCheckBox checkBox;
    private int hourHeight = 240;


    private int startTimeHour = 9;
    private int endTimeHour = 23;
    private int flingStepCount = 150;
    private int hourDifference = 2;

    private float flingY = -5020f;

    private volatile boolean autoScroll = false;


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_week_schedule, container, false);
        view.setOnTouchListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCalendar();
        mWeekView = view.findViewById(R.id.weekView);
        prevButton = view.findViewById(R.id.prevButton);
        nextButton = view.findViewById(R.id.nextButton);
        todayButton = view.findViewById(R.id.goToTodayWeek);
        titleText = view.findViewById(R.id.titleTextWeek);
        checkBox = view.findViewById(R.id.checkbox);
        notificationTextView = view.findViewById(R.id.notificationTextView);
        notificationScrollView = view.findViewById(R.id.notificationScrollView);
        startTimeButton = view.findViewById(R.id.wvStartTimeButton);
        endTimeButton = view.findViewById(R.id.wvEndTimeButton);
        mWeekView.setDefaultEventColor(Color.parseColor("#3F7388"));
        setListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (EventRepo.getInstance().shouldReloadWeekViewEvents) {
            mWeekView.notifyDatasetChanged();
            EventRepo.getInstance().shouldReloadWeekViewEvents = false;
        }
        EventRepo.getInstance().clearAllWeekEvents();
        mWeekView.notifyDatasetChanged();
        setHeadingDate();
        comeBackToToday();

        EventRepo.getInstance().getNotificationData().observe(this, new Observer<Pair<Boolean, Spannable>>() {
            @Override
            public void onChanged(Pair<Boolean, Spannable> booleanSpannablePair) {
                setNotificationText(booleanSpannablePair);
            }
        });

        int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        setButtonText(startTimeButton, hourOfDay, 0);
        if (hourOfDay >= 22)
            setButtonText(endTimeButton, 0, 0);
        else
            setButtonText(endTimeButton, hourOfDay + 2, 0);
    }

    private void setCalendar() {
        mCalendar.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        mCalendar.clear(Calendar.MINUTE);
        mCalendar.clear(Calendar.SECOND);
        mCalendar.clear(Calendar.MILLISECOND);

        // set start of the week
        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    }

    private void setListeners() {

        setButtonClickListeners();

        setWeekViewListeners();

        setLiveDataObservers();
    }

    private void setButtonClickListeners() {

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

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar.setTime(Calendar.getInstance().getTime());
                setCalendar();
                if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    mCalendar.add(Calendar.WEEK_OF_YEAR, -1);
                    mWeekView.goToDate(mCalendar);
                    mWeekView.setFirstDayOfWeek(Calendar.MONDAY);
                    setHeadingDate();
                } else {
                    mWeekView.goToDate(mCalendar);
                    mWeekView.setFirstDayOfWeek(Calendar.MONDAY);
                }
                mWeekView.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //scrolls to end
                    ((TabActivity) getActivity()).disableTabScrolling();
                    autoScroll = true;
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                if (mWeekView == null) return;
                                float bot = mWeekView.getBottom();
                                fling(500f, 500f, bot, flingY, flingStepCount);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    ((TabActivity) getActivity()).enableTabScrolling();
                    //stop scrolling
                    autoScroll = false;

                }
            }
        });

        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour = Integer.parseInt(startTimeButton.getText().subSequence(0, 2).toString());
                int minute = Integer.parseInt(startTimeButton.getText().subSequence(3, 5).toString());
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        //String formatted = String.format(Locale.US,"%02d:%02d", selectedHour,selectedMinute);
                        //startTimeButton.setText( formatted);
                        setButtonText((Button) v, selectedHour, selectedMinute);
                        startTimeHour = selectedHour;
                        setWeekViewHeight();
                    }
                }, hour, minute, true);//Yes 24 hour time
                //mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour = Integer.parseInt(endTimeButton.getText().subSequence(0, 2).toString());
                int minute = Integer.parseInt(endTimeButton.getText().subSequence(3, 5).toString());

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        //String formatted = String.format("%02d:%02d", selectedHour,selectedMinute);
                        //endTimeButton.setText( formatted);
                        setButtonText((Button) v, selectedHour, selectedMinute);
                        endTimeHour = selectedHour;
                        if (selectedMinute > 0) endTimeHour++;
                        setWeekViewHeight();
                    }
                }, hour, minute, true);//Yes 24 hour time
                //mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }

    private void setWeekViewListeners() {

        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {
                if (hasEventTimePassed(event)) return;

                Intent createEventActivityIntent = new Intent(getContext(), edit_reservation.class);


                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String dateStr = dateFormat.format(event.getStartTime().getTime());

                createEventActivityIntent.putExtra("user_id", EventRepo.userName);
                createEventActivityIntent.putExtra("date", dateStr);
                createEventActivityIntent.putExtra("e_id", String.valueOf(event.getId()));


                startActivity(createEventActivityIntent);
            }
        });

        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                EventRepo.getInstance().loadWeekViewEvents(newYear, newMonth);
                return EventRepo.getInstance().getMatchedEvents(newYear, newMonth);
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
                return (hour) + ":00    ";
            }
        });

        mWeekView.setEmptyViewClickListener(new WeekView.EmptyViewClickListener() {
            @Override
            public void onEmptyViewClicked(Calendar time) {
                boolean after = time.after(Calendar.getInstance());
                if (!after) {
                    //don't open activity
                    return;
                }
                Intent createEventActivityIntent = new Intent(getContext(), edit_reservation.class);

                //todo send time along with date here
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String dateStr = dateFormat.format(time.getTime());

                createEventActivityIntent.putExtra("user_id", EventRepo.userName);
                createEventActivityIntent.putExtra("date", dateStr);


                startActivity(createEventActivityIntent);

            }
        });

        mWeekView.setHeaderRowBackgroundColor(Color.parseColor("#ffffff"));
    }

    private void setLiveDataObservers() {

        EventRepo.getInstance().allEventsLiveData.observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                EventRepo.getInstance().updateWeekViewEvents();
            }
        });

        EventRepo.getInstance().allWeekViewEventsLiveData.observe(getViewLifecycleOwner(), new Observer<List<WeekViewEvent>>() {
            @Override
            public void onChanged(List<WeekViewEvent> weekViewEvents) {
                mWeekView.notifyDatasetChanged();
            }
        });
    }

    private String getFormattedDayText(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.US);
        String dateString = dateFormat.format(date) + "\n";
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int str = cal.get(Calendar.DAY_OF_WEEK);
        switch (str) {
            case Calendar.SUNDAY:
                dateString = dateString + "\n(日)";
                break;
            case Calendar.MONDAY:
                dateString = dateString + "\n(月)";
                break;
            case Calendar.TUESDAY:
                dateString = dateString + "\n(火)";
                break;
            case Calendar.WEDNESDAY:
                dateString = dateString + "\n(水)";
                break;
            case Calendar.THURSDAY:
                dateString = dateString + "\n(木)";
                break;
            case Calendar.FRIDAY:
                dateString = dateString + "\n(⾦)";
                break;
            case Calendar.SATURDAY:
                dateString = dateString + "\n(土)";
                break;
        }
        return dateString;
    }

    private void setHeadingDate() {
        Date d = mCalendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月", Locale.US);
        titleText.setText(dateFormat.format(d));
    }

    private void ifSundayShowPrevWeek() {
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && !hasMoved) {
            mCalendar.add(Calendar.WEEK_OF_YEAR, -1);
            mWeekView.goToDate(mCalendar);
            mWeekView.setFirstDayOfWeek(Calendar.MONDAY);
            setHeadingDate();
            hasMoved = true;
        }
    }

    private void comeBackToToday() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setCalendar();
                mWeekView.goToDate(mCalendar);
                mWeekView.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                ifSundayShowPrevWeek();
            }
        }, 100);
    }

    private void setNotificationText(Pair<Boolean, Spannable> booleanSpannablePair) {
        notificationTextView.setText("");
        try {
            // boolean shouldWrap = booleanSpannablePair.first;
            Spannable notificationText = booleanSpannablePair.second;/*

            if (!shouldWrap) {
                float dp = getResources().getDisplayMetrics().density;
                ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        (int) (35 * dp));

                notificationScrollView.setLayoutParams(lp);


            } else {
                ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                notificationScrollView.setLayoutParams(lp);
            }*/

            notificationTextView.setText(notificationText);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean hasEventTimePassed(WeekViewEvent event) {
        return Calendar.getInstance().getTime().after(event.getStartTime().getTime());
    }

    private void scrollToEnd() {
        // Obtain MotionEvent object
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 1000;
        float x = 0.0f;
        float y = 0.0f;
        float centreX =/*mWeekView.getX() +*/ ((float) mWeekView.getWidth() / 2);
        float centreY =/*mWeekView.getY() + */((float) mWeekView.getHeight() / 2);
        // List of meta states found here:     developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_UP,
                centreX,
                centreY,
                metaState
        );

        // Dispatch touch event to view
        mWeekView.requestFocus();
        mWeekView.dispatchTouchEvent(motionEvent);
    }

    private void fling(
            Float fromX, Float toX, Float fromY,
            Float toY, int stepCount
    ) {

        Instrumentation inst = new Instrumentation();

        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();

        float y = fromY;
        float x = fromX;

        float yStep = (toY - fromY) / stepCount;
        float xStep = (toX - fromX) / stepCount;

        MotionEvent event = MotionEvent.obtain(
                downTime, eventTime,
                MotionEvent.ACTION_DOWN, fromX, fromY, 0
        );
        event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        inst.sendPointerSync(event);

        //First visible hour after scroll
        int firstVisibleHourAfterScroll = 24 - hourDifference;


        for (int i = 0; i < stepCount; i++/* in 0 until stepCount*/) {
            Log.d("WeekSchedule", "fling: " + mWeekView.getFirstVisibleHour());
            int currentFirstVisibleHour = round(mWeekView.getFirstVisibleHour());
            Log.d("WeekSchedule", "fling: rounded " + currentFirstVisibleHour);

            //End Scrolling Event if the first visible hour after scroll is reached
            if (firstVisibleHourAfterScroll == currentFirstVisibleHour) {
                mWeekView.goToHour(currentFirstVisibleHour);
                checkBox.setChecked(false);
                return;
            }

            //End scrolling event if autoScroll boolean is false -> autoScroll checkBox is unchecked
            if (!autoScroll) return;

            //Fling code
            y += yStep;
            x += xStep;
            eventTime = SystemClock.uptimeMillis();
            event = MotionEvent.obtain(
                    downTime, eventTime + stepCount,
                    MotionEvent.ACTION_MOVE, x, y, 0
            );
            event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
            inst.sendPointerSync(event);

            //Logging Fling Values
            Log.d("WeekSchedule", "fling: Logging -------------------------------\n "
                    + "\nfling: x " + x
                    + "\nfling: y " + y
                    + "\nfling: stepCount " + stepCount
                    + "\nfling: xStep " + xStep
                    + "\nfling: yStep " + yStep
                    + "\nfling: eventTime " + eventTime);
        }

        eventTime = SystemClock.uptimeMillis() + (stepCount) + 2;
        event = MotionEvent.obtain(
                downTime, eventTime,
                MotionEvent.ACTION_UP, toX, toY, 0
        );
        if (!autoScroll) return;

        event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        inst.sendPointerSync(event);

        //Set checkBox to unChecked indicating scroll event ended
        checkBox.setChecked(false);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (autoScroll) {
            autoScroll = false;
            checkBox.setChecked(false);
            return true;
        }
        return false;
    }

    private void setWeekViewHeight() {
        hourDifference = endTimeHour - startTimeHour;

        mWeekView.setHourHeight(calculateHourHeight());

        flingStepCount = calculateFlingStepCount();

        calculateFlingY();

        //Wait for weekView to finish resetting height
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mWeekView.goToHour(startTimeHour);
            }
        }, 500);

    }

    private int calculateHourHeight() {
        switch (hourDifference) {
            case 0:
            case 1:
                return 480; // return 480 for 0 and 1 hour
            case 2:
                return 240;
            case 3:
                return 160;
            case 4:
                return 120;
            case 5:
                return 100;
            case 6:
                return 80;
            case 7:
                return 70;
            default:
                return 60; // return 60 for 8 and more hours
        }

    }

    private int calculateFlingStepCount() {
        switch (hourDifference) {
            case 0:
            case 1:
                return 278; // return 480 for 0 and 1 hour
            case 2:
                return 150;
            case 3:
                return 200;
            case 4:
                return 240;
            case 5: /*return 300;*/
            case 6:
            case 7:
                return 300;
            default:
                return 300; // return 60 for 8 and more hours
        }

    }

    private void calculateFlingY() {
        switch (hourDifference) {
            case 0:
            case 1:
                flingY = -5020f * 2.5f; // return 480 for 0 and 1 hour
                break;
            default:
                flingY = -5020f; // return 60 for 8 and more hours
                break;
        }

    }

    private int round(double doubleValue) {
        int intValue = (int) doubleValue;
        double fraction = doubleValue - intValue;
        if (fraction > 0.8) intValue++;
        return intValue;
    }

    private void setButtonText(Button button, int hour, int min) {
        String formatted = String.format(Locale.US, "%02d:%02d", hour, min);
        button.setText(formatted);
    }
}