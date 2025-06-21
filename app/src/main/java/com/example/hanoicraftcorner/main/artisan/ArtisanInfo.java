package com.example.hanoicraftcorner.main.artisan;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.utils.CloudinaryUploader;

public class ArtisanInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_artisan_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Xử lý nút back
        findViewById(R.id.btn_back).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        // Lấy email từ Intent
        String email = getIntent().getStringExtra("email");
        // Ánh xạ view
        final android.widget.TextView tvUsername = findViewById(R.id.tv_username);
        final android.widget.TextView tvPhone = findViewById(R.id.tv_phone);
        final android.widget.TextView tvEmail = findViewById(R.id.tv_email);
        final android.widget.TextView tvStoreName = findViewById(R.id.tv_store_name);
        final android.widget.TextView tvIntroduce = findViewById(R.id.tv_introduce);
        final android.widget.ImageView avatarImage = findViewById(R.id.avatar_image);
        final android.widget.ImageView coverImage = findViewById(R.id.cover_image);
        final android.widget.EditText etUsername = findViewById(R.id.et_username);
        final android.widget.EditText etPhone = findViewById(R.id.et_phone);
        final android.widget.EditText etEmail = findViewById(R.id.et_email);
        final android.widget.EditText etStoreName = findViewById(R.id.et_store_name);
        final android.widget.EditText etIntroduce = findViewById(R.id.et_introduce);
        final android.widget.Button btnSave = findViewById(R.id.btn_save);
        final android.widget.ImageButton btnEdit = findViewById(R.id.btn_edit);
        final android.widget.TextView tvAvatarHint = findViewById(R.id.tv_avatar_hint);
        final android.widget.TextView tvCoverHint = findViewById(R.id.tv_cover_hint);

        // Lấy dữ liệu từ Firestore
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        // Lưu lại url ảnh cũ để xóa nếu có upload mới
        final String[] oldAvatarUrl = {null};
        final String[] oldCoverUrl = {null};
        db.collection("users")
            .whereEqualTo("Email", email)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    com.google.firebase.firestore.DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                    String username = userDoc.getString("Username");
                    String phone = userDoc.getString("Phone");
                    String storeName = userDoc.getString("StoreOrBrand");
                    String introduce = userDoc.getString("Introduce");
                    String avatarUrl = userDoc.getString("Avatar");
                    String coverUrl = userDoc.getString("Background");
                    oldAvatarUrl[0] = avatarUrl;
                    oldCoverUrl[0] = coverUrl;
                    tvUsername.setText(username != null ? username : "");
                    tvPhone.setText(phone != null ? phone : "");
                    tvEmail.setText(email != null ? email : "");
                    tvStoreName.setText(storeName != null ? storeName : "");
                    tvIntroduce.setText(introduce != null ? introduce : "");
                    // Load avatar và cover nếu có
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        try {
                            com.bumptech.glide.Glide.with(this).load(avatarUrl).into(avatarImage);
                        } catch (Exception ignored) {}
                    }
                    if (coverUrl != null && !coverUrl.isEmpty()) {
                        try {
                            com.bumptech.glide.Glide.with(this).load(coverUrl).into(coverImage);
                        } catch (Exception ignored) {}
                    }
                }
            });

        // Khi ấn nút chỉnh sửa
        btnEdit.setOnClickListener(v -> {
            // Ẩn TextView, hiện EditText và nút Lưu
            tvUsername.setVisibility(android.view.View.GONE);
            tvPhone.setVisibility(android.view.View.GONE);
            tvEmail.setVisibility(android.view.View.GONE);
            tvStoreName.setVisibility(android.view.View.GONE);
            tvIntroduce.setVisibility(android.view.View.GONE);
            etUsername.setVisibility(android.view.View.VISIBLE);
            etPhone.setVisibility(android.view.View.VISIBLE);
            etEmail.setVisibility(android.view.View.VISIBLE);
            etStoreName.setVisibility(android.view.View.VISIBLE);
            etIntroduce.setVisibility(android.view.View.VISIBLE);
            btnSave.setVisibility(android.view.View.VISIBLE);
            tvAvatarHint.setVisibility(android.view.View.VISIBLE);
            tvCoverHint.setVisibility(android.view.View.VISIBLE);
            // Set giá trị hiện tại vào EditText
            etUsername.setText(tvUsername.getText());
            etPhone.setText(tvPhone.getText());
            etEmail.setText(tvEmail.getText());
            etStoreName.setText(tvStoreName.getText());
            etIntroduce.setText(tvIntroduce.getText());
            // Hiện chú thích cho từng EditText
            etUsername.setHint("Tên người dùng");
            etPhone.setHint("Số điện thoại");
            etEmail.setHint("Email");
            etStoreName.setHint("Tên cửa hàng");
            etIntroduce.setHint("Giới thiệu về cửa hàng hoặc nghệ nhân");
        });

        // Khi ấn nút Lưu
        btnSave.setOnClickListener(v -> {
            String newUsername = etUsername.getText().toString().trim();
            String newPhone = etPhone.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();
            String newStoreName = etStoreName.getText().toString().trim();
            String newIntroduce = etIntroduce.getText().toString().trim();
            // Cập nhật Firestore
            db.collection("users")
                .whereEqualTo("Email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        com.google.firebase.firestore.DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                        userDoc.getReference().update(
                            "Username", newUsername,
                            "Phone", newPhone,
                            "Email", newEmail,
                            "StoreOrBrand", newStoreName,
                            "Introduce", newIntroduce
                        ).addOnSuccessListener(unused -> {
                            // Cập nhật lại UI
                            tvUsername.setText(newUsername);
                            tvPhone.setText(newPhone);
                            tvEmail.setText(newEmail);
                            tvStoreName.setText(newStoreName);
                            tvIntroduce.setText(newIntroduce);
                            tvAvatarHint.setVisibility(android.view.View.GONE);
                            tvCoverHint.setVisibility(android.view.View.GONE);
                            // Ẩn EditText, hiện TextView và ẩn nút Lưu
                            tvUsername.setVisibility(android.view.View.VISIBLE);
                            tvPhone.setVisibility(android.view.View.VISIBLE);
                            tvEmail.setVisibility(android.view.View.VISIBLE);
                            tvStoreName.setVisibility(android.view.View.VISIBLE);
                            tvIntroduce.setVisibility(android.view.View.VISIBLE);
                            etUsername.setVisibility(android.view.View.GONE);
                            etPhone.setVisibility(android.view.View.GONE);
                            etEmail.setVisibility(android.view.View.GONE);
                            etStoreName.setVisibility(android.view.View.GONE);
                            etIntroduce.setVisibility(android.view.View.GONE);
                            btnSave.setVisibility(android.view.View.GONE);
                        });
                    }
                });
        });

        // Xử lý chọn/chụp ảnh cho avatar và cover
        final androidx.activity.result.ActivityResultLauncher<android.content.Intent> avatarImagePicker =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    android.net.Uri uri = result.getData().getData();
                    if (uri != null) {
                        avatarImage.setImageURI(uri);
                        avatarImage.setTag(uri); // Lưu tạm uri để upload khi lưu
                    }
                }
            });
        final androidx.activity.result.ActivityResultLauncher<android.content.Intent> coverImagePicker =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    android.net.Uri uri = result.getData().getData();
                    if (uri != null) {
                        coverImage.setImageURI(uri);
                        coverImage.setTag(uri); // Lưu tạm uri để upload khi lưu
                    }
                }
            });

        tvAvatarHint.setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Chọn ảnh đại diện")
                .setItems(new CharSequence[]{"Chụp ảnh", "Chọn từ thư viện"}, (dialog, which) -> {
                    android.content.Intent intent;
                    if (which == 0) {
                        intent = new android.content.Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    } else {
                        intent = new android.content.Intent(android.content.Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    }
                    avatarImagePicker.launch(intent);
                }).show();
        });
        tvCoverHint.setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Chọn ảnh bìa")
                .setItems(new CharSequence[]{"Chụp ảnh", "Chọn từ thư viện"}, (dialog, which) -> {
                    android.content.Intent intent;
                    if (which == 0) {
                        intent = new android.content.Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    } else {
                        intent = new android.content.Intent(android.content.Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    }
                    coverImagePicker.launch(intent);
                }).show();
        });

        // Khi ấn nút Lưu
        btnSave.setOnClickListener(v -> {
            String newUsername = etUsername.getText().toString().trim();
            String newPhone = etPhone.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();
            String newStoreName = etStoreName.getText().toString().trim();
            String newIntroduce = etIntroduce.getText().toString().trim();
            java.util.concurrent.atomic.AtomicReference<String> avatarUrlRef = new java.util.concurrent.atomic.AtomicReference<>(null);
            java.util.concurrent.atomic.AtomicReference<String> coverUrlRef = new java.util.concurrent.atomic.AtomicReference<>(null);
            java.util.List<java.util.concurrent.Callable<Void>> uploadTasks = new java.util.ArrayList<>();
            android.net.Uri avatarUri = (android.net.Uri) avatarImage.getTag();
            android.net.Uri coverUri = (android.net.Uri) coverImage.getTag();
            if (avatarUri != null) {
                uploadTasks.add(() -> {
                    if (oldAvatarUrl[0] != null && !oldAvatarUrl[0].isEmpty()) {
                        deleteCloudinaryImage(oldAvatarUrl[0]);
                    }
                    String url = uploadImageToCloudinary(avatarUri, "avatar");
                    avatarUrlRef.set(url);
                    return null;
                });
            }
            if (coverUri != null) {
                uploadTasks.add(() -> {
                    if (oldCoverUrl[0] != null && !oldCoverUrl[0].isEmpty()) {
                        deleteCloudinaryImage(oldCoverUrl[0]);
                    }
                    String url = uploadImageToCloudinary(coverUri, "background");
                    coverUrlRef.set(url);
                    return null;
                });
            }
            if (!uploadTasks.isEmpty()) {
                new Thread(() -> {
                    try {
                        for (java.util.concurrent.Callable<Void> task : uploadTasks) task.call();
                        runOnUiThread(() -> updateUserInfo(db, email, newUsername, newPhone, newEmail, newStoreName, newIntroduce, avatarUrlRef.get(), coverUrlRef.get(), tvUsername, tvPhone, tvEmail, tvStoreName, tvIntroduce, tvAvatarHint, tvCoverHint, etUsername, etPhone, etEmail, etStoreName, etIntroduce, btnSave));
                    } catch (Exception e) {
                        runOnUiThread(() -> android.widget.Toast.makeText(this, "Lỗi upload ảnh", android.widget.Toast.LENGTH_SHORT).show());
                    }
                }).start();
            } else {
                updateUserInfo(db, email, newUsername, newPhone, newEmail, newStoreName, newIntroduce, null, null, tvUsername, tvPhone, tvEmail, tvStoreName, tvIntroduce, tvAvatarHint, tvCoverHint, etUsername, etPhone, etEmail, etStoreName, etIntroduce, btnSave);
            }
        });
    }

    // Hàm upload ảnh lên Cloudinary, trả về url (sử dụng CloudinaryUploader)
    private String uploadImageToCloudinary(android.net.Uri uri, String type) {
        final String[] resultUrl = {null};
        try {
            android.content.ContentResolver resolver = getContentResolver();
            String fileName = type + System.currentTimeMillis() + ".jpg";
            java.io.InputStream inputStream = resolver.openInputStream(uri);
            if (inputStream != null) {
                final Object lock = new Object();
                CloudinaryUploader.uploadImage(inputStream, fileName, new CloudinaryUploader.UploadCallback() {
                    @Override
                    public void onSuccess(String imageUrl) {
                        resultUrl[0] = imageUrl;
                        synchronized (lock) { lock.notify(); }
                    }
                    @Override
                    public void onFailure(Exception e) {
                        resultUrl[0] = null;
                        synchronized (lock) { lock.notify(); }
                    }
                });
                synchronized (lock) { lock.wait(); }
            }
        } catch (Exception e) {
            android.util.Log.e("ArtisanInfo", "Error uploading image to Cloudinary", e);
        }
        return resultUrl[0];
    }

    // Hàm update Firestore
    private void updateUserInfo(com.google.firebase.firestore.FirebaseFirestore db, String email, String newUsername, String newPhone, String newEmail, String newStoreName, String newIntroduce, String avatarUrl, String coverUrl,
                                android.widget.TextView tvUsername, android.widget.TextView tvPhone, android.widget.TextView tvEmail, android.widget.TextView tvStoreName, android.widget.TextView tvIntroduce,
                                android.widget.TextView tvAvatarHint, android.widget.TextView tvCoverHint,
                                android.widget.EditText etUsername, android.widget.EditText etPhone, android.widget.EditText etEmail, android.widget.EditText etStoreName, android.widget.EditText etIntroduce,
                                android.widget.Button btnSave) {
        db.collection("users")
            .whereEqualTo("Email", email)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    com.google.firebase.firestore.DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                    java.util.Map<String, Object> updates = new java.util.HashMap<>();
                    updates.put("Username", newUsername);
                    updates.put("Phone", newPhone);
                    updates.put("Email", newEmail);
                    updates.put("StoreOrBrand", newStoreName);
                    updates.put("Introduce", newIntroduce);
                    if (avatarUrl != null) updates.put("Avatar", avatarUrl);
                    if (coverUrl != null) updates.put("Background", coverUrl);
                    userDoc.getReference().update(updates).addOnSuccessListener(unused -> {
                        tvUsername.setText(newUsername);
                        tvPhone.setText(newPhone);
                        tvEmail.setText(newEmail);
                        tvStoreName.setText(newStoreName);
                        tvIntroduce.setText(newIntroduce);
                        tvAvatarHint.setVisibility(android.view.View.GONE);
                        tvCoverHint.setVisibility(android.view.View.GONE);
                        tvUsername.setVisibility(android.view.View.VISIBLE);
                        tvPhone.setVisibility(android.view.View.VISIBLE);
                        tvEmail.setVisibility(android.view.View.VISIBLE);
                        tvStoreName.setVisibility(android.view.View.VISIBLE);
                        tvIntroduce.setVisibility(android.view.View.VISIBLE);
                        etUsername.setVisibility(android.view.View.GONE);
                        etPhone.setVisibility(android.view.View.GONE);
                        etEmail.setVisibility(android.view.View.GONE);
                        etStoreName.setVisibility(android.view.View.GONE);
                        etIntroduce.setVisibility(android.view.View.GONE);
                        btnSave.setVisibility(android.view.View.GONE);
                    });
                }
            });
    }

    // --- Cloudinary helper dùng chung ---
    private void deleteCloudinaryImage(String imageUrl) {
        MainBoardArtisan.deleteCloudinaryImage(imageUrl);
    }
}
