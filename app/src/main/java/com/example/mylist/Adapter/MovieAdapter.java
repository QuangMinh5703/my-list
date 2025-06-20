package com.example.mylist.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.mylist.Model.Movie;
import com.example.mylist.R;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private Context context;

    private OnMovieClickListener listener;
    private static final String IMAGE_BASE_URL = "https://phimimg.com/";

    public interface OnMovieClickListener {
        void onClick(Movie movie, View view);
    }

    public MovieAdapter(List<Movie> movies, Context context) {
        this.movies = movies;
        this.context = context;
    }

    public void setOnMovieClickListener(OnMovieClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie, context);
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    public void updateMovies(List<Movie> newMovies) {
        this.movies = newMovies;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewPoster;
        private TextView tvTitle, tvSubtitle, tvRating;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewPoster = itemView.findViewById(R.id.imageViewPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
            tvRating = itemView.findViewById(R.id.tvRating);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Movie movie, Context context) {
            tvTitle.setText(movie.getName());
            tvRating.setText(movie.getTmdb().getRanking().toString());
            tvSubtitle.setText(movie.getYear());

            loadMoviePoster(context, movie.getImageUrl());

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onClick(movie, v);
            });
        }

        private void loadMoviePoster(Context context, String imageUrl) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .listener(new RequestListener<Drawable>() {
                        private boolean triedFallback = false;

                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e,
                                                    Object model, Target<Drawable> target, boolean isFirstResource) {
                            if (!triedFallback) {
                                triedFallback = true;

                                // Tạo URL đầy đủ bằng cách ghép với IMAGE_BASE_URL
                                String fullUrl = IMAGE_BASE_URL + imageUrl;

                                // Sử dụng Handler để post lên main thread
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    Glide.with(context)
                                            .load(fullUrl)
                                            .placeholder(R.drawable.placeholder_image)
                                            .error(R.drawable.error_image)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .centerCrop()
                                            .transition(DrawableTransitionOptions.withCrossFade(300))
                                            .into(imageViewPoster);
                                });

                                return true;
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target, DataSource dataSource,
                                                       boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageViewPoster);
        }
    }
}
