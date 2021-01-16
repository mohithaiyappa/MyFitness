package com.example.myfitness.network;

import com.example.myfitness.model.Event;
import com.example.myfitness.model.InterRimCategory;
import com.example.myfitness.model.Notification;
import com.example.myfitness.model.User;
import com.example.myfitness.model.VideoData;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EventApi {

    @GET("vidjson.php")
    Call<List<Event>> getEvents(@Query("year") int year,
                                @Query("month") int month,
                                @Query("user_id") String userId);

    @GET("devent.php")
    Call<List<Event>> getDayEvents(@Query("year") int year,
                                   @Query("month") int month,
                                   @Query("user_id") String userId,
                                   @Query("day") int dayOfMonth);

    @DELETE("del.php")
    Call<Void> deleteEvent(@Query("user_id") String userId,
                           @Query("e_id") int e_id);

    @GET("notification.php")
    Call<List<Notification>> getNotifications();

    @GET("loginExpDate.php")
    Call<List<User>> getUserDetails();

    @GET("viddetails.php")
    Call<List<Event>> getEventWithCompleteDetails(@Query("user_id") String userId, @Query("e_id") int e_id);

    @GET("category.php")
    Call<List<InterRimCategory>> getInterRimCategories();

    @GET("categorynew.php")
    Call<List<VideoData>> getVideoData();

    @GET("save_video.php")
    Call<List<VideoData>> getDownloadedVideoIds(@Query("user_id") String userId);

    @POST("file_delete2.php")
    Call<ResponseBody> deleteFile(@Query("user_id") String userId, @Query("video_id") String videoId);
}
