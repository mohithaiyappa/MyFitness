package com.example.myfitness;

import com.google.gson.annotations.SerializedName;

public class Event {

    @SerializedName("video_title")
    private String videoTitle;
    @SerializedName("video_time")
    private String videoTime;
    @SerializedName("ir_name")
    private String irName;
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("date")
    private String eventDate;

    //Getters
    public String getVideoTitle() {
        return videoTitle;
    }

    public String getVideoTime() {
        return videoTime;
    }

    public String getIrName() {
        return irName;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEventDate() {
        return eventDate;
    }

    //Setters
    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public void setVideoTime(String videoTime) {
        this.videoTime = videoTime;
    }

    public void setIrName(String irName) {
        this.irName = irName;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }
}
