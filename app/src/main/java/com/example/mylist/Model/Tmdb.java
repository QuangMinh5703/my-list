package com.example.mylist.Model;

import com.google.gson.annotations.SerializedName;

public class Tmdb {
    @SerializedName("id")
    private String id;
    @SerializedName("type")
    private String type;
    @SerializedName("season")
    private String season;
    @SerializedName("vote_average")
    private Float ranking;
    @SerializedName("vote_count")
    private Integer vote_count;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public Float getRanking() {
        return ranking;
    }

    public void setRanking(Float ranking) {
        this.ranking = ranking;
    }

    public Integer getVote_count() {
        return vote_count;
    }

    public void setVote_count(Integer vote_count) {
        this.vote_count = vote_count;
    }
}
