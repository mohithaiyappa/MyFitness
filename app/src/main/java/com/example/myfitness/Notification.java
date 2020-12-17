package com.example.myfitness;

import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("date")
    private String notificationDate;
    @SerializedName("text")
    private String notificationText;

    //getters
    public String getNotificationDate() {
        return notificationDate;
    }

    //setters
    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }
}
