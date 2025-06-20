package com.example.mylist.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseSearchMovie {
    @SerializedName("titlePage")
    private String title;

    @SerializedName("items")
    private List<Movie> data;

    public List<Movie> getData() {
        return data;
    }

    public String getTitle() {
        return title;
    }
}
