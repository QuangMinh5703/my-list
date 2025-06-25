package com.example.mylist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylist.Adapter.EpisodesAdapter;
import com.example.mylist.Api.ApiClient;
import com.example.mylist.Api.ApiService;
import com.example.mylist.Model.Episode;
import com.example.mylist.Model.Movie;
import com.example.mylist.Model.ResponsePlayMovie;
import com.example.mylist.Model.ServerData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayMovieActivity extends AppCompatActivity {
    private TextView tvNameMovie, tvMovieDescription;
    private Button btnNextEpisode, btnFullscreen;
    private PlayerView vdView;
    private ImageView btnBack;
    private ApiService apiService;
    private RecyclerView recyclerView;
    private Handler handler = new Handler();
    private int nPosition = 0;
    private EpisodesAdapter adapter;
    private List<ServerData> allServerData = new ArrayList<>();
    private boolean isFullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_movie);

        Intent intent = getIntent();
        String slug = intent.getStringExtra("Slug");

        initView();
        setupApiService();
        listener();
        loadData(slug);
    }

    private int calculateSpanCount() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float screenWidth = displayMetrics.widthPixels;

        float itemWidth = 60;
        float density = getResources().getDisplayMetrics().density;
        float itemWidthPx = itemWidth * density;

        int spanCount = Math.max(2, Math.min(6, (int) (screenWidth / itemWidthPx)));
        return spanCount;
    }

    private void loadData(String slug) {
        apiService.getMovieBySlug(slug).enqueue(new Callback<ResponsePlayMovie>() {
            @Override
            public void onResponse(Call<ResponsePlayMovie> call, Response<ResponsePlayMovie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Movie movie = response.body().getData();
                    List<Episode> episodes = response.body().getEpisodes();

                    tvNameMovie.setText(movie.getName());
                    tvMovieDescription.setText(movie.getContent());

                    if (episodes != null && !episodes.isEmpty()) {
                        Episode firstEpisode = episodes.get(nPosition);
                        if (firstEpisode.getData() != null && !firstEpisode.getData().isEmpty()) {
                            List<ServerData> serverDataList = firstEpisode.getData();
                            String videoUrl = serverDataList.get(nPosition).getUrl();
                            setupVideoPlayer(videoUrl);
                        }

                        allServerData = new ArrayList<>();
                        for (Episode episode : episodes) {
                            if (episode.getData() != null) {
                                allServerData.addAll(episode.getData());
                            }
                        }
                        adapter = new EpisodesAdapter(allServerData, PlayMovieActivity.this);
                        adapter.setSelectedPosition(nPosition);
                        int spanCount = calculateSpanCount();
                        adapter.setOnItemClickListener(new EpisodesAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position, String url) {
                                adapter.setSelectedPosition(position);
                                updateMovie(position, url);
                            }
                        });
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(PlayMovieActivity.this, spanCount);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(PlayMovieActivity.this, "Không có tập phim nào", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PlayMovieActivity.this, "Không thể tải dữ liệu phim", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponsePlayMovie> call, Throwable throwable) {
                Toast.makeText(PlayMovieActivity.this, "Lỗi tải dữ liệu: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMovie(int position, String url) {
        nPosition = position;
        setupVideoPlayer(url);
    }

    private void setupVideoPlayer(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            Toast.makeText(this, "URL video không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Uri inputUri = Uri.parse(videoUrl);
            String actualVideoUrl = inputUri.getQueryParameter("url");

            if (actualVideoUrl == null || actualVideoUrl.isEmpty()) {
                actualVideoUrl = videoUrl;
            }

            Log.d("VideoPlayer", "Final Video URL to play: " + actualVideoUrl);

            Uri videoUri = Uri.parse(actualVideoUrl);
            ExoPlayer player = new ExoPlayer.Builder(PlayMovieActivity.this).build();

            player.addListener(new Player.Listener() {
                @Override
                public void onPlayerError(PlaybackException error) {
                    Player.Listener.super.onPlayerError(error);
                    Log.e("VideoPlayer", "Playback error: " + error.getMessage());
                    Toast.makeText(PlayMovieActivity.this, "Lỗi phát video: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    Player.Listener.super.onPlaybackStateChanged(playbackState);
                    Log.d("VideoPlayer", "Playback state changed: " + playbackState);
                    switch (playbackState) {
                        case Player.STATE_READY:
                            Log.d("VideoPlayer", "Player ready to play");
                            break;
                        case Player.STATE_BUFFERING:
                            Log.d("VideoPlayer", "Player buffering");
                            break;
                        case Player.STATE_ENDED:
                            Log.d("VideoPlayer", "Playback ended");
                            break;
                        case Player.STATE_IDLE:
                            Log.d("VideoPlayer", "Player is idle");
                            break;
                    }
                }
            });

            vdView.setPlayer(player);

            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(videoUri)
                    .build();

            player.setMediaItem(mediaItem);
            player.prepare();
            player.setPlayWhenReady(true);

        } catch (Exception e) {
            Log.e("VideoPlayer", "Error setting up video player", e);
            Toast.makeText(this, "Lỗi khi thiết lập video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        tvNameMovie = findViewById(R.id.tvNameMovie);
        tvMovieDescription = findViewById(R.id.tvMovieDescription);
        vdView = findViewById(R.id.playerView);
        btnBack = findViewById(R.id.btnBackHome);
        recyclerView = findViewById(R.id.rvEpisodes);
        btnNextEpisode = findViewById(R.id.btnNextEpisode);
        btnFullscreen = findViewById(R.id.btnFullscreen);
    }

    private void setupApiService() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    private void listener() {
        btnBack.setOnClickListener(v -> finish());

        btnNextEpisode.setOnClickListener(v -> {
            if (nPosition < adapter.getItemCount() && nPosition != 0) {
                nPosition++;

                String nextUrl = allServerData.get(nPosition).getUrl();
                adapter.setSelectedPosition(nPosition);
                updateMovie(nPosition, nextUrl);
            } else {
                Toast.makeText(this, "Đây là tập cuối cùng!", Toast.LENGTH_SHORT).show();
            }

        });

        btnFullscreen.setOnClickListener(v -> {
            if (!isFullscreen) {
                Toast.makeText(this, "Chưa xong tính năng này!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}