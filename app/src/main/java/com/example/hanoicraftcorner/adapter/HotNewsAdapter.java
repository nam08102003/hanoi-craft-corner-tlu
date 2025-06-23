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
import com.example.hanoicraftcorner.model.HotNews;

import java.util.List;

public class HotNewsAdapter extends RecyclerView.Adapter<HotNewsAdapter.HotNewsViewHolder> {

    private Context context;
    private List<HotNews> newsList;

    public HotNewsAdapter(Context context, List<HotNews> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public HotNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hot_news, parent, false);
        return new HotNewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotNewsViewHolder holder, int position) {
        HotNews news = newsList.get(position);
        holder.titleTextView.setText(news.getTitle());

        Glide.with(context)
                .load(news.getImageUrl())
                .placeholder(R.drawable.ic_placeholder_default)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Mở tin: " + news.getTitle(), Toast.LENGTH_SHORT).show();
            // TODO: Mở link chi tiết tin tức
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class HotNewsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;

        public HotNewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_hot_news);
            titleTextView = itemView.findViewById(R.id.text_hot_news_title);
        }
    }
} 