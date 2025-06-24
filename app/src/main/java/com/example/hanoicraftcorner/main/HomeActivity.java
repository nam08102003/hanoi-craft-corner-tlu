package com.example.hanoicraftcorner.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.hanoicraftcorner.model.FavoriteItem;
import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.adapter.BannerAdapter;
import com.example.hanoicraftcorner.adapter.HomeArtisanAdapter;
import com.example.hanoicraftcorner.adapter.HomeCategoryAdapter;
import com.example.hanoicraftcorner.adapter.HomeProductAdapter;
import com.example.hanoicraftcorner.model.Category;
import com.example.hanoicraftcorner.model.Product;
import com.example.hanoicraftcorner.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.hanoicraftcorner.SearchActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ViewPager2 bannerViewPager;
    private RecyclerView categoryRecycler, featuredRecycler, newProductRecycler, artisanRecycler;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Handler bannerHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bannerViewPager = findViewById(R.id.bannerViewPager);
        categoryRecycler = findViewById(R.id.categoryRecyclerView);
        featuredRecycler = findViewById(R.id.featuredRecyclerView);
        newProductRecycler = findViewById(R.id.newProductRecyclerView);
        artisanRecycler = findViewById(R.id.artisanRecyclerView);

        // Thêm sự kiện click cho ô tìm kiếm
        LinearLayout searchBox = findViewById(R.id.searchBox);
        searchBox.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        EditText searchInput = findViewById(R.id.searchInput);
        searchInput.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_menu) {
                drawerLayout.openDrawer(GravityCompat.END);
                return true;
            }
            return false;
        });

        setupBannerSlider();
        loadCategories();
        loadFeaturedProducts();
        loadNewProducts();
        loadArtisans();
    }

    private void setupBannerSlider() {
        db.collection("ads").get().addOnSuccessListener(querySnapshot -> {
            List<String> bannerUrls = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot) {
                bannerUrls.add(doc.getString("Image"));
            }
            BannerAdapter adapter = new BannerAdapter(this,bannerUrls);
            bannerViewPager.setAdapter(adapter);
            autoSlideBanner();
        });
    }

    private void autoSlideBanner() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int next = (bannerViewPager.getCurrentItem() + 1) % bannerViewPager.getAdapter().getItemCount();
                bannerViewPager.setCurrentItem(next, true);
                bannerHandler.postDelayed(this, 5000);
            }
        };
        bannerHandler.postDelayed(runnable, 5000);
    }

    private void loadCategories() {
        db.collection("categories").get().addOnSuccessListener(snapshot -> {
            List<Category> list = snapshot.toObjects(Category.class);
            HomeCategoryAdapter adapter = new HomeCategoryAdapter(this,list);
            categoryRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            categoryRecycler.setAdapter(adapter);
        });
    }

    private void loadFeaturedProducts() {
        db.collection("products").whereEqualTo("hot", true).get().addOnSuccessListener(snapshot -> {
            List<Product> list = snapshot.toObjects(Product.class);
            HomeProductAdapter adapter = new HomeProductAdapter(this,list);
            featuredRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            featuredRecycler.setAdapter(adapter);
        });
    }

    private void loadNewProducts() {
        db.collection("products")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Product> list = snapshot.toObjects(Product.class);
                    HomeProductAdapter adapter = new HomeProductAdapter(this,list);
                    newProductRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                    newProductRecycler.setAdapter(adapter);
                });
    }

    private void loadArtisans() {
        db.collection("users")
                .whereEqualTo("role", "artisan")
                .whereEqualTo("status", "avaiable")
                .whereEqualTo("hot", true)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<User> list = snapshot.toObjects(User.class);
                    HomeArtisanAdapter adapter = new HomeArtisanAdapter(this,list);
                    artisanRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                    artisanRecycler.setAdapter(adapter);
                });
    }
}