package com.example.hanoicraftcorner; // Hoặc package đúng của bạn

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hanoicraftcorner.databinding.ActivityProfileUserBinding;
import com.example.hanoicraftcorner.model.Profileuser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileUserActivity extends AppCompatActivity {

    private ActivityProfileUserBinding binding;
    private Profileuser currentUser;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String TAG = "ProfileUserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get user_id from intent
        String userId = getIntent().getStringExtra("user_id");
        if (userId == null) {
            Toast.makeText(this, "Không tìm thấy user_id!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Use userId for fetching user data
        setupToolbarAndProfileInfo();
        fetchUserData(userId);
        setupOptionsClickListeners();
    }

    private void setupToolbarAndProfileInfo() {
        binding.imageBackArrow.setOnClickListener(v -> onBackPressed());
        binding.textProfileTitle.setText("Profile");
        binding.textUserNameTag.setText("Đang tải...");
    }

    private void fetchUserData(String userId) {
        // Fetch user data using userId
        DocumentReference userRef = db.collection("users").document(userId);
        Log.d(TAG, "Đang lấy dữ liệu cho người dùng ID: " + userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Log.d(TAG, "Tìm thấy document: " + document.getId());
                    // In ra dữ liệu thô từ document để debug
                    Log.d(TAG, "Dữ liệu document: " + document.getData());

                    currentUser = document.toObject(Profileuser.class);
                    if (currentUser != null) {
                        currentUser.setId(document.getId()); // Rất quan trọng
                        Log.d(TAG, "Đã chuyển đổi thành công đối tượng Profileuser.");
                        Log.d(TAG, "Email: " + currentUser.getEmail());
                        Log.d(TAG, "Username: " + currentUser.getUsername());
                        Log.d(TAG, "Avatar URL: " + currentUser.getAvatarUrl());
                        Log.d(TAG, "FullName (từ model): " + currentUser.getFullName());
                        updateUIWithUserData();
                    } else {
                        Log.e(TAG, "currentUser là null sau khi gọi document.toObject(Profileuser.class). " +
                                "Kiểm tra lại Model Profileuser.java và tên trường trên Firestore.");
                        Log.e(TAG, "Các trường trên Firestore: " + document.getData().keySet());
                        binding.textUserNameTag.setText("Lỗi dữ liệu model");
                    }
                } else {
                    Log.w(TAG, "Không tìm thấy document cho ID: " + userId);
                    Toast.makeText(this, "Không tìm thấy người dùng (ID: " + userId + ")", Toast.LENGTH_SHORT).show();
                    binding.textUserNameTag.setText("Không tìm thấy");
                }
            } else {
                Log.e(TAG, "Lỗi khi lấy dữ liệu người dùng: ", task.getException());
                Toast.makeText(this, "Không thể tải dữ liệu người dùng.", Toast.LENGTH_SHORT).show();
                binding.textUserNameTag.setText("Lỗi tải dữ liệu");
            }
        });
    }

    private void updateUIWithUserData() {
        if (currentUser == null) {
            Log.e(TAG, "updateUIWithUserData được gọi nhưng currentUser là null.");
            return;
        }

        binding.textUserNameTag.setText(currentUser.getFullName()); // Sử dụng getFullName() đã có logic fallback

        Log.d(TAG, "Đang tải ảnh đại diện từ URL: " + currentUser.getAvatarUrl());
        if (currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(currentUser.getAvatarUrl())
                    .placeholder(R.drawable.img) // Thay bằng placeholder của bạn
                    .error(R.drawable.ic_placeholder_default) // Thay bằng error placeholder của bạn
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "Glide: Lỗi tải ảnh: " + (e != null ? e.getMessage() : "Unknown error"), e);
                            return false; // Quan trọng: trả về false để Glide hiển thị error drawable
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            Log.d(TAG, "Glide: Tải ảnh thành công từ " + model);
                            return false;
                        }
                    })
                    .into(binding.imageProfile);
        } else {
            Log.w(TAG, "Avatar URL là null hoặc rỗng. Đang hiển thị placeholder/error mặc định.");
            Glide.with(this)
                    .load(R.drawable.ic_placeholder_default) // Hoặc placeholder của bạn nếu avatarUrl null
                    .into(binding.imageProfile);
        }
    }

    // ... (setupOptionsClickListeners và navigateToDetailScreen không thay đổi nhiều)
    private void setupOptionsClickListeners() {
        binding.itemThongTinCaNhan.setOnClickListener(v -> navigateToDetailScreen());
        binding.itemTinNhan.setOnClickListener(v -> Toast.makeText(this, "Tin nhắn clicked", Toast.LENGTH_SHORT).show());
        binding.itemYeuThich.setOnClickListener(v -> Toast.makeText(this, "Yêu thích clicked", Toast.LENGTH_SHORT).show());
        binding.itemThongTinUuDai.setOnClickListener(v -> Toast.makeText(this, "Thông tin ưu đãi clicked", Toast.LENGTH_SHORT).show());
        binding.itemChiaSePhanHoi.setOnClickListener(v -> Toast.makeText(this, "Chia sẻ phản hồi clicked", Toast.LENGTH_SHORT).show());
        binding.itemBaoHanh.setOnClickListener(v -> Toast.makeText(this, "Bảo hành clicked", Toast.LENGTH_SHORT).show());
    }

    private void navigateToDetailScreen() {
        if (currentUser != null) {
            Intent intent = new Intent(this, ProfileDetailActivity.class);
            intent.putExtra("USER_DATA", currentUser);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Dữ liệu người dùng chưa sẵn sàng, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            // Có thể fetch lại dữ liệu nếu cần
            // fetchUserData();
        }
    }
}