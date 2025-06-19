package com.example.mylist.Api;

import com.example.mylist.Model.Movie;
import com.example.mylist.Model.ResponseMovie;
import com.example.mylist.Model.ResponseSection;
import com.example.mylist.Model.Section;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("danh-sach/phim-moi-cap-nhat-v3")
    Call<ResponseMovie> getNewMovies(@Query("page") int page);

    @GET("the-loai")
    Call<List<Section>> getSections();

    @GET("https://phimapi.com/v1/api/the-loai/{genre}")
    Call<ResponseSection> getMoviesByGenre(@Path("genre") String genre);


}
