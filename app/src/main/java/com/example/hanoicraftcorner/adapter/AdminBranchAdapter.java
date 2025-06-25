package com.example.hanoicraftcorner.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.main.admin.AdminBranchDetailActivity;
import com.example.hanoicraftcorner.model.User;
import com.google.gson.Gson;

import java.util.List;

public class AdminBranchAdapter extends RecyclerView.Adapter<AdminBranchAdapter.AdminBranchViewHolder>{

    private List<User> userList;
    private Context context;

    public AdminBranchAdapter(Context context, List<User> list) {
        this.context = context;
        this.userList = list;
    }

    @NonNull
    @Override
    public AdminBranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_branch, parent, false);
        return new AdminBranchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminBranchViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvBrandName.setText(user.getBrand_name());
        holder.tvUserName.setText(user.getName());

        switch (user.getStatus()) {
            case "active":
                holder.tvStatus.setText("Đã duyệt");
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "pending":
                holder.tvStatus.setText("Chờ duyệt");
                holder.tvStatus.setTextColor(Color.parseColor("#FFA000"));
                break;
            case "rejected":
                holder.tvStatus.setText("Bị từ chối");
                holder.tvStatus.setTextColor(Color.RED);
                break;
        }

        // Xử lý sự kiện nếu cần
        holder.ivEdit.setOnClickListener(v -> {
            // TODO: Edit logic
        });

        holder.ivDelete.setOnClickListener(v -> {
            // TODO: Delete logic
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminBranchDetailActivity.class);
            intent.putExtra("branch", new Gson().toJson(user));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class AdminBranchViewHolder extends RecyclerView.ViewHolder {
        TextView tvBrandName, tvUserName, tvStatus;
        ImageView ivEdit, ivDelete;

        public AdminBranchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBrandName = itemView.findViewById(R.id.tvBrandName);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
