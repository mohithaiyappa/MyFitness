package com.example.myfitness.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("user_id")
    private String userId;
    @SerializedName("password")
    private String password;
    @SerializedName("EXP_date")
    private String expDate;

    //getters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    //setters

    public void setPassword(String password) {
        this.password = password;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof User)) return false;
        User user = (User) obj;
        return userId.equals(user.getUserId());
    }
}
