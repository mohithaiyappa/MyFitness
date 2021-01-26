package com.example.myfitness.model;

import com.google.gson.annotations.SerializedName;

public class VideoData {

    @SerializedName("category")
    private String categoryString = "";
    @SerializedName("video_id")
    private String videoId = "";
    @SerializedName("video_title")
    private String videoTitle = "";
    @SerializedName("ir_name")
    private String irName = "";
    @SerializedName("calorie")
    private String calorie = "";
    @SerializedName("release_date")
    private String releaseDate = "";
    @SerializedName("video_time")
    private String videoTime = "";
    @SerializedName("thumbnail_url")
    private String thumbnailUrl = "";
    @SerializedName("video_url")
    private String videoUrl = "";
    @SerializedName("video_explanation")
    private String videoExplanation = "";
    @SerializedName("user_only")
    private String usersOnly = "";

    //getters

    public String getCategoryString() {
        return categoryString;
    }

    public void setCategoryString(String categoryString) {
        this.categoryString = categoryString;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

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

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getUsersOnly() {
        return usersOnly;
    }

    //setters

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(String videoTime) {
        this.videoTime = videoTime;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoExplanation() {
        return videoExplanation;
    }

    public void setVideoExplanation(String videoExplanation) {
        this.videoExplanation = videoExplanation;
    }

    public void setUsersOnly(String usersOnly) {
        this.usersOnly = usersOnly;
    }
}
