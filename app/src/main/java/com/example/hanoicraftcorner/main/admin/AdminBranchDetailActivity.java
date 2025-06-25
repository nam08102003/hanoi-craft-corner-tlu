package com.example.hanoicraftcorner.main.admin;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.adapter.ProductAdapter;
import com.example.hanoicraftcorner.model.Product;
import com.example.hanoicraftcorner.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AdminBranchDetailActivity extends AppCompatActivity {
    TextView tvBrandName, tvIsFeatured, tvName, tvPhone, tvEmail, tvStatus, tvCreatedAt, tvUpdatedAt, tvIntroduce;
    RatingBar ratingBar;
    LinearLayout layoutImages, formButton;
    RecyclerView rvProducts;

    Button btnDecline, btnConfirm;

    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_branch_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_shop);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
                return true;
            } else if (id == R.id.nav_user) {
                startActivity(new Intent(this, AdminUserManagementActivity.class));
                return true;
            } else if (id == R.id.nav_category) {
                startActivity(new Intent(this, AdminCategoryActivity.class));
                return true;
            }  else if (id == R.id.nav_shop) {
                startActivity(new Intent(this, AdminBrandActivity.class));
                return true;
            } else if (id == R.id.nav_menu) {
//                startActivity(new Intent(this, MenuActivity.class));
                return true;
            }
            return false;
        });

        tvBrandName = findViewById(R.id.tvBrandName);
        tvIsFeatured = findViewById(R.id.tvIsFeatured);
        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        tvStatus = findViewById(R.id.tvStatus);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvUpdatedAt = findViewById(R.id.tvUpdatedAt);
        tvIntroduce = findViewById(R.id.tvIntroduce);
        ratingBar = findViewById(R.id.ratingBar);
        layoutImages = findViewById(R.id.layoutImages);
        rvProducts = findViewById(R.id.rvProducts);
        formButton = findViewById(R.id.form_button);
        btnDecline = findViewById(R.id.btn_decline);
        btnConfirm = findViewById(R.id.btn_confirm);

        String userJson = getIntent().getStringExtra("branch");
        User user = new Gson().fromJson(userJson, User.class);

        if(user != null) {
            tvBrandName.setText(user.getBrand_name());
            if(user.isHot()) {
                tvIsFeatured.setVisibility(View.VISIBLE);
            } else {
                tvIsFeatured.setVisibility(View.GONE);
            }

            if("pending".equals(user.getStatus())) {
                formButton.setVisibility(View.VISIBLE);
            }

            tvName.setText(user.getName());
            tvPhone.setText(user.getPhone());
            tvEmail.setText(user.getEmail());
            tvStatus.setText(getReadableStatus(user.getStatus()));
            tvCreatedAt.setText(formatTimestamp(user.getCreated_at()));
            tvUpdatedAt.setText(formatTimestamp(user.getUpdated_at()));
            tvIntroduce.setText(user.getIntroduce());

            String userId = user.getUser_id();

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateStatus("verified", userId);
                }
            });

            btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateStatus("rejected", userId);
                }
            });

            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.parseColor("#FFD700"), PorterDuff.Mode.SRC_ATOP); // Filled stars
            stars.getDrawable(1).setColorFilter(Color.parseColor("#CCCCCC"), PorterDuff.Mode.SRC_ATOP); // Half filled (if any)
            stars.getDrawable(0).setColorFilter(Color.parseColor("#EEEEEE"), PorterDuff.Mode.SRC_ATOP);

            layoutImages.removeAllViews();
            for (String url : user.getImages()) {
                ImageView img = new ImageView(this);
                img.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(this).load(url).into(img);
                layoutImages.addView(img);
            }

        }
    }

    private String getReadableStatus(String status) {
        switch (status) {
            case "active": return "Đã được duyệt";
            case "pending": return "Chờ duyệt";
            case "rejected": return "Bị từ chối";
            default: return status;
        }
    }

    private String formatTimestamp(Timestamp ts) {
        if (ts == null) return "";
        DateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        return df.format(ts.toDate());
    }

    private void updateStatus(String status, String userId) {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.update("status", status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AdminBranchDetailActivity.this, "Cập nhật phê duyệt thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminBranchDetailActivity.this, "Cập nhật phê duyệt thất ba", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}