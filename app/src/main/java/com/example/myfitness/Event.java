package com.example.myfitness;

import androidx.annotation.Nullable;

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
    @SerializedName("e_id")
    private int e_id;
    @SerializedName("video_id")
    private int videoId;
    @SerializedName("video_url")
    private String videoUrl;
    @SerializedName("local_path")
    private String localPath;

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

    public int getE_id() {
        return e_id;
    }

    public int getVideoId() {
        return videoId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getLocalPath() {
        return localPath;
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

    public void setE_id(int e_id) {
        this.e_id = e_id;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null) return false;
        if(obj instanceof Event){
            Event e =(Event) obj;
            return e.e_id==this.e_id;
        }
        return super.equals(obj);
    }
}
