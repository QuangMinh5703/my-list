package com.example.mylist.Model;

import com.google.gson.annotations.SerializedName;

public class ServerData {
    @SerializedName("name")
    private String name;
    @SerializedName("link_embed")
    private String url;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
