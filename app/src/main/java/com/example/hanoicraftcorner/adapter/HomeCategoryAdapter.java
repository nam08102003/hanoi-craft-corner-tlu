package com.example.hanoicraftcorner.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.model.Category;

import java.util.List;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.CategoryViewHolder>{
    private List<Category> categoryList;
    private Context context;

    public HomeCategoryAdapter(Context ctx, List<Category> categories) {
        this.context = ctx;
        this.categoryList = categories;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        public CategoryViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.imageCategory);
            name = view.findViewById(R.id.textCategory);
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category c = categoryList.get(position);
        holder.name.setText(c.getName());
        Glide.with(context).load(c.getImageUrl()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

}
