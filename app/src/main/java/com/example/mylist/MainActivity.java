package com.example.mylist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylist.Adapter.SectionAdapter;
import com.example.mylist.Api.ApiClient;
import com.example.mylist.Api.ApiService;
import com.example.mylist.Model.Movie;
import com.example.mylist.Model.ResponseMovie;
import com.example.mylist.Model.ResponseSection;
import com.example.mylist.Model.Section;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button btnGenre;
    private ImageButton btnSearch;
    private RecyclerView recyclerViewSections;
    private ApiService apiService;
    private SectionAdapter sectionAdapter;

    List<Section> sectionList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_home_main);
        
        initView();
        setupApiService();
        listener();
        loadData();
    }

    private void listener() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {

        // 1. Gọi API lấy phim mới cập nhật
        apiService.getNewMovies(1).enqueue(new Callback<ResponseMovie>() {
            @Override
            public void onResponse(Call<ResponseMovie> call, Response<ResponseMovie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getData();
                    Section newMoviesSection = new Section();
                    newMoviesSection.setId("new_movies");
                    newMoviesSection.setName("Phim mới cập nhật");
                    newMoviesSection.setSlug("new-movies");
                    newMoviesSection.setMovies(movies);
                    sectionList.add(newMoviesSection);
                    sectionList.clear();
                    sectionList.add(newMoviesSection);
                    loadGenreSections();

                }
            }

            @Override
            public void onFailure(Call<ResponseMovie> call, Throwable t) {
                Log.e("API_ERROR", "Failed to get new movies", t);
                sectionList.clear();
                loadGenreSections();
            }
        });
    }

    private void loadGenreSections() {
        apiService.getSections().enqueue(new Callback<List<Section>>() {
            @Override
            public void onResponse(Call<List<Section>> call, Response<List<Section>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Section> genreSections = response.body();

                    if (genreSections.isEmpty()) {
                        // Chỉ có section phim mới
                        setupRecyclerView(sectionList);
                        return;
                    }

                    loadMoviesForGenreSections(genreSections, 0);

                } else {
                    Log.e("API_ERROR", "Failed to get genre sections: " + response.code());
                    // Setup với những gì đã có (có thể chỉ có section phim mới)
                    setupRecyclerView(sectionList);
                }
            }

            @Override
            public void onFailure(Call<List<Section>> call, Throwable t) {
                Log.e("API_ERROR", "Failed to get genre sections", t);
                // Setup với những gì đã có
                setupRecyclerView(sectionList);
            }
        });
    }

    private void loadMoviesForGenreSections(List<Section> genreSections, int currentIndex) {
        // Kiểm tra đã xử lý hết danh sách chưa
        if (currentIndex >= genreSections.size()) {
            // Hoàn thành - Setup RecyclerView với tất cả sections
            setupRecyclerView(sectionList);
            return;
        }

        Section currentSection = genreSections.get(currentIndex);

        // Lấy phim cho thể loại hiện tại
        apiService.getMoviesByGenre(currentSection.getSlug()).enqueue(new Callback<ResponseSection>() {
            @Override
            public void onResponse(Call<ResponseSection> call, Response<ResponseSection> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getData().getData();
                    if (movies != null && !movies.isEmpty()) {
                        // Gán danh sách phim vào section
                        currentSection.setMovies(movies);
                        // Thêm vào sectionList
                        sectionList.add(currentSection);
                    } else {
                        Log.w("API_WARNING", "No movies found for genre: " + currentSection.getSlug());
                    }
                } else {
                    Log.e("API_ERROR", "Failed to get movies for genre: " + currentSection.getSlug() +
                            ", Response code: " + response.code());
                }

                // Tiếp tục với thể loại tiếp theo
                loadMoviesForGenreSections(genreSections, currentIndex + 1);
            }

            @Override
            public void onFailure(Call<ResponseSection> call, Throwable t) {
                Log.e("API_ERROR", "Failed to get movies for genre: " + currentSection.getSlug(), t);

                // Tiếp tục với thể loại tiếp theo dù có lỗi
                loadMoviesForGenreSections(genreSections, currentIndex + 1);
            }
        });
    }


    private void setupApiService() {
        apiService = ApiClient.getApiService();
    }

    private void setupRecyclerView(List<Section> sectionList) {
        sectionAdapter = new SectionAdapter(sectionList, this);
        recyclerViewSections.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSections.setAdapter(sectionAdapter);
    }

    @SuppressLint("WrongViewCast")
    private void initView() {
        View header = findViewById(R.id.home_header);
        recyclerViewSections = findViewById(R.id.recyclerViewSections);
        btnSearch = header.findViewById(R.id.btnSearch);
        btnGenre = header.findViewById(R.id.btnGenre);
    }
}