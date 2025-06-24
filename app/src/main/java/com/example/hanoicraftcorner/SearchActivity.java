package com.example.hanoicraftcorner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoicraftcorner.adapter.ProductAdapter;
import com.example.hanoicraftcorner.adapter.SuggestedProductAdapter;
import com.example.hanoicraftcorner.model.Product;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SearchPrefs";
    private static final String KEY_SEARCH_HISTORY = "SearchHistory";

    private EditText etSearch;
    private ImageView ivSearchIcon;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;

    // Recent & Suggested Views
    private LinearLayout initialSearchView;
    private ChipGroup chipGroupRecent;
    private TextView tvRecentSearches;
    private RecyclerView rvSuggestedProducts;
    private SuggestedProductAdapter suggestedProductAdapter;
    private List<Product> suggestedProductList;

    // Search Result Views
    private RecyclerView rvSearchResults;
    private ProductAdapter searchResultsAdapter;
    private List<Product> searchResultsList;
    private TextView tvNoResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Common UI
        etSearch = findViewById(R.id.et_search);
        ivSearchIcon = findViewById(R.id.iv_search_icon);
        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> finish());

        // Initial View UI
        initialSearchView = findViewById(R.id.initial_search_view);
        chipGroupRecent = findViewById(R.id.chip_group_recent);
        tvRecentSearches = findViewById(R.id.tv_recent_searches);
        rvSuggestedProducts = findViewById(R.id.rv_suggested_products);

        // Search Result UI
        rvSearchResults = findViewById(R.id.rv_search_results);
        tvNoResults = findViewById(R.id.tv_no_results);

        setupInitialView();
        setupSearchResultsView();

        setupSearchListener();
        loadAndDisplayRecentSearches();
        loadSuggestedProducts();

        // Tự động focus và hiện bàn phím khi mở SearchActivity
        etSearch.requestFocus();
        etSearch.postDelayed(() -> {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etSearch, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
    }

    private void setupInitialView() {
        rvSuggestedProducts.setLayoutManager(new GridLayoutManager(this, 2));
        suggestedProductList = new ArrayList<>();
        suggestedProductAdapter = new SuggestedProductAdapter(this, suggestedProductList);
        rvSuggestedProducts.setAdapter(suggestedProductAdapter);
    }

    private void setupSearchResultsView() {
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        searchResultsList = new ArrayList<>();
        // We can reuse the ProductAdapter for search results
        searchResultsAdapter = new ProductAdapter(this, searchResultsList);
        rvSearchResults.setAdapter(searchResultsAdapter);
    }

    private void setupSearchListener() {
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = etSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                    intent.putExtra("keyword", query);
                    startActivity(intent);
                }
                return true;
            }
            return false;
        });

        ivSearchIcon.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                intent.putExtra("keyword", query);
                startActivity(intent);
            }
        });
    }

    private void performSearch(String query) {
        Toast.makeText(this, "Đang tìm kiếm: " + query, Toast.LENGTH_SHORT).show();
        saveSearchQuery(query);

        initialSearchView.setVisibility(View.GONE);
        tvNoResults.setVisibility(View.GONE);
        rvSearchResults.setVisibility(View.GONE);

        db.collection("products")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    searchResultsList.clear();
                    List<Product> exactMatch = new ArrayList<>();
                    List<Product> similarMatch = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = document.toObject(Product.class);
                        String name = product.getName();
                        if (name == null) continue; // Bỏ qua sản phẩm không có tên
                        name = name.toLowerCase();
                        String q = query.toLowerCase();
                        if (name.equals(q)) {
                            exactMatch.add(product);
                        } else if (name.contains(q)) {
                            similarMatch.add(product);
                        }
                    }
                    // Sản phẩm chính lên đầu, tương tự phía sau
                    searchResultsList.addAll(exactMatch);
                    searchResultsList.addAll(similarMatch);

                    if (searchResultsList.isEmpty()) {
                        tvNoResults.setVisibility(View.VISIBLE);
                        rvSearchResults.setVisibility(View.GONE);
                    } else {
                        searchResultsAdapter.notifyDataSetChanged();
                        tvNoResults.setVisibility(View.GONE);
                        rvSearchResults.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.w("Firestore", "Error getting documents.", task.getException());
                    tvNoResults.setVisibility(View.VISIBLE);
                    rvSearchResults.setVisibility(View.GONE);
                }
            });
    }

    private void saveSearchQuery(String query) {
        Set<String> history = new HashSet<>(sharedPreferences.getStringSet(KEY_SEARCH_HISTORY, new HashSet<>()));
        history.add(query);
        sharedPreferences.edit().putStringSet(KEY_SEARCH_HISTORY, history).apply();
        loadAndDisplayRecentSearches();
    }

    private void removeSearchQuery(String query) {
        Set<String> history = new HashSet<>(sharedPreferences.getStringSet(KEY_SEARCH_HISTORY, new HashSet<>()));
        history.remove(query);
        sharedPreferences.edit().putStringSet(KEY_SEARCH_HISTORY, history).apply();
        loadAndDisplayRecentSearches();
    }

    private void loadAndDisplayRecentSearches() {
        chipGroupRecent.removeAllViews();
        Set<String> history = sharedPreferences.getStringSet(KEY_SEARCH_HISTORY, new HashSet<>());

        if (history.isEmpty()) {
            tvRecentSearches.setVisibility(View.GONE);
            chipGroupRecent.setVisibility(View.GONE);
        } else {
            tvRecentSearches.setVisibility(View.VISIBLE);
            chipGroupRecent.setVisibility(View.VISIBLE);
            List<String> sortedHistory = new ArrayList<>(history);

            for (String query : sortedHistory) {
                Chip chip = new Chip(this);
                chip.setText(query);
                chip.setCloseIconVisible(true);

                chip.setOnClickListener(v -> {
                    Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                    intent.putExtra("keyword", query);
                    startActivity(intent);
                });

                chip.setOnCloseIconClickListener(v -> removeSearchQuery(query));

                chipGroupRecent.addView(chip);
            }
        }
    }

    private void loadSuggestedProducts() {
        db.collection("products").limit(4).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                suggestedProductList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Product product = document.toObject(Product.class);
                    suggestedProductList.add(product);
                }
                suggestedProductAdapter.notifyDataSetChanged();
            } else {
                Log.w("Firestore", "Error getting suggested products.", task.getException());
            }
        });
    }
} 