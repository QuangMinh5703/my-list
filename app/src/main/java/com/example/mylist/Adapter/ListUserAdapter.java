package com.example.mylist.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mylist.Model.User;
import com.example.mylist.R;
import java.util.List;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.ListUserViewHolder> {
    private List<User> userList;
    private Context context;
    private static OnDeleteUserClickListener listener;

    public interface OnDeleteUserClickListener {
        void onClick(User user, View view);
    }

    public void setOnDeleteUserClickListener(OnDeleteUserClickListener listener) {
        this.listener = listener;
    }

    public ListUserAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public ListUserAdapter.ListUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ListUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListUserAdapter.ListUserViewHolder holder, int position) {
        //Gan du lieu cho ViewHolder
        User user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public static class ListUserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName;

        public ListUserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
        }

        public void bind(User user) {
            tvUserName.setText(user.getUsername());
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onClick(user, v);
            });
        }
    }
}
