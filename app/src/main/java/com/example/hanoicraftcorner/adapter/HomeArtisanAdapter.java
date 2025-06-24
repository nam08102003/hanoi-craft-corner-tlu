package com.example.hanoicraftcorner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.model.User;

import java.util.List;

public class HomeArtisanAdapter extends RecyclerView.Adapter<HomeArtisanAdapter.ArtisanViewHolder>{
    private List<User> artisanList;
    private Context context;

    public HomeArtisanAdapter(Context ctx, List<User> users) {
        this.context = ctx;
        this.artisanList = users;
    }

    public static class ArtisanViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name, introduce, address;

        public ArtisanViewHolder(View view) {
            super(view);
            avatar = view.findViewById(R.id.imageAvatar);
            name = view.findViewById(R.id.textName);
            introduce = view.findViewById(R.id.tvIntroduce);
            address = view.findViewById(R.id.tvLocation);
        }
    }

    @NonNull
    @Override
    public ArtisanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_item_artisan, parent, false);
        return new ArtisanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArtisanViewHolder holder, int position) {
        User u = artisanList.get(position);
        holder.name.setText(u.getFullname());
        holder.address.setText(u.getAddress());
        holder.introduce.setText(u.getIntroduce());
        Glide.with(context).load(u.getAvatar()).into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return artisanList.size();
    }
}
