package com.example.hanoicraftcorner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.model.Artisan;

import java.util.List;

public class ArtisanAdapter extends RecyclerView.Adapter<ArtisanAdapter.ArtisanViewHolder> {

    private Context context;
    private List<Artisan> artisanList;

    public ArtisanAdapter(Context context, List<Artisan> artisanList) {
        this.context = context;
        this.artisanList = artisanList;
    }

    @NonNull
    @Override
    public ArtisanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artisan, parent, false);
        return new ArtisanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtisanViewHolder holder, int position) {
        Artisan artisan = artisanList.get(position);
        holder.nameTextView.setText(artisan.getName());
        // Show brand name if available, else show name
        String brandOrName = artisan.getBrand_name() != null && !artisan.getBrand_name().isEmpty() ? artisan.getBrand_name() : artisan.getName();
        holder.nameTextView.setText(brandOrName);
        // Show introduce
        holder.introTextView.setText(artisan.getIntroduce());
        // Load avatar image if available
        if (artisan.getAvatar() != null && !artisan.getAvatar().isEmpty()) {
            Glide.with(context)
                    .load(artisan.getAvatar())
                    .placeholder(R.drawable.ic_placeholder_default)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_placeholder_default);
        }
        holder.storeButton.setOnClickListener(v -> {
            Toast.makeText(context, "Mở cửa hàng của " + brandOrName, Toast.LENGTH_SHORT).show();
            // TODO: Chuyển sang màn hình cửa hàng của nghệ nhân
        });
    }

    @Override
    public int getItemCount() {
        return artisanList.size();
    }

    public static class ArtisanViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView introTextView;
        Button storeButton;

        public ArtisanViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_artisan);
            nameTextView = itemView.findViewById(R.id.text_artisan_name);
            introTextView = itemView.findViewById(R.id.text_artisan_intro);
            storeButton = itemView.findViewById(R.id.btn_go_to_store);
        }
    }
}
