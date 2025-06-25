package com.example.mylist.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylist.Model.ServerData;
import com.example.mylist.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodesViewHolder>{
     private List<ServerData> datas;
    private Context context;
    private OnItemClickListener listener;
    private int selectedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(int position, String url);
    }

    public EpisodesAdapter(List<ServerData> datas, Context context) {
        this.context = context;
        this.datas = datas;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public EpisodesAdapter.EpisodesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_episode, parent, false);
        return new EpisodesAdapter.EpisodesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesAdapter.EpisodesViewHolder holder, int position) {
        ServerData data = datas.get(position);
        holder.bind(data, position, position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    public void setSelectedPosition(int position) {
        int previousSelected = selectedPosition;
        selectedPosition = position;

        if (previousSelected != -1) {
            notifyItemChanged(previousSelected);
        }
        if (selectedPosition != -1) {
            notifyItemChanged(selectedPosition);
        }
    }

    public class EpisodesViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvEpisodeNumber;

        public EpisodesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEpisodeNumber = itemView.findViewById(R.id.tvEpisodeNumber);
        }

        public void bind(ServerData data, int position, boolean isSelected) {
            if (data != null) {
                String episodeNumber = extractEpisodeNumber(data.getName(), position);
                tvEpisodeNumber.setText(episodeNumber);
                updateBackground(isSelected);
                itemView.setOnClickListener(v -> {
                    int previousSelected = selectedPosition;
                    selectedPosition = position;

                    // Refresh UI
                    if (previousSelected != -1) {
                        notifyItemChanged(previousSelected);
                    }
                    notifyItemChanged(selectedPosition);
                    listener.onItemClick(position, data.getUrl());
                });
            }
        }

        private void updateBackground(boolean isSelected) {
            CardView cardView = (CardView) itemView;
            if (isSelected) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.form_main));
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.edt_text));
            }
        }

        private String extractEpisodeNumber(String name, int position) {
            if (name == null || name.isEmpty()) {
                return String.valueOf(position + 1);
            }

            Pattern[] patterns = {
                    Pattern.compile("(?i)(?:tập|tap|episode|ep)\\s*(\\d+)"),
                    Pattern.compile("(?i)\\b(\\d+)\\s*(?:tập|tap|episode|ep)"),
                    Pattern.compile("\\b(\\d+)\\b"),
            };

            for (Pattern pattern : patterns) {
                Matcher matcher = pattern.matcher(name);
                if (matcher.find()) {
                    String episodeNum = matcher.group(1);
                    if (episodeNum != null) {
                        return episodeNum;
                    }
                }
            }

            return String.valueOf(position + 1);
        }
    }
}
