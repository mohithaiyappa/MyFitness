package com.example.myfitness;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alamkanak.weekview.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventRepo {

    private static EventRepo eventRepoInstance;
    private static final MutableLiveData<List<Event>> eventsLiveData = new MutableLiveData<>();
    private static final MutableLiveData<List<Event>> dayEventsLiveData = new MutableLiveData<>();
    private static final MutableLiveData<List<Event>> alarmEventsLiveData = new MutableLiveData<>();
    private static final MutableLiveData<Date> selectedDate = new MutableLiveData<>();
    public static String userName;
    public MutableLiveData<List<Event>> allEventsLiveData = new MutableLiveData<>();
    public List<Event> allEvents = new ArrayList<>();
    public List<WeekViewEvent> allWeekViewEvents = new ArrayList<>();
    public MutableLiveData<List<WeekViewEvent>> allWeekViewEventsLiveData = new MutableLiveData<>();

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
                new Thread() {
                    @Override
                    public void run() {
                        Collections.sort(response.body(), getComparator());
                        eventsLiveData.postValue(response.body());
                        Log.d("MyFitness229789", "posting value: ");
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
    public void loadAlarmEvents(){
        Calendar cal = Calendar.getInstance();
        RetrofitEvent.getEventApi().getDayEvents(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH)+1,
                userName,
                cal.get(Calendar.DAY_OF_MONTH)).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if(!response.isSuccessful()|| response.body()==null) return;
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

    //loads events for the selected day in day fragment
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

    public void deleteEvent(int e_id,Event event){
        RetrofitEvent.getEventApi().deleteEvent(userName,e_id).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) { }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d("MyFitness", "onFailure: could not delete item "+t);
            }

        });
        try {
            List<Event> newAllEvents = eventsLiveData.getValue();
            newAllEvents.remove(event);
            eventsLiveData.postValue(newAllEvents);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

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
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void loadInitialEvents(){
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
}
