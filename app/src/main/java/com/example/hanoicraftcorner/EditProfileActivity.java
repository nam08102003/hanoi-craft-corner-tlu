package com.example.hanoicraftcorner;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
// import android.net.Uri; // Bỏ comment nếu bạn triển khai chọn ảnh
import android.os.Bundle;
// import android.provider.MediaStore; // Bỏ comment nếu bạn triển khai chọn ảnh
import android.util.Log;
import android.widget.Toast;
// import androidx.activity.result.ActivityResultLauncher; // Bỏ comment nếu bạn triển khai chọn ảnh
// import androidx.activity.result.contract.ActivityResultContracts; // Bỏ comment nếu bạn triển khai chọn ảnh
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hanoicraftcorner.databinding.ActivityProfileEditBinding;
import com.example.hanoicraftcorner.model.Profileuser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityProfileEditBinding binding;
    private Profileuser currentUser;
    private FirebaseFirestore db;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final String TAG = "EditProfileActivity";
    // private Uri newImageUri = null; // Bỏ comment nếu bạn triển khai chọn ảnh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imageBackArrow.setOnClickListener(v -> onBackPressed());
        binding.textEditProfileTitle.setText("Chỉnh sửa thông tin");

        db = FirebaseFirestore.getInstance();
        currentUser = (Profileuser) getIntent().getSerializableExtra("USER_DATA");

        if (currentUser != null && currentUser.getId() != null) {
            Log.d(TAG, "Đã nhận currentUser để sửa: " + currentUser.getId() + ", Email: " + currentUser.getEmail());
            populateFieldsWithCurrentData();
            setupEditableFieldClickListeners();
        } else {
            Log.e(TAG, "currentUser là null hoặc không có ID khi khởi tạo EditProfileActivity.");
            Toast.makeText(this, "Lỗi: Không nhận được dữ liệu người dùng hợp lệ để chỉnh sửa.", Toast.LENGTH_LONG).show();
            finish();
        }

        binding.buttonUpdate.setOnClickListener(v -> saveUpdatesToFirestore());
    }

    private void populateFieldsWithCurrentData() {
        if (currentUser == null) {
            Log.e(TAG, "populateFieldsWithCurrentData được gọi nhưng currentUser là null.");
            return;
        }

        if (currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(currentUser.getAvatarUrl())
                    .placeholder(R.drawable.img)
                    .error(R.drawable.ic_placeholder_default)
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "Glide Edit: Lỗi tải ảnh: " + (e != null ? e.getMessage() : "Unknown error"), e);
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            Log.d(TAG, "Glide Edit: Tải ảnh thành công từ " + model);
                            return false;
                        }
                    })
                    .into(binding.imageProfile);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_placeholder_default)
                    .into(binding.imageProfile);
        }


        // Nếu fullName giống username và fullName là null/rỗng, hiển thị EditText rỗng để người dùng nhập mới
        String displayFullName = currentUser.getFullName();
        if (displayFullName != null && displayFullName.equals(currentUser.getUsername()) &&
                (currentUser.getFullName() == null || currentUser.getFullName().trim().isEmpty())) {
            binding.edittextTenDangNhap.setText("");
        } else {
            binding.edittextTenDangNhap.setText(displayFullName);
        }
        binding.edittextTenDangNhap.setEnabled(true);

        binding.edittextEmail.setText(currentUser.getEmail());
        binding.edittextEmail.setEnabled(false);

        binding.edittextSoDienThoai.setText(currentUser.getPhoneNumber());

        if (currentUser.getBirthDate() != null) {
            binding.edittextNgaySinh.setText(dateFormat.format(currentUser.getBirthDate()));
        } else {
            binding.edittextNgaySinh.setText("");
        }

        binding.edittextCccdCmnd.setText(currentUser.getIdCardNumber());
        binding.edittextGioiTinh.setText(currentUser.getGender());
        binding.edittextDiaChi.setText(currentUser.getAddress());
    }

    private void setupEditableFieldClickListeners() {
        binding.textAddPhoto.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng thay đổi ảnh đang được phát triển", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // imagePickerLauncher.launch(intent);
        });

        binding.edittextNgaySinh.setOnClickListener(v -> showDatePickerDialog());
        binding.edittextNgaySinh.setFocusable(false);
        binding.edittextNgaySinh.setFocusableInTouchMode(false);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        if (currentUser.getBirthDate() != null) {
            calendar.setTime(currentUser.getBirthDate());
        } else if (!binding.edittextNgaySinh.getText().toString().isEmpty()) {
            try {
                Date dateFromEditText = dateFormat.parse(binding.edittextNgaySinh.getText().toString());
                if (dateFromEditText != null) {
                    calendar.setTime(dateFromEditText);
                }
            } catch (ParseException e) {
                Log.w(TAG, "Không parse được ngày từ EditText: " + binding.edittextNgaySinh.getText().toString());
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, month, dayOfMonth);
                    binding.edittextNgaySinh.setText(dateFormat.format(newDate.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void saveUpdatesToFirestore() {
        if (currentUser == null || currentUser.getId() == null) {
            Toast.makeText(this, "Lỗi: Không có thông tin người dùng để cập nhật.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullNameInput = binding.edittextTenDangNhap.getText().toString().trim();
        currentUser.setFullName(fullNameInput.isEmpty() ? null : fullNameInput);

        currentUser.setPhoneNumber(binding.edittextSoDienThoai.getText().toString().trim());
        currentUser.setIdCardNumber(binding.edittextCccdCmnd.getText().toString().trim());
        currentUser.setGender(binding.edittextGioiTinh.getText().toString().trim());
        currentUser.setAddress(binding.edittextDiaChi.getText().toString().trim());

        String birthDateString = binding.edittextNgaySinh.getText().toString().trim();
        if (!birthDateString.isEmpty()) {
            try {
                currentUser.setBirthDate(dateFormat.parse(birthDateString));
            } catch (ParseException e) {
                Log.e(TAG, "Lỗi parse ngày sinh khi lưu: " + birthDateString, e);
                Toast.makeText(this, "Định dạng ngày sinh không hợp lệ (dd/MM/yyyy).", Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            currentUser.setBirthDate(null);
        }

        // if (newImageUri != null) { /* Xử lý upload ảnh mới */ } else { performFirestoreUpdate(); }
        performFirestoreUpdate();
    }

    private void performFirestoreUpdate() {
        Map<String, Object> updates = currentUser.toMap();
        updates.put("updatedAt", FieldValue.serverTimestamp()); // Luôn cập nhật thời gian sửa đổi

        Log.d(TAG, "Đang cập nhật Firestore cho ID: " + currentUser.getId() + " với dữ liệu: " + updates);

        db.collection("users").document(currentUser.getId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Cập nhật Firestore thành công.");
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("UPDATED_USER_DATA", currentUser);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi cập nhật Firestore", e);
                    Toast.makeText(this, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /*
    // Bỏ comment và triển khai nếu cần thay đổi ảnh
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
       new ActivityResultContracts.StartActivityForResult(),
       result -> {
           if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
               newImageUri = result.getData().getData();
               Glide.with(this).load(newImageUri).into(binding.imageProfile);
               Log.d(TAG, "Ảnh mới được chọn: " + newImageUri.toString());
           }
       });

    private void uploadImageToFirebaseStorageAndGetUrl(Uri imageUri, com.google.android.gms.tasks.OnSuccessListener<Uri> onSuccessListener) {
        // Implement image upload to Firebase Storage here
        // Get storage reference, putFile, getDownloadUrl
        // Call onSuccessListener.onSuccess(downloadUrl);
        Toast.makeText(this, "Chức năng upload ảnh chưa được triển khai.", Toast.LENGTH_SHORT).show();
    }
    */
}