package com.example.mylist.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponsePlayMovie {

    @SerializedName("movie")
    private Movie data;

    @SerializedName("episodes")
    private List<Episode> episodes;

    public Movie getData() {
        return data;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }
}
