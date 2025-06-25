package com.example.hanoicraftcorner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.interfaces.OnCategoryListener;
import com.example.hanoicraftcorner.model.Category;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<String> docIdList;
    private OnCategoryListener actionListener;
    private List<Category> categoryList;

<<<<<<< Updated upstream:app/src/main/java/com/example/hanoicraftcorner/adapter/CategoryAdapter.java
    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
=======
    public AdminCategoryAdapter(List<Category> categoryList, List<String> docIdList, Context context, OnCategoryListener actionListener) {
>>>>>>> Stashed changes:app/src/main/java/com/example/hanoicraftcorner/adapter/AdminCategoryAdapter.java
        this.categoryList = categoryList;
        this.docIdList = docIdList;
        this.context = context;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        String docId = docIdList.get(position);

        holder.txtName.setText(category.getName());

        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditClicked(category, docId);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDelete(categoryList.get(position), docIdList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView btnEdit, btnDelete;
        TextView txtName, txtCreatedAt;


        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
        }
    }
<<<<<<< Updated upstream:app/src/main/java/com/example/hanoicraftcorner/adapter/CategoryAdapter.java
} 
=======

    public interface OnCategoryClickListener {
        void onEdit(int position);
        void onDelete(int position);
    }
}
>>>>>>> Stashed changes:app/src/main/java/com/example/hanoicraftcorner/adapter/AdminCategoryAdapter.java
