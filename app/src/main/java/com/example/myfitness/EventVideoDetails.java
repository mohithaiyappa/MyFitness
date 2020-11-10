package com.example.myfitness;

import com.google.gson.annotations.SerializedName;

public class EventVideoDetails {

    @SerializedName("video_title")
    private String videoTitle;
    @SerializedName("ir_name")
    private String irName;
    @SerializedName("video_time")
    private String videoTime;
    @SerializedName("local_path")
    private String localPath;

    //getters

    public String getVideoTitle() {
        return videoTitle;
    }

    public String getIrName() {
        return irName;
    }

    public String getVideoTime() {
        return videoTime;
    }

    public String getLocalPath() {
        return localPath;
    }

    //setters

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public void setIrName(String irName) {
        this.irName = irName;
    }

    public void setVideoTime(String videoTime) {
        this.videoTime = videoTime;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}
