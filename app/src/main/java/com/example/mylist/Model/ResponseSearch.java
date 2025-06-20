package com.example.mylist.Model;

import com.google.gson.annotations.SerializedName;


public class ResponseSearch {
    @SerializedName("data")
    private ResponseSearchMovie data;

    public ResponseSearchMovie getData() {
        return data;
    }
}
