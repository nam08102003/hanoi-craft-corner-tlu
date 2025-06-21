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
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyProductsAdapter extends RecyclerView.Adapter<MyProductsAdapter.ProductViewHolder> {
    public interface OnProductActionListener {
        void onEdit(DocumentSnapshot product);
        void onDelete(DocumentSnapshot product);
    }

    private final List<DocumentSnapshot> products = new ArrayList<>();
    private final OnProductActionListener listener;

    public MyProductsAdapter(OnProductActionListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<DocumentSnapshot> productList) {
        products.clear();
        if (productList != null && !productList.isEmpty()) products.addAll(productList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        DocumentSnapshot product = products.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
        private final TextView name, quantity, price;
        private final View layoutEdit, layoutDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_product);
            name = itemView.findViewById(R.id.text_product_name);
            quantity = itemView.findViewById(R.id.text_product_quantity);
            price = itemView.findViewById(R.id.text_product_price);
            layoutEdit = itemView.findViewById(R.id.layout_edit);
            layoutDelete = itemView.findViewById(R.id.layout_delete);
        }

        public void bind(DocumentSnapshot product, OnProductActionListener listener) {
            name.setText(product.getString("Name"));
            quantity.setText("Số lượng: " + (product.getString("Quantity") != null ? product.getString("Quantity") : "0"));
            String priceValue = product.getString("Price");
            if (priceValue != null) {
                try {
                    long priceLong = Long.parseLong(priceValue.replace(".", ""));
                    java.text.NumberFormat nf = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
                    price.setText("Giá: " + nf.format(priceLong) + "₫");
                } catch (Exception e) {
                    price.setText("Giá: " + priceValue + "₫");
                }
            } else {
                price.setText("Giá: 0₫");
            }
            String imageUrl = product.getString("Image");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(image.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(image);
            } else {
                image.setImageResource(R.drawable.ic_launcher_foreground);
            }
            layoutEdit.setOnClickListener(v -> listener.onEdit(product));
            layoutDelete.setOnClickListener(v -> listener.onDelete(product));
        }
    }
}
