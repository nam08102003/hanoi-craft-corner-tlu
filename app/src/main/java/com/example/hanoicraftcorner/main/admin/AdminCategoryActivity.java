package com.example.hanoicraftcorner.main.admin;

import static com.example.hanoicraftcorner.R.*;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.interfaces.OnCategoryListener;
import com.example.hanoicraftcorner.model.Category;
<<<<<<< Updated upstream
import com.example.hanoicraftcorner.adapter.CategoryAdapter;
=======
import com.google.android.material.bottomnavigation.BottomNavigationView;
>>>>>>> Stashed changes
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminCategoryActivity extends AppCompatActivity implements OnCategoryListener {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<Category> categoryList;

    private FirebaseFirestore db;
    private List<String> docIdList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_category);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_category);
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

        EditText edtSearch = findViewById(R.id.edtSearch);
        Button btnSearch = findViewById(R.id.btnSearch);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryList = new ArrayList<>();
        docIdList = new ArrayList<>();
<<<<<<< Updated upstream
        adapter = new CategoryAdapter(categoryList, docIdList, this, this);
=======
        adapter = new AdminCategoryAdapter(categoryList, docIdList, this,this);
>>>>>>> Stashed changes
        recyclerView.setAdapter(adapter);
        recyclerView.setAdapter(adapter);

        loadCategoriesFromFirestore();

        Button btnAddCategory = findViewById(R.id.btnAddCategory);
        btnAddCategory.setOnClickListener(v -> showAddCategoryDialog());

        btnSearch.setOnClickListener(v -> {
            String keyword = edtSearch.getText().toString().trim();
            if (keyword.isEmpty()) {
                loadCategoriesFromFirestore(); // Nếu rỗng thì load lại toàn bộ
            } else {
                searchCategories(keyword);
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadCategoriesFromFirestore() {
        db.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categoryList.clear();
                    docIdList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        String parentId = doc.getString("parent_id");

                        Timestamp createdTimestamp = doc.getTimestamp("created_at");
                        Timestamp updatedTimestamp = doc.getTimestamp("updated_at");

                        String createdAt = formatTimestamp(createdTimestamp);
                        String updatedAt = formatTimestamp(updatedTimestamp);

                        Category category = new Category(name, parentId, createdAt, updatedAt);
                        categoryList.add(category);
                        docIdList.add(doc.getId());
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminCategoryActivity.this, "Lỗi khi load dữ liệu", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error: ", e);
                });
    }

    private void searchCategories(String keyword) {
        db.collection("categories")
                .orderBy("created_at")
                .startAt(keyword)
                .endAt(keyword + "\uf8ff") // tìm tương đối
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categoryList.clear();
                    docIdList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        String parentId = doc.getString("parent_id");
                        Timestamp createdTimestamp = doc.getTimestamp("created_at");
                        Timestamp updatedTimestamp = doc.getTimestamp("updated_at");

                        String createdAt = formatTimestamp(createdTimestamp);
                        String updatedAt = formatTimestamp(updatedTimestamp);

                        Category category = new Category(name, parentId, createdAt, updatedAt);
                        categoryList.add(category);
                        docIdList.add(doc.getId());
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminCategoryActivity.this, "Lỗi khi tìm kiếm", Toast.LENGTH_SHORT).show();
                });
    }


    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText edtCategoryName = dialogView.findViewById(R.id.edtCategoryName);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = edtCategoryName.getText().toString().trim();
            if (name.isEmpty()) {
                edtCategoryName.setError("Tên không được để trống");
                return;
            }

            Map<String, Object> newCategory = new HashMap<>();
            newCategory.put("name", name);
            newCategory.put("parent_id", "");
            newCategory.put("created_at", FieldValue.serverTimestamp());
            newCategory.put("updated_at", FieldValue.serverTimestamp());

            db.collection("categories")
                    .add(newCategory)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(AdminCategoryActivity.this, "Đã thêm danh mục", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadCategoriesFromFirestore(); // reload data
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AdminCategoryActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void showEditCategoryDialog(Category category, String docId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(layout.dialog_edit_category, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText edtCategoryName = dialogView.findViewById(R.id.edtCategoryName);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Gán dữ liệu cũ
        edtCategoryName.setText(category.getName());

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String newName = edtCategoryName.getText().toString().trim();
            if (newName.isEmpty()) {
                edtCategoryName.setError("Tên không được để trống");
                return;
            }

            Map<String, Object> update = new HashMap<>();
            update.put("name", newName);
            update.put("updated_at", FieldValue.serverTimestamp());

            db.collection("categories").document(docId)
                    .update(update)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AdminCategoryActivity.this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadCategoriesFromFirestore(); // reload
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AdminCategoryActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void showDeleteCategoryDialog(String docId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(layout.dialog_delete_category, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button btnCancel = dialogView.findViewById(R.id.btnCancelDelete);
        Button btnDelete = dialogView.findViewById(R.id.btnConfirmDelete);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnDelete.setOnClickListener(v -> {
            db.collection("categories").document(docId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AdminCategoryActivity.this, "Xoá thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadCategoriesFromFirestore();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AdminCategoryActivity.this, "Lỗi khi xoá", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public void onEditClicked(Category category, String docId) {
        showEditCategoryDialog(category,docId);
    }

    @Override
    public void onDelete(Category category, String docId) {
        showDeleteCategoryDialog(docId);
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "";
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }
}