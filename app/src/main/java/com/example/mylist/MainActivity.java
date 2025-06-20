package com.example.mylist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button btnGenre;
    private ImageButton btnSearch;
    private TextView tvHeaderTitle;
    LinearLayout  btnHome, btnProfile;
    private RecyclerView recyclerViewSections;
    private ApiService apiService;
    private SectionAdapter sectionAdapter;
    private NestedScrollView scrollView;

    List<Section> sectionList = new ArrayList<>();


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_home_main);

        Intent intent = getIntent();;
        String uid = intent.getStringExtra("uid");
        String userName = intent.getStringExtra("username");
        
        initView();
        setupApiService();
        listener();
        loadData();
        tvHeaderTitle.setText("Dành cho " + userName);
    }

    private void listener() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scrollView != null) {
                    scrollView.smoothScrollTo(0, 0);
                }
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadData() {
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
                        setupRecyclerView(sectionList);
                        return;
                    }

                    loadMoviesForGenreSections(genreSections, 0);

                } else {
                    Log.e("API_ERROR", "Failed to get genre sections: " + response.code());
                    setupRecyclerView(sectionList);
                }
            }

            @Override
            public void onFailure(Call<List<Section>> call, Throwable t) {
                Log.e("API_ERROR", "Failed to get genre sections", t);
                setupRecyclerView(sectionList);
            }
        });
    }

    private void loadMoviesForGenreSections(List<Section> genreSections, int currentIndex) {
        if (currentIndex >= genreSections.size()) {
            setupRecyclerView(sectionList);
            return;
        }

        Section currentSection = genreSections.get(currentIndex);

        apiService.getMoviesByGenre(currentSection.getSlug()).enqueue(new Callback<ResponseSection>() {
            @Override
            public void onResponse(Call<ResponseSection> call, Response<ResponseSection> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getData().getData();
                    if (movies != null && !movies.isEmpty()) {
                        currentSection.setMovies(movies);
                        sectionList.add(currentSection);
                    } else {
                        Log.w("API_WARNING", "No movies found for genre: " + currentSection.getSlug());
                    }
                } else {
                    Log.e("API_ERROR", "Failed to get movies for genre: " + currentSection.getSlug() +
                            ", Response code: " + response.code());
                }

                loadMoviesForGenreSections(genreSections, currentIndex + 1);
            }

            @Override
            public void onFailure(Call<ResponseSection> call, Throwable t) {
                Log.e("API_ERROR", "Failed to get movies for genre: " + currentSection.getSlug(), t);

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
        scrollView = findViewById(R.id.scroll_content);
        View header = findViewById(R.id.home_header);
        recyclerViewSections = findViewById(R.id.recyclerViewSections);
        btnSearch = header.findViewById(R.id.btnSearch);
        btnGenre = header.findViewById(R.id.btnGenre);
        tvHeaderTitle = header.findViewById(R.id.tvHeaderTitle);
        View menu = findViewById(R.id.menu_home);
        btnHome = menu.findViewById(R.id.btn_home);
        btnProfile = menu.findViewById(R.id.btn_profile);
    }
}