package com.example.hanoicraftcorner.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoicraftcorner.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private final List<ImageItem> imageItems;
    private final Context context;
    private final OnImageActionListener listener;

    public interface OnImageActionListener {
        void onImageClick(int position, ImageItem item);

        void onDeleteClick(int position);
    }

    public ImageAdapter(Context context, List<ImageItem> imageItems, OnImageActionListener listener) {
        this.context = context;
        this.imageItems = imageItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.register_artisan_item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageItem item = imageItems.get(position);
        holder.imageView.setImageURI(item.getUri());
        holder.labelView.setText(getLabelForType(item.getType()));
        holder.imageView.setOnClickListener(v -> listener.onImageClick(position, item));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        return imageItems.size();
    }

    private String getLabelForType(String type) {
        if ("certificate".equals(type)) return "Chứng chỉ";
        if ("cccd_front".equals(type)) return "CCCD trước";
        if ("cccd_back".equals(type)) return "CCCD sau";
        return "Ảnh";
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView labelView;
        ImageButton deleteButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item_view);
            labelView = itemView.findViewById(R.id.image_label);
            deleteButton = itemView.findViewById(R.id.delete_image_btn);
        }
    }
}
