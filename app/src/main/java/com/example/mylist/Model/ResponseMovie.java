package com.example.mylist.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseMovie {
    @SerializedName("items")
    private List<Movie> data;

    public List<Movie> getData() {
        return data;
    }
}
