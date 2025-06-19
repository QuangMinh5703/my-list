package com.example.mylist.Api;

import com.example.mylist.Model.Movie;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    String BASE_URL = "https://phimapi.com/";
    ApiService apiService = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService.class);


    @GET("danh-sach/phim-moi-cap-nhat-v3")
    Call<List<Movie>> getNewMovies(@Query("page") int page);
}
