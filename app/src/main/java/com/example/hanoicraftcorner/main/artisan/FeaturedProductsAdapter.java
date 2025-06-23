package com.example.hanoicraftcorner.main.artisan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.hanoicraftcorner.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeaturedProductsAdapter extends RecyclerView.Adapter<FeaturedProductsAdapter.ProductViewHolder> {
    private final List<Map<String, Object>> products = new ArrayList<>();

    public void setProducts(List<Map<String, Object>> productList) {
        products.clear();
        if (productList != null) products.addAll(productList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_featured_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvProductPrice;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
        }
        public void bind(Map<String, Object> product) {
            Object nameObj = product.get("Name");
            tvProductName.setText(nameObj != null ? nameObj.toString() : "");
            Object priceObj = product.get("Price");
            String price = priceObj != null ? priceObj.toString() : "";
            tvProductPrice.setText(price.isEmpty() ? "" : price + "₫");
            Object imageObj = product.get("Image");
            String imageUrl = imageObj != null ? imageObj.toString() : "";
            if (!imageUrl.isEmpty()) {
                Glide.with(imgProduct.getContext()).load(imageUrl).placeholder(R.drawable.ic_launcher_foreground).into(imgProduct);
            } else {
                imgProduct.setImageResource(R.drawable.ic_launcher_foreground);
            }
        }
    }
}
