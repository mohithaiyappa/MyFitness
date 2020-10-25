package com.example.myfitness;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EventApi {

    @GET("cal.php")
    Call<List<Event>> getEvents();
}
