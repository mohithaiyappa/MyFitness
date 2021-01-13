package com.example.myfitness.model;

import java.util.ArrayList;
import java.util.List;

public class Subcategory {

    private String subcategoryName = "";
    private List<VideoData> videoDataList = new ArrayList<>();

    //getters

    public String getSubcategoryName() {
        return subcategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        this.subcategoryName = subcategoryName;
    }

    //setters

    public List<VideoData> getVideoDataList() {
        return videoDataList;
    }

    public void setVideoDataList(List<VideoData> videoDataList) {
        this.videoDataList = videoDataList;
    }

    //utils

    public void addToVideoDataList(VideoData videoData) {
        this.videoDataList.add(videoData);
    }
}
