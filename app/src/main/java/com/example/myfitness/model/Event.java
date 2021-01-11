package com.example.myfitness.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Event {

    @SerializedName("e_id")
    private int e_id;
    @SerializedName("mode")
    private String mode;
    @SerializedName("vid_id")
    private String videoId;
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("end_time")
    private String endTime;
    @SerializedName("date")
    private String eventDate;
    @SerializedName("start_date")
    private String eventStartDate;
    @SerializedName("end_date")
    private String eventEndDate;
    @SerializedName("days_only")
    private String daysOnly;
    @SerializedName("video_array")
    private List<EventVideoDetails> videoArray;

    //Getters

    public int getE_id() {
        return e_id;
    }

    public void setE_id(int e_id) {
        this.e_id = e_id;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    //Setters

    public String getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(String eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public String getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(String eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public String getDaysOnly() {
        return daysOnly;
    }

    public void setDaysOnly(String daysOnly) {
        this.daysOnly = daysOnly;
    }

    public List<EventVideoDetails> getVideoArray() {
        return videoArray;
    }

    public void setVideoArray(List<EventVideoDetails> videoArray) {
        this.videoArray = videoArray;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        if (obj instanceof Event) {
            Event e = (Event) obj;
            return e.e_id == this.e_id;
        }
        return super.equals(obj);
    }
}
