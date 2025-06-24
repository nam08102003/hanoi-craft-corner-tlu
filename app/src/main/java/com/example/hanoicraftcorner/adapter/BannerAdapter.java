package com.example.hanoicraftcorner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hanoicraftcorner.R;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<String> bannerUrls;
    private Context context;

    public BannerAdapter(List<String> urls) {
        this.bannerUrls = urls;
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;

        public BannerViewHolder(View view) {
            super(view);
            bannerImage = view.findViewById(R.id.imageBanner);
        }
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BannerViewHolder holder, int position) {
        Glide.with(holder.bannerImage.getContext()).load(bannerUrls.get(position)).into(holder.bannerImage);
    }

    @Override
    public int getItemCount() {
        return bannerUrls.size();
    }
}

