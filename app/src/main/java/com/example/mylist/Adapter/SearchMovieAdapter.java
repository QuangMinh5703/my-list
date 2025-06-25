package com.example.mylist.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.mylist.Model.Movie;
import com.example.mylist.PlayMovieActivity;
import com.example.mylist.R;

import java.util.List;

public class SearchMovieAdapter extends RecyclerView.Adapter<SearchMovieAdapter.SearchMovieViewHolder> {
    private List<Movie> movies;
    private Context context;


    private static final String IMAGE_BASE_URL = "https://phimimg.com/";

    public SearchMovieAdapter(List<Movie> movies, Context context) {
        this.movies = movies;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_search, parent, false);
        return new SearchMovieAdapter.SearchMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchMovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie, context);
        holder.ivPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Play " + movie.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    public class SearchMovieViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivThumbnail, ivPlayButton;
        private TextView tvTitle;


        public SearchMovieViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivPlayButton = itemView.findViewById(R.id.ivPlayButton);
        }

        public void bind(Movie movie, Context context) {
            tvTitle.setText(movie.getName());

            String url = IMAGE_BASE_URL + movie.getImageUrl();
            loadMoviePoster(context, url);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PlayMovieActivity.class);
                    intent.putExtra("Slug", movie.getSlug());
                    context.startActivity(intent);
                }
            });
        }

        private void loadMoviePoster(Context context, String imageUrl) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(ivThumbnail);
        }
    }
}
