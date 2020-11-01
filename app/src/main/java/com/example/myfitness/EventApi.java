package com.example.myfitness;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EventApi {

    @GET("cal.php")
    Call<List<Event>> getEvents(@Query("year") int year,
                                @Query("month") int month,
                                @Query("user_id") String userId);

    @GET("devent.php")
    Call<List<Event>> getDayEvents(@Query("year") int year,
                                @Query("month") int month,
                                @Query("user_id") String userId,
                                @Query("day") int dayOfMonth);

    @DELETE("del.php")
    Call<Object> deleteEvent(@Query("user_id") String userId,
                     @Query("e_id") int e_id);
}
