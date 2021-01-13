package com.example.myfitness.model;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private String categoryName = "";
    private List<Subcategory> subcategories = new ArrayList<>();

    //getters

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    //setters

    public List<Subcategory> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<Subcategory> subcategories) {
        this.subcategories = subcategories;
    }

    //utils

    public void addToSubcategoryList(Subcategory subcategory) {
        subcategories.add(subcategory);
    }

}
