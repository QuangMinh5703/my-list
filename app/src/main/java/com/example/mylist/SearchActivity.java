package com.example.mylist;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylist.Adapter.SearchMovieAdapter;
import com.example.mylist.Api.ApiClient;
import com.example.mylist.Api.ApiService;
import com.example.mylist.Model.Movie;
import com.example.mylist.Model.ResponseSearch;
import com.example.mylist.Model.ResponseSearchMovie;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private ImageButton btnBack, btnSearch;
    private EditText etSearch;
    private TextView tvResultsHeader;
    private RecyclerView rvSearchResults;
    private ApiService apiService;
    private SearchMovieAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_sreach);

        initView();
        setupApiService();
        listener();
    }

    private void initView() {
        btnBack = findViewById(R.id.btnBack);
        btnSearch = findViewById(R.id.btnSearch);
        etSearch = findViewById(R.id.etSearch);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        tvResultsHeader = findViewById(R.id.tvResultsHeader);
    }

    private void setupApiService() {
        apiService = ApiClient.getApiService();
    }

    private void listener() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = etSearch.getText().toString();
                if (!query.isEmpty()) {
                    searchMovies(query);
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void searchMovies(String keyword) {
        apiService.searchMovies(keyword).enqueue(new Callback<ResponseSearch>() {

            @Override
            public void onResponse(Call<ResponseSearch> call, Response<ResponseSearch> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseSearchMovie responseMovies = response.body().getData();
                    tvResultsHeader.setText(responseMovies.getTitle());
                    tvResultsHeader.setVisibility(View.VISIBLE);
                    setupRecyclerView(responseMovies.getData());
                }
            }

            @Override
            public void onFailure(Call<ResponseSearch> call, Throwable throwable) {
                Log.e("API_ERROR", "Failed to get new movies", throwable);
            }
        });
    }

    private void setupRecyclerView(List<Movie> movies) {
        searchAdapter = new SearchMovieAdapter(movies, this);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResults.setAdapter(searchAdapter);
    }
}
