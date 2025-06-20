package com.example.mylist.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylist.MainActivity;
import com.example.mylist.Model.Section;
import com.example.mylist.R;

import java.util.List;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {
    private List<Section> sections;
    private Context context;

    public SectionAdapter(List<Section> sections, Context context) {
        this.sections = sections;
        this.context = context;
    }


    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        Section section = sections.get(position);
        holder.bind(section);
    }

    @Override
    public int getItemCount() {
        return sections != null ? sections.size() : 0;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    class SectionViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSectionTitle;
        private RecyclerView recyclerViewItems;
        private MovieAdapter movieAdapter;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSectionTitle = itemView.findViewById(R.id.tvSectionTitle);
            recyclerViewItems = itemView.findViewById(R.id.recyclerViewItems);

        }

        public void bind(Section section) {
            tvSectionTitle.setText(section.getName());

            if (movieAdapter == null) {
                movieAdapter = new MovieAdapter(section.getMovies(), context);
                recyclerViewItems.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                recyclerViewItems.setAdapter(movieAdapter);
                recyclerViewItems.setHasFixedSize(true);
                recyclerViewItems.setItemViewCacheSize(20);
                movieAdapter.setOnMovieClickListener((movie, view) -> {
                    Toast.makeText(context, "Play: " + movie.getName(), Toast.LENGTH_SHORT).show();
                });
            } else {
                movieAdapter.updateMovies(section.getMovies());
            }
        }
    }
}
