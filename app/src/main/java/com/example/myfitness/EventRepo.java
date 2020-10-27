package com.example.myfitness;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
    private static MutableLiveData<Date> selectedDate = new MutableLiveData<>();

    //private constructor
    private EventRepo(){ }

    //Singleton pattern
    public static EventRepo getInstance(){
        if(eventRepoInstance == null) {
            eventRepoInstance = new EventRepo();
            selectedDate.setValue(Calendar.getInstance(Locale.US).getTime());
        }
        return eventRepoInstance;
    }

    /*//todo remove this later
    public static void loadEvents(Callback callback){
        RetrofitEvent.getEventApi().getEvents().enqueue(callback);
    }*/

    //use this to get events
    public void loadEvents(String year,String month){
        RetrofitEvent.getEventApi().getEvents(year,month,"a").enqueue(new Callback<List<Event>>(){

            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if ((!response.isSuccessful())||response.body()==null){
                    eventsLiveData.setValue(Collections.emptyList());
                    return;
                }
                eventsLiveData.setValue(response.body());
                for (Event event: response.body()) {
                    Log.d("MyFitness229", "onResponse: "+event.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                eventsLiveData.setValue(Collections.emptyList());
            }
        });
    }

    public void loadDayEvents(Date date){
        List<Event> dayEventList  = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String dateString = dateFormat.format(date);
        for ( Event event : getEventsList()){
            if (event.getEventDate().equals(dateString)){
                dayEventList.add(event);
            }
        }
        dayEventsLiveData.setValue(dayEventList);
        selectedDate.setValue(date);
    }

    public String currentYear(){
        Calendar cal = Calendar.getInstance();
        return Integer.toString(cal.get(Calendar.YEAR));
    }

    public String currentMonth(){
        Calendar cal = Calendar.getInstance();
        return Integer.toString(cal.get(Calendar.MONTH)+1);
    }

    public LiveData<Date> getSelectedDate(){
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


}
