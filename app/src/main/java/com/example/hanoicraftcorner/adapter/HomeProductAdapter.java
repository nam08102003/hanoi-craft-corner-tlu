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
import com.example.hanoicraftcorner.model.Product;

import java.util.List;

public class HomeProductAdapter extends RecyclerView.Adapter<HomeProductAdapter.ProductViewHolder>{
    private List<Product> productList;
    private Context context;

    public HomeProductAdapter(Context ctx, List<Product> products) {
        this.context = ctx;
        this.productList = products;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice;

        public ProductViewHolder(View view) {
            super(view);
            productImage = view.findViewById(R.id.imageProduct);
            productName = view.findViewById(R.id.textName);
            productPrice = view.findViewById(R.id.textPrice);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product p = productList.get(position);
        holder.productName.setText(p.getName());
        holder.productPrice.setText(p.getPrice());

        if (p.getImages() != null && !p.getImages().isEmpty()) {
            Glide.with(context)
                    .load(p.getImages().get(0))
                    .into(holder.productImage);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
