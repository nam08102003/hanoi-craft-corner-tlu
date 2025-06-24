package com.example.hanoicraftcorner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.model.Product;

import java.util.List;

public class SuggestedProductAdapter extends RecyclerView.Adapter<SuggestedProductAdapter.SuggestedProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public SuggestedProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public SuggestedProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_suggested_product, parent, false);
        return new SuggestedProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestedProductViewHolder holder, int position) {
        Product product = productList.get(position);
        // Use getImages() instead of getImageUrls()
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            Glide.with(context)
                    .load(product.getImages().get(0))
                    .placeholder(R.drawable.ic_placeholder_default)
                    .into(holder.imageView);
        }

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Mở sản phẩm: " + product.getName(), Toast.LENGTH_SHORT).show();
            // TODO: Mở trang chi tiết sản phẩm
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class SuggestedProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public SuggestedProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_suggested_product);
        }
    }
}
