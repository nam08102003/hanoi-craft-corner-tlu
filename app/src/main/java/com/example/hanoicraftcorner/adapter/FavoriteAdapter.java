package com.example.hanoicraftcorner.adapter;



import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.Model.FavoriteItem;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<FavoriteItem> favoriteItems;
    private OnItemClickListener listener;

    // Interface to handle item click events



    public interface OnItemClickListener {
        void onRemoveClick(int position);
        void onDetailsClick(FavoriteItem item);
    }

    public FavoriteAdapter(List<FavoriteItem> favoriteItems, OnItemClickListener listener) {
        this.favoriteItems = favoriteItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dashboard_user, parent, false);
        return new FavoriteViewHolder(view);
    }

    // Trong FavoriteAdapter.java
    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoriteItem currentItem = favoriteItems.get(position);

        holder.productName.setText(currentItem.getName());
        holder.productQuantity.setText("Số lượng: " + currentItem.getQuantity());

        String imageUrl = currentItem.getImageUrl(); // Lấy URL ảnh
        Log.d(TAG, "Item: " + currentItem.getName() + ", ImageUrl: " + imageUrl);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext()) // Lấy context từ itemView của ViewHolder
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder_default) // Ảnh hiển thị trong khi tải
                    .error(R.drawable.icon)       // Ảnh hiển thị nếu có lỗi tải
                    .into(holder.productImage);                  // ImageView để hiển thị ảnh
        } else {
            // Nếu không có URL hoặc URL rỗng, hiển thị ảnh placeholder
            Log.w(TAG, "Image URL is null or empty for item: " + currentItem.getName() + ". Setting placeholder.");
            holder.productImage.setImageResource(R.drawable.ic_placeholder_default);
        }
    }

    @Override
    public int getItemCount() {
        return favoriteItems.size();
    }

    public void removeItem(int position) {
        favoriteItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, favoriteItems.size());
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productQuantity;
        ImageButton btnRemove;
        ImageView btnDetails;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.ivProductImage);
            productName = itemView.findViewById(R.id.tvProductName);
            productQuantity = itemView.findViewById(R.id.tvProductQuantity);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnDetails = itemView.findViewById(R.id.btnDetails);

            btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onRemoveClick(position);
                    }
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDetailsClick(favoriteItems.get(position));
                    }
                }
            });
        }
    }
}
