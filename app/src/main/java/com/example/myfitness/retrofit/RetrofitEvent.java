package com.example.myfitness.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitEvent {
    private static EventApi eventApi = null;

    public static EventApi getEventApi() {
        if (eventApi == null) {
            eventApi = new Retrofit.Builder()
                    .baseUrl("http://www.cmanage.net/homefitness/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(EventApi.class);
        }
        return eventApi;
    }
}
