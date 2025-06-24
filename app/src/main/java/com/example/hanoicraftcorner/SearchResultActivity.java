package com.example.hanoicraftcorner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoicraftcorner.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import com.example.hanoicraftcorner.adapter.ProductAdapter;

public class SearchResultActivity extends AppCompatActivity {
    private EditText etSearch;
    private ImageView ivBack, ivSearchIcon;
    private RecyclerView rvSearchResults;
    private Button btnSeeMore;
    private LinearLayout layoutShopHighlight;
    private TextView tvShopName;
    private Button btnViewShop;
    private List<Product> searchResultsList = new ArrayList<>();
    private ProductAdapter searchResultsAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        etSearch = findViewById(R.id.et_search);
        ivBack = findViewById(R.id.iv_back);
        ivSearchIcon = findViewById(R.id.iv_search_icon);
        rvSearchResults = findViewById(R.id.rv_search_results);
        btnSeeMore = findViewById(R.id.btn_see_more);
        layoutShopHighlight = findViewById(R.id.layout_shop_highlight);
        tvShopName = findViewById(R.id.tv_shop_name);
        btnViewShop = findViewById(R.id.btn_view_shop);

        db = FirebaseFirestore.getInstance();
        rvSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
        searchResultsAdapter = new ProductAdapter(this, searchResultsList);
        rvSearchResults.setAdapter(searchResultsAdapter);

        // Nhận từ khóa tìm kiếm từ Intent
        String keyword = getIntent().getStringExtra("keyword");
        if (keyword != null) {
            etSearch.setText(keyword);
            etSearch.setSelection(keyword.length());
            etSearch.requestFocus();
            etSearch.postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 100);
            performSearch(keyword);
        }

        ivBack.setOnClickListener(v -> finish());
        ivSearchIcon.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                performSearch(query);
            }
        });
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                performSearch(query);
            }
            return true;
        });
        // TODO: Xử lý các chip lọc, xem shop, xem thêm...
    }

    private void performSearch(String query) {
        Toast.makeText(this, "Đang tìm kiếm: " + query, Toast.LENGTH_SHORT).show();
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        searchResultsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            // TODO: Lọc và thêm vào searchResultsList
                        }
                        searchResultsAdapter.notifyDataSetChanged();
                    }
                });
    }
} 