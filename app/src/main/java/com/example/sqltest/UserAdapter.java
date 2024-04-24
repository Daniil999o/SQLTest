package com.example.sqltest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<User> users;
    private static MainActivity mainActivity;

    public UserAdapter(Context context, List<User> users, MainActivity activity) {
        this.users = users;
        this.inflater = LayoutInflater.from(context);
        mainActivity = activity;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.user_adapter, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = users.get(position);

        holder.idView.setText("id" + user.id);
        holder.ageView.setText(user.age);
        holder.nameView.setText(user.name);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView idView;
        final TextView nameView;
        final TextView ageView;

        ViewHolder(View view) {
            super(view);

            idView = view.findViewById(R.id.idInfo);
            nameView = view.findViewById(R.id.nameInfo);
            ageView = view.findViewById(R.id.ageInfo);

            view.findViewById(R.id.deleteBtn).setOnClickListener((v) -> {
                String id = idView.getText().toString().split("id")[1];
                mainActivity.removeUserWithId(id);
            });

            view.findViewById(R.id.editBtn).setOnClickListener((v) -> {
                String id = idView.getText().toString().split("id")[1];
                mainActivity.editUserWithId(id);
            });
        }
    }
}
