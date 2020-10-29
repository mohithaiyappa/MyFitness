package com.example.myfitness;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EventApi {

    @GET("cal.php")
    Call<List<Event>> getEvents(@Query("year") int year,
                                @Query("month") int month,
                                @Query("user_id") String userId);
}