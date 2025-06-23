package com.example.hanoicraftcorner.main.admin;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminUserDetailActivity extends AppCompatActivity {

    private ImageView imageAvatar, btnDelete;
    private TextView tvFullname, tvRole, tvPhone, tvEmail, tvLink;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_user_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageAvatar = findViewById(R.id.imageAvatar);
        btnDelete = findViewById(R.id.btnDelete);
        tvFullname = findViewById(R.id.tvFullname);
        tvRole = findViewById(R.id.tvRole);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);

        db = FirebaseFirestore.getInstance();

        User user = (User) getIntent().getSerializableExtra("user");

        if (user != null) {
            userId = user.getEmail();

            tvFullname.setText(user.getFullname());
            tvRole.setText(user.getRole());
            tvPhone.setText(user.getPhone());
            tvEmail.setText(user.getEmail());
            tvLink.setText(user.getIntroduce());

            Glide.with(this)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.ic_user_placeholder)
                    .into(imageAvatar);
        }

        btnDelete.setOnClickListener(v -> showDeleteConfirmDialog());
    }

    private void showDeleteConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_delete, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            if (userId != null) {
                deleteUser(userId);
            }
        });

        dialog.show();
    }


    private void deleteUser(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query) {
                        doc.getReference().delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Đã xoá người dùng", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Lỗi xoá: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });
    }
}