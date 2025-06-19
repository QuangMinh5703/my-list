package com.example.mylist.Model;

import com.google.gson.annotations.SerializedName;

public class ResponseSection {
    @SerializedName("data")
    private ResponseMovie data;

    public ResponseMovie getData() {
        return data;
    }
}
