package com.example.hanoicraftcorner.main.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.adapter.AdminUserAdapter;
import com.example.hanoicraftcorner.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class AdminUserManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminUserAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private FirebaseFirestore db;

    private EditText edtSearch;
    private Button btnSearch;
    private Spinner spinnerSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_user_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        spinnerSort = findViewById(R.id.spinnerSort);


        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminUserAdapter(this, userList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        fetchAllUsers("fullname", Query.Direction.ASCENDING);

        btnSearch.setOnClickListener(v -> searchUser());

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applySorting(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fetchAllUsers(String field, Query.Direction direction) {
        db.collection("users")
                .orderBy(field, direction)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        User user = doc.toObject(User.class);
                        String user_id = doc.getId();
                        user.setUser_id(user_id);
                        userList.add(user);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void searchUser() {
        String keyword = edtSearch.getText().toString().trim();
        if (keyword.isEmpty()) {
            fetchAllUsers("fullname", Query.Direction.ASCENDING);
            return;
        }

        db.collection("users")
                .orderBy("fullname")
                .startAt(keyword)
                .endAt(keyword + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        User user = doc.toObject(User.class);
                        String user_id = doc.getId();
                        user.setUser_id(user_id);
                        userList.add(user);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tìm kiếm: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void applySorting(int position) {
        switch (position) {
            case 0: // Tên tăng
                fetchAllUsers("fullname", Query.Direction.ASCENDING);
                break;
            case 1: // Tên giảm
                fetchAllUsers("fullname", Query.Direction.DESCENDING);
                break;
            case 2: // Ngày tạo tăng
                fetchAllUsers("created_at", Query.Direction.ASCENDING);
                break;
            case 3: // Ngày tạo giảm
                fetchAllUsers("created_at", Query.Direction.DESCENDING);
                break;
        }
    }
}