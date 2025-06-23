package com.example.hanoicraftcorner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.model.Experience;

import java.util.List;

public class ExperienceAdapter extends RecyclerView.Adapter<ExperienceAdapter.ExperienceViewHolder> {

    private Context context;
    private List<Experience> experienceList;

    public ExperienceAdapter(Context context, List<Experience> experienceList) {
        this.context = context;
        this.experienceList = experienceList;
    }

    @NonNull
    @Override
    public ExperienceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_experience, parent, false);
        return new ExperienceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExperienceViewHolder holder, int position) {
        Experience experience = experienceList.get(position);
        holder.titleTextView.setText(experience.getTitle());

        Glide.with(context)
                .load(experience.getImageUrl())
                .placeholder(R.drawable.ic_placeholder_default)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Mở bài viết: " + experience.getTitle(), Toast.LENGTH_SHORT).show();
            // TODO: Mở link chi tiết bài viết
        });
    }

    @Override
    public int getItemCount() {
        return experienceList.size();
    }

    public static class ExperienceViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;

        public ExperienceViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_experience);
            titleTextView = itemView.findViewById(R.id.text_experience_title);
        }
    }
} 