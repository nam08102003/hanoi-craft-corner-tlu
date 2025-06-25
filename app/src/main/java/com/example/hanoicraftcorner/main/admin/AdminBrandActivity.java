package com.example.hanoicraftcorner.main.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.adapter.AdminBranchAdapter;
import com.example.hanoicraftcorner.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class AdminBrandActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminBranchAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private FirebaseFirestore db;
    Spinner spinnerStatus, spinnerSort;

    EditText edtSearch;
    Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_brand);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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


        spinnerStatus = findViewById(R.id.spinnerStatus);
        spinnerSort = findViewById(R.id.spinnerSort);
        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new AdminBranchAdapter(this, userList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        fetchUsers();

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                applyFilterAndSort();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                applyFilterAndSort();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSearch.setOnClickListener(v -> applyFilterAndSort());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchUsers() {
        db.collection("users")
                .whereEqualTo("role", "artisan")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        User user = doc.toObject(User.class);
                        assert user != null;
                        user.setUser_id(doc.getId());
                        userList.add(user);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void applyFilterAndSort() {
        String selectedStatus = spinnerStatus.getSelectedItem().toString();
        String selectedSort = spinnerSort.getSelectedItem().toString();
        String keyword = edtSearch.getText().toString().trim().toLowerCase();

        Query query = db.collection("users").whereEqualTo("role", "artisan");

        // Filter theo trạng thái
        if (!selectedStatus.equals("Tất cả")) {
            if (selectedStatus.equals("Đã duyệt")) query = query.whereEqualTo("status", "active");
            else if (selectedStatus.equals("Chờ duyệt")) query = query.whereEqualTo("status", "pending");
            else if (selectedStatus.equals("Từ chối")) query = query.whereEqualTo("status", "rejected");
        }

        // Sắp xếp
        if (selectedSort.equals("Mới nhất")) {
            query = query.orderBy("created_at", Query.Direction.DESCENDING);
        } else {
            query = query.orderBy("created_at", Query.Direction.ASCENDING);
        }

        query.get().addOnSuccessListener(snapshot -> {
            userList.clear();
            for (DocumentSnapshot doc : snapshot) {

                User user = doc.toObject(User.class);
                assert user != null;

                if (!keyword.isEmpty()) {
                    String name = user.getName() == null ? "" : user.getName().toLowerCase();
                    String brand = user.getBrand_name() == null ? "" : user.getBrand_name().toLowerCase();
                    if (!name.contains(keyword) && !brand.contains(keyword)) continue;
                }

                user.setUser_id(doc.getId());
                userList.add(user);
            }
            adapter.notifyDataSetChanged();
        });
    }

}