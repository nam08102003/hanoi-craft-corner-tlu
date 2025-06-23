package com.example.hanoicraftcorner.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.main.admin.AdminUserDetailActivity;
import com.example.hanoicraftcorner.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private Context context;

    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.userList = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvFullname.setText(user.getFullname());
        holder.tvRole.setText(user.getRole());

        if ("Nghệ nhân".equalsIgnoreCase(user.getRole()) && "chờ duyệt".equalsIgnoreCase(user.getStatus())) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText("Chờ duyệt");
        } else {
            holder.tvStatus.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(user.getAvatar())
                .into(holder.imageAvatar);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminUserDetailActivity.class);
            intent.putExtra("user", user);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullname, tvRole, tvStatus;
        ImageView imageAvatar;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullname = itemView.findViewById(R.id.tvFullname);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            imageAvatar = itemView.findViewById(R.id.imageAvatar);
        }
    }
}
