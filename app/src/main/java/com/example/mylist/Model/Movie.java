package com.example.mylist.Model;

import com.google.gson.annotations.SerializedName;

public class Movie {
    @SerializedName("_id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("poster_url")
    private String imageUrl;
    @SerializedName("tmdb")
    private Tmdb tmdb;
    @SerializedName("year")
    private String year;

    public Movie() {
    }

    public Movie(String id, String name, String imageUrl, Tmdb tmdb, String year) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.tmdb = tmdb;
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Tmdb getTmdb() {
        return tmdb;
    }

    public void setTmdb(Tmdb tmdb) {
        this.tmdb = tmdb;
    }
}
