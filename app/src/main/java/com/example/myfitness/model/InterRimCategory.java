package com.example.myfitness.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class InterRimCategory {

    @SerializedName("main_category")
    private String mainCategory = "";
    @SerializedName("sub_category")
    private String subCategory = "";

    //getters

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    //setters

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    @NonNull
    @Override
    public String toString() {
        return "\n" + "main Category: " + mainCategory + "\n" + "subCategory: " + subCategory;
    }
}
