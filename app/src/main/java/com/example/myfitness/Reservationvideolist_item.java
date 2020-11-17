package com.example.myfitness;

import android.graphics.Bitmap;

public class Reservationvideolist_item {

    private String thumbnail = null;
    private String thumbnail_time = null;
    private String video_title = null;
    private String local_path = null;
    private String calorie = null;
    private String save_date = null;
    private String video_time = null;
    private String ir_name = null;
    private String video_explanation = null;
    private Bitmap context_img = null;
    private String video_id = null;
    private String medley_num = null;

    /**
     * 空のコンストラクタ
     */
    public Reservationvideolist_item() {};

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setLocal_path(String local_path) {
        this.local_path = local_path;
    }

    public void setThumbnail_time(String thumbnail_time) {
        this.thumbnail_time = thumbnail_time;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public void setVideo_explanation(String video_explanation) {
        this.video_explanation = video_explanation;
    }

    public void setMedley_num(String medley_num) {
        this.medley_num = medley_num;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public void setSave_date(String save_date) {
        this.save_date = save_date;
    }

    public void setVideo_time(String video_time) {
        this.video_time = video_time;
    }

    public void setIr_name(String ir_name) {
        this.ir_name = ir_name;
    }

    public void setContext_img(Bitmap context_img) {
        this.context_img = context_img;
    }

    public String getVideo_id() {
        return video_id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getLocal_path() {
        return local_path;
    }

    public String getThumbnail_time() {
        return thumbnail_time;
    }

    public String getVideo_title() {
        return video_title;
    }

    public String getVideo_explanation() {
        return video_explanation;
    }

    public String getCalorie() {
        return calorie;
    }

    public String getSave_date() {
        return save_date;
    }

    public String getVideo_time() {
        return video_time;
    }

    public String getIr_name() {
        return ir_name;
    }

    public Bitmap getContext_img() {
        return context_img;
    }

    public String getMedley_num() {
        return medley_num;
    }

}
