package com.example.hanoicraftcorner;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoicraftcorner.adapter.FavoriteAdapter;
import com.example.hanoicraftcorner.model.FavoriteItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView rvFavorites;
    private FavoriteAdapter favoriteAdapter;
    private List<FavoriteItem> favoriteList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView tvNoFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tvNoFavorites = findViewById(R.id.tv_no_favorites);
        rvFavorites = findViewById(R.id.rv_favorites);
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        favoriteList = new ArrayList<>();
        favoriteAdapter = new FavoriteAdapter(favoriteList, new FavoriteAdapter.OnItemClickListener() {
            @Override
            public void onRemoveClick(int position) {
            }

            @Override
            public void onDetailsClick(FavoriteItem item) {
            }
        });
        rvFavorites.setAdapter(favoriteAdapter);

        loadFavoriteItems();
    }

    private void loadFavoriteItems() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem mục yêu thích", Toast.LENGTH_SHORT).show();
            tvNoFavorites.setVisibility(View.VISIBLE);
            tvNoFavorites.setText("Vui lòng đăng nhập");
            return;
        }

        String userId = currentUser.getUid();
        db.collection("users").document(userId).collection("favorites")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        favoriteList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FavoriteItem item = document.toObject(FavoriteItem.class);
                            favoriteList.add(item);
                        }
                        favoriteAdapter.notifyDataSetChanged();

                        if (favoriteList.isEmpty()) {
                            tvNoFavorites.setVisibility(View.VISIBLE);
                            rvFavorites.setVisibility(View.GONE);
                        } else {
                            tvNoFavorites.setVisibility(View.GONE);
                            rvFavorites.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(this, "Lỗi khi tải danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    }
                });
    }
} 