package com.example.mylist.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Episode {
    @SerializedName("server_data")
    private List<ServerData> data;

    public List<ServerData> getData() {
        return data;
    }

}
