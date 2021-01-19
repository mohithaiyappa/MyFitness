package com.example.myfitness.repository;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alamkanak.weekview.WeekViewEvent;
import com.example.myfitness.model.Event;
import com.example.myfitness.model.Notification;
import com.example.myfitness.model.User;
import com.example.myfitness.model.VideoData;
import com.example.myfitness.network.RetrofitEvent;
import com.example.myfitness.utils.EventAlarmManager;
import com.example.myfitness.utils.WeekEventConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventRepo {

    private static final MutableLiveData<List<Event>> eventsLiveData = new MutableLiveData<>();
    private static final MutableLiveData<List<Event>> dayEventsLiveData = new MutableLiveData<>();
    private static final MutableLiveData<List<Event>> alarmEventsLiveData = new MutableLiveData<>();
    private static final MutableLiveData<Pair<Boolean, Spannable>> notificationData = new MutableLiveData<>();
    private static final MutableLiveData<Date> selectedDate = new MutableLiveData<>();
    private static final MutableLiveData<Boolean> membershipStatus = new MutableLiveData<>(true);
    public static String userName;
    private static EventRepo eventRepoInstance;
    public MutableLiveData<List<Event>> allEventsLiveData = new MutableLiveData<>();
    public List<Event> allEvents = new ArrayList<>();
    public List<WeekViewEvent> allWeekViewEvents = new ArrayList<>();
    public MutableLiveData<List<WeekViewEvent>> allWeekViewEventsLiveData = new MutableLiveData<>();

    private static final MutableLiveData<Event> createOrEditEvent = new MutableLiveData<>();

    public static List<String> downloadedVideosIds = new ArrayList<>();

    public boolean shouldReloadWeekViewEvents = false;
    public boolean shouldReloadDayEvents = false;

    //private constructor
    private EventRepo() {
        allEventsLiveData.setValue(allEvents);
        allWeekViewEventsLiveData.setValue(allWeekViewEvents);
    }

    //Singleton pattern
    public static EventRepo getInstance() {
        if (eventRepoInstance == null) {
            eventRepoInstance = new EventRepo();
            selectedDate.setValue(Calendar.getInstance(Locale.US).getTime());
        }
        return eventRepoInstance;
    }

    //this loads events for month view
    public void loadEvents(int year, int month) {
        RetrofitEvent.getEventApi().getEvents(year, month, userName).enqueue(new Callback<List<Event>>() {

            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if ((!response.isSuccessful()) || response.body() == null) {
                    eventsLiveData.setValue(Collections.emptyList());
                    return;
                }
                Log.d("MyFitness229789", "posting value: " + response.body().size() + " <- size");

                new Thread() {
                    @Override
                    public void run() {
                        Collections.sort(response.body(), getComparator());
                        eventsLiveData.postValue(response.body());
                        Log.d("MyFitness229789", "posting value: " + response.body().size());
                        for (Event event :
                                response.body()) {
                            Log.d("MyFitness229789", "run: " + event.toString());
                        }
                    }
                }.start();
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                eventsLiveData.setValue(Collections.emptyList());
            }
        });
    }

    //this loads events for the week view
    public void loadWeekViewEvents(int year, int month) {
        RetrofitEvent.getEventApi().getEvents(year, month, userName).enqueue(new Callback<List<Event>>() {

            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if ((!response.isSuccessful()) || response.body() == null) {
                    //todo handle unsuccessful or null case
                    return;
                }

                new Thread() {
                    @Override
                    public void run() {
                        for (Event event : response.body()) {
                            if (!allEvents.contains(event)) {
                                allEvents.add(event);
                            }
                        }
                        allEventsLiveData.postValue(allEvents);
                        Log.d("MyFitness229789", "posting value: ");
                    }
                }.start();
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                //todo handle failure
            }
        });
    }

    //loads events for day for the auto play events
    public void loadAlarmEvents() {
        Calendar cal = Calendar.getInstance();
        RetrofitEvent.getEventApi().getDayEvents(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                userName,
                cal.get(Calendar.DAY_OF_MONTH)).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                new Thread() {
                    @Override
                    public void run() {
                        Collections.sort(response.body(), getComparator());
                        alarmEventsLiveData.postValue(response.body());
                    }
                }.start();
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {

            }
        });
    }

    public void loadAlarmEvents(Callback<List<Event>> alarmCallback) {
        Calendar cal = Calendar.getInstance();
        RetrofitEvent.getEventApi().getDayEvents(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                userName,
                cal.get(Calendar.DAY_OF_MONTH)).enqueue(alarmCallback);
    }

    //loads events for the selected day in day fragment
    //todo remove later if unnecessary
    public void loadDayEvents(Date date) {
        List<Event> dayEventList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String dateString = dateFormat.format(date);
        for (Event event : getEventsList()) {
            if (event.getEventDate().equals(dateString)) {
                dayEventList.add(event);
            }
        }
        dayEventsLiveData.setValue(dayEventList);
        selectedDate.setValue(date);
    }

    public void loadUserDetails() {
        RetrofitEvent.getEventApi().getUserDetails().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (!response.isSuccessful()) return;
                new Thread() {
                    @Override
                    public void run() {
                        for (User user : response.body()) {
                            if (user.getUserId().equals(userName)) {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                                    Date membershipDate = sdf.parse(user.getExpDate());
                                    Date currentDate = new Date();
                                    if (membershipDate.before(currentDate)) {
                                        membershipStatus.postValue(false);
                                    }

                                } catch (ParseException | NullPointerException parseException) {
                                    parseException.printStackTrace();
                                }

                            }
                        }
                    }
                }.start();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
    }

    public void loadSelectedDayEvents(Date date) {
        Calendar cal = Calendar.getInstance();
        if (date == null) {
            dayEventsLiveData.postValue(Collections.emptyList());
            return;
        }
        cal.setTime(date);
        RetrofitEvent.getEventApi().getDayEvents(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                userName,
                cal.get(Calendar.DAY_OF_MONTH)).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (!response.isSuccessful()) return;
                new Thread() {
                    @Override
                    public void run() {
                        if (response.body() == null) {
                            dayEventsLiveData.postValue(Collections.emptyList());
                        } else {
                            Collections.sort(response.body(), getComparator());
                            dayEventsLiveData.postValue(response.body());
                        }
                    }
                }.start();
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                dayEventsLiveData.postValue(Collections.emptyList());
            }
        });

    }

    public void loadEventWithDetails(int eId) {
        RetrofitEvent.getEventApi().getEventWithCompleteDetails(userName, eId).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (!response.isSuccessful() || response.body().isEmpty()) {
                    return;
                }

                Event event = response.body().get(0);
                event.setEventStartDate(event.getEventDate());
                event.setEventEndDate(event.getEventDate());
                createOrEditEvent.postValue(event);
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {

            }
        });
    }

    public void deleteEvent(int e_id, Event event, Context context) {
        RetrofitEvent.getEventApi().deleteEvent(userName, e_id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                EventAlarmManager.getInstance().resetAlarm(context);
                Log.d("TestingDelete", "onResponse: in OnResponse ");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("TestingDelete", "onFailure: could not delete item " + t);
            }
        });
        try {
            List<Event> newAllEvents = eventsLiveData.getValue();
            if (newAllEvents != null) {
                newAllEvents.remove(event);
            }
            eventsLiveData.setValue(newAllEvents);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            allEvents.remove(event);
            allEventsLiveData.setValue(allEvents);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            WeekViewEvent weekViewEvent = WeekEventConverter.getInstance().getWeekEventConverter(event);
            allWeekViewEvents.remove(weekViewEvent);
            allWeekViewEventsLiveData.setValue(allWeekViewEvents);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void loadNotifications() {
        RetrofitEvent.getEventApi().getNotifications().enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                new Thread() {
                    @Override
                    public void run() {
                        boolean shouldWrap = true;
                        if (response.body().size() > 2) shouldWrap = false;
                        Pair<Boolean, Spannable> pairNotificationData = new Pair<>(
                                shouldWrap,
                                makeSpannable(response.body()));
                        notificationData.postValue(pairNotificationData);
                    }
                }.start();

            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Log.d("MyFitness", "onFailure: loading notifications failed");
            }
        });

    }

    public void loadDownloadedVideoIds() {
        RetrofitEvent.getEventApi().getDownloadedVideoIds(userName).enqueue(new Callback<List<VideoData>>() {
            @Override
            public void onResponse(Call<List<VideoData>> call, Response<List<VideoData>> response) {
                downloadedVideosIds.clear();
                if (!response.isSuccessful() && response.body().isEmpty()) {
                    return;
                }

                for (VideoData vData : response.body()) {
                    downloadedVideosIds.add(vData.getVideoId());
                }
                for (String id : downloadedVideosIds) {
                    Log.d("testingDownloadId", "onResponse: loadDownloadedVideoIds" + id);
                }
            }

            @Override
            public void onFailure(Call<List<VideoData>> call, Throwable t) {

            }
        });
    }

    public void deleteFile(String videoId) {
        RetrofitEvent.getEventApi().deleteFile(userName, videoId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loadDownloadedVideoIds();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private Spannable makeSpannable(List<Notification> notifications) {
        SpannableStringBuilder finalSpannable = new SpannableStringBuilder();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        boolean start = true;
        for (Notification notification : notifications) {

            spannableStringBuilder.clear();
            int dateTextLength = notification.getNotificationDate().length();
            if (!start) {
                dateTextLength++;
                spannableStringBuilder.append("\n");
            }
            spannableStringBuilder.append(notification.getNotificationDate());
            //commenting this if in the future the text needs to be modified modify it here
            /**
             spannableStringBuilder.setSpan(
             new ForegroundColorSpan(Color.BLACK),
             0, dateTextLength,
             Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
             spannableStringBuilder.setSpan(
             new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
             0, dateTextLength,
             Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
             */

            spannableStringBuilder.append("   ");
            spannableStringBuilder.append(notification.getNotificationText());
            finalSpannable.append(spannableStringBuilder);
            start = false;

        }

        return finalSpannable;
    }

    public void clearAllWeekEvents() {
        allEvents.clear();
        allEventsLiveData.setValue(allEvents);
        allWeekViewEvents.clear();
        allWeekViewEventsLiveData.setValue(allWeekViewEvents);
        shouldReloadWeekViewEvents = true;
    }

    public int currentYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    public int currentMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH) + 1;
    }

    public List<WeekViewEvent> getAllWeekViewEvents() {
        return allWeekViewEvents;
    }

    //observer asks for updating the WeekViewEvents when AllEvents changes
    public void updateWeekViewEvents() {
        if (allWeekViewEvents.size() != allEvents.size()) {
            try {
                List<WeekViewEvent> list = WeekEventConverter.getInstance().convert(allEvents);
                for (WeekViewEvent event : list) {
                    if (!allWeekViewEvents.contains(event)) {
                        allWeekViewEvents.add(event);
                    }
                }
                allWeekViewEventsLiveData.postValue(allWeekViewEvents);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadInitialEvents() {
        loadEvents(currentYear(),
                currentMonth());
    }

    public List<WeekViewEvent> getMatchedEvents(int year, int month) {
        Log.d("MyFitness2298", "getAllWeekViewEvents: " + allWeekViewEvents.size());
        List<WeekViewEvent> matchedEvents = new ArrayList<>();
        for (WeekViewEvent event : allWeekViewEvents) {
            if (eventMatches(event, year, month)) {
                matchedEvents.add(event);
            }
        }
        return matchedEvents;
    }

    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month - 1)
                || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }

    private Comparator<Event> getComparator() {
        return new Comparator<Event>() {
            @Override
            public int compare(Event e1, Event e2) {
                return e1.getStartTime().compareTo(e2.getStartTime());
            }
        };
    }

    public LiveData<Date> getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date date) {
        selectedDate.setValue(date);
    }

    public LiveData<List<Event>> getEventsLiveData() {
        return eventsLiveData;
    }

    public List<Event> getEventsList() {
        return eventsLiveData.getValue();
    }

    public LiveData<List<Event>> getDayEventsLiveData() {
        return dayEventsLiveData;
    }

    public LiveData<List<Event>> getAlarmEventsLiveData() {
        return alarmEventsLiveData;
    }

    public LiveData<Pair<Boolean, Spannable>> getNotificationData() {
        return notificationData;
    }

    public LiveData<Boolean> getMembershipStatusLiveData() {
        return membershipStatus;
    }

    public void setCreateOrEditEvent(Event event) {
        createOrEditEvent.postValue(event);
    }

    public LiveData<Event> getCreateOrEditEventLiveData() {
        return createOrEditEvent;
    }

    public void resetMembershipStatus() {
        membershipStatus.postValue(true);
    }
}
