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
import com.example.hanoicraftcorner.model.Village;

import java.util.List;

public class VillageAdapter extends RecyclerView.Adapter<VillageAdapter.VillageViewHolder> {

    private Context context;
    private List<Village> villageList;

    public VillageAdapter(Context context, List<Village> villageList) {
        this.context = context;
        this.villageList = villageList;
    }

    @NonNull
    @Override
    public VillageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_village, parent, false);
        return new VillageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VillageViewHolder holder, int position) {
        Village village = villageList.get(position);
        holder.nameTextView.setText(village.getName());

        Glide.with(context)
                .load(village.getImageUrl())
                .placeholder(R.drawable.ic_placeholder_default)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Mở làng nghề: " + village.getName(), Toast.LENGTH_SHORT).show();
            // TODO: Mở trang chi tiết làng nghề
        });
    }

    @Override
    public int getItemCount() {
        return villageList.size();
    }

    public static class VillageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;

        public VillageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_village);
            nameTextView = itemView.findViewById(R.id.text_village_name);
        }
    }
} 