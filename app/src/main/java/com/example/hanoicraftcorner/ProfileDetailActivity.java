package com.example.hanoicraftcorner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hanoicraftcorner.databinding.ActivityProfileDetailsBinding;
import com.example.hanoicraftcorner.model.Profileuser;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ProfileDetailActivity extends AppCompatActivity {

    private ActivityProfileDetailsBinding binding;
    private Profileuser currentUser;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private ActivityResultLauncher<Intent> editProfileLauncher;
    private static final String TAG = "ProfileDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imageBackArrow.setOnClickListener(v -> onBackPressed());
        binding.textProfileTitle.setText("Thông tin cá nhân");

        currentUser = (Profileuser) getIntent().getSerializableExtra("USER_DATA");

        registerEditLauncher();

        if (currentUser != null) {
            Log.d(TAG, "Đã nhận currentUser: " + currentUser.getId() + ", Email: " + currentUser.getEmail());
            populateDetails();
        } else {
            Log.e(TAG, "currentUser là null khi khởi tạo ProfileDetailActivity.");
            Toast.makeText(this, "Lỗi: Không nhận được dữ liệu người dùng.", Toast.LENGTH_LONG).show();
            finish();
        }

        binding.buttonEditProfile.setOnClickListener(v -> {
            if (currentUser != null) {
                Intent intent = new Intent(this, EditProfileActivity.class);
                intent.putExtra("USER_DATA", currentUser);
                editProfileLauncher.launch(intent);
            } else {
                Toast.makeText(this, "Dữ liệu người dùng không có sẵn để chỉnh sửa.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateDetails() {
        if (currentUser == null) {
            Log.e(TAG, "populateDetails được gọi nhưng currentUser là null.");
            return;
        }

        binding.textUserNameBelowProfile.setText(currentUser.getFullName());

        if (currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(currentUser.getAvatarUrl())
                    .placeholder(R.drawable.img)
                    .error(R.drawable.ic_placeholder_default)
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "Glide Detail: Lỗi tải ảnh: " + (e != null ? e.getMessage() : "Unknown error"), e);
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            Log.d(TAG, "Glide Detail: Tải ảnh thành công từ " + model);
                            return false;
                        }
                    })
                    .into(binding.imageProfile);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_placeholder_default)
                    .into(binding.imageProfile);
        }


        binding.textviewTenDangNhap.setText(currentUser.getFullName());
        binding.textviewEmail.setText(currentUser.getEmail());
        binding.textviewSoDienThoai.setText(formatDisplayValue(currentUser.getPhoneNumber()));

        String birthDateStr = "Chưa có";
        if (currentUser.getBirthDate() != null) {
            try {
                birthDateStr = dateFormat.format(currentUser.getBirthDate());
            } catch (Exception e) {
                Log.e(TAG, "Lỗi định dạng ngày sinh: " + currentUser.getBirthDate(), e);
            }
        }
        binding.textviewNgaySinh.setText(birthDateStr);
        binding.textviewCccdCmnd.setText(formatDisplayValue(currentUser.getIdCardNumber()));
        binding.textviewGioiTinh.setText(formatDisplayValue(currentUser.getGender()));
        binding.textviewDiaChi.setText(formatDisplayValue(currentUser.getAddress()));
    }

    private String formatDisplayValue(String value) {
        return (value != null && !value.trim().isEmpty()) ? value : "Chưa có";
    }

    private void registerEditLauncher() {
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("UPDATED_USER_DATA")) {
                            currentUser = (Profileuser) data.getSerializableExtra("UPDATED_USER_DATA");
                            if (currentUser != null) {
                                Log.d(TAG, "Đã nhận dữ liệu cập nhật từ EditProfileActivity. Đang cập nhật UI.");
                                populateDetails();
                                Toast.makeText(this, "Thông tin đã được cập nhật!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}