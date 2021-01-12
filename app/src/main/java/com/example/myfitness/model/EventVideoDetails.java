package com.example.myfitness.model;

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
    @SerializedName("thumbnail_url")
    private String thumbnailUrl = "";
    @SerializedName("calorie")
    private String calorie = "";
    @SerializedName("release_date")
    private String releaseDate = "";

    //getters

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getIrName() {
        return irName;
    }

    public void setIrName(String irName) {
        this.irName = irName;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getCalorie() {
        return calorie;
    }

    //setters

    public String getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(String videoTime) {
        this.videoTime = videoTime;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
