package com.example.myfitness;

import android.graphics.Bitmap;

public class videolist_item {

    private String thumbnail = null;
    private String thumbnail_time = null;
    private String video_title = null;
    private String video_url = null;
    private String calorie = null;
    private String release_date = null;
    private String video_time = null;
    private String ir_name = null;
    private String video_explanation = null;
    private Bitmap context_img = null;
    private Bitmap download_img = null;
    private String video_id = null;

    /**
     * 空のコンストラクタ
     */
    public videolist_item() {
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getThumbnail_time() {
        return thumbnail_time;
    }

    public void setThumbnail_time(String thumbnail_time) {
        this.thumbnail_time = thumbnail_time;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getVideo_explanation() {
        return video_explanation;
    }

    public void setVideo_explanation(String video_explanation) {
        this.video_explanation = video_explanation;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getVideo_time() {
        return video_time;
    }

    public void setVideo_time(String video_time) {
        this.video_time = video_time;
    }

    public String getIr_name() {
        return ir_name;
    }

    public void setIr_name(String ir_name) {
        this.ir_name = ir_name;
    }

    public Bitmap getContext_img() {
        return context_img;
    }

    public void setContext_img(Bitmap context_img) {
        this.context_img = context_img;
    }

    public Bitmap getDownload_img() {
        return download_img;
    }

    public void setDownload_img(Bitmap download_img) {
        this.download_img = download_img;
    }


}
