package com.example.hanoicraftcorner.main;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hanoicraftcorner.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.View;

public class MainBoardArtisan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String email = getIntent().getStringExtra("email");
//        finishAffinity();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_board_artisan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Truy cập dữ liệu Firestore bằng email
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("Users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    com.google.firebase.firestore.DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                    String username = userDoc.getString("username");
                    // Hiển thị tên người dùng lên layout (ví dụ: trong frame_menu)
                    android.widget.TextView tv = findViewById(R.id.text_username);
                    if (tv != null && username != null) {
                        tv.setText(username);
                    }
                }
            });

        // BottomNavigationView logic
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        View frameHome = findViewById(R.id.frame_home);
        View frameHeart = findViewById(R.id.frame_heart);
        View frameWallet = findViewById(R.id.frame_wallet);
        View frameMenu = findViewById(R.id.frame_menu);

        // Default: show frame_menu, hide others
        frameHome.setVisibility(View.VISIBLE);
        frameHeart.setVisibility(View.GONE);
        frameWallet.setVisibility(View.GONE);
        frameMenu.setVisibility(View.GONE);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            frameHome.setVisibility(View.GONE);
            frameHeart.setVisibility(View.GONE);
            frameWallet.setVisibility(View.GONE);
            frameMenu.setVisibility(View.GONE);
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                frameHome.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.nav_heart) {
                frameHeart.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.nav_wallet) {
                frameWallet.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.nav_menu) {
                frameMenu.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });
    }
}