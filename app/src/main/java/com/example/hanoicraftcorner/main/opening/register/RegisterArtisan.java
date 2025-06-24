package com.example.hanoicraftcorner.main.opening.register;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.provider.MediaStore;
import android.net.Uri;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.adapter.ImageAdapter;
import com.example.hanoicraftcorner.adapter.ImageItem;
import com.example.hanoicraftcorner.main.opening.login.LoginActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.hanoicraftcorner.utils.CloudinaryUploader;

import java.util.Arrays;
import java.util.Objects;

import android.media.MediaScannerConnection;

public class RegisterArtisan extends AppCompatActivity {
    private EditText username, stOrbr, email, phone, password, introduce;
    private TextView imageErrorText;
    private ImageAdapter imageAdapter;
    private final List<ImageItem> imageItems = new ArrayList<>();
    private Uri cameraImageUri;
    private View frameRegister, frameSuccess;
    private static final int REQUEST_CAMERA_PERMISSION = 1001;

    private final ActivityResultLauncher<Intent> imageDocumentPickerLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent data = result.getData();
                int currentCount = imageItems.size();
                int maxSelectable = 3 - currentCount;
                String[] defaultTypes = {"certificate", "cccd_front", "cccd_back"};
                if (data.getClipData() != null) {
                    int count = Math.min(data.getClipData().getItemCount(), maxSelectable);
                    for (int i = 0; i < count && currentCount + i < 3; i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        String type = defaultTypes[currentCount + i];
                        imageItems.add(new ImageItem(uri, type));
                        imageAdapter.notifyItemInserted(imageItems.size() - 1);
                    }
                } else if (data.getData() != null && currentCount < 3) {
                    Uri uri = data.getData();
                    grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    String type = defaultTypes[currentCount];
                    imageItems.add(new ImageItem(uri, type));
                    imageAdapter.notifyItemInserted(imageItems.size() - 1);
                }
            }
        }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && cameraImageUri != null) {
                scanFileToGallery(cameraImageUri);
                String[] defaultTypes = {"certificate", "cccd_front", "cccd_back"};
                int currentCount = imageItems.size();
                if (currentCount < 3) {
                    String type = defaultTypes[currentCount];
                    imageItems.add(new ImageItem(cameraImageUri, type));
                    imageAdapter.notifyItemInserted(imageItems.size() - 1);
                } else {
                    Toast.makeText(this, "Chỉ được chọn tối đa 3 ảnh theo thứ tự: chứng chỉ, CCCD trước, CCCD sau", Toast.LENGTH_SHORT).show();
                }
                cameraImageUri = null;
            }
        }
    );

    @SuppressLint("IntentReset")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_artisan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username = findViewById(R.id.inpUsername);
        stOrbr = findViewById(R.id.inpStoreOrBrand);
        email = findViewById(R.id.inpEmail);
        phone = findViewById(R.id.inpPhone);
        password = findViewById(R.id.inpPassword);
        introduce = findViewById(R.id.inpIntroduce);
        LinearLayout addpic = findViewById(R.id.addPic);
        Button register = findViewById(R.id.registerButtonArtisan);
        TextView clicklogin = findViewById(R.id.ClickableText);
        RecyclerView imageRecyclerView = findViewById(R.id.imageRecyclerView);
        imageErrorText = findViewById(R.id.imageErrorText);
        frameRegister = findViewById(R.id.main);
        frameSuccess = findViewById(R.id.frameSuccess);
        Button btnSuccessOK = findViewById(R.id.btnSuccessOK);

        imageErrorText.setVisibility(View.GONE);
        frameSuccess.setVisibility(View.GONE);

        imageAdapter = new ImageAdapter(this, imageItems, new ImageAdapter.OnImageActionListener() {
            @Override
            public void onImageClick(int position, ImageItem item) {
                String[] labels = {"Chứng chỉ", "CCCD trước", "CCCD sau"};
                String[] types = {"certificate", "cccd_front", "cccd_back"};
                int checked = Arrays.asList(types).indexOf(item.getType());
                new AlertDialog.Builder(RegisterArtisan.this)
                    .setTitle("Chọn loại ảnh cho vị trí này")
                    .setSingleChoiceItems(labels, checked, null)
                    .setPositiveButton("OK", (dialog, which) -> {
                        int selected = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        String newType = types[selected];
                        String oldType = item.getType();
                        if (!newType.equals(oldType)) {
                            int conflictIndex = -1;
                            for (int i = 0; i < imageItems.size(); i++) {
                                if (i != position && imageItems.get(i).getType().equals(newType)) {
                                    conflictIndex = i;
                                    break;
                                }
                            }
                            item.setType(newType);
                            imageAdapter.notifyItemChanged(position);
                            if (conflictIndex != -1) {
                                String unusedType = getUnusedType(types, imageItems);
                                if (unusedType == null) unusedType = oldType;
                                imageItems.get(conflictIndex).setType(unusedType);
                                imageAdapter.notifyItemChanged(conflictIndex);
                            }
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            }
            @Override
            public void onDeleteClick(int position) {
                imageItems.remove(position);
                imageAdapter.notifyItemRemoved(position);
                imageErrorText.setVisibility(View.GONE);
            }
        });
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageRecyclerView.setAdapter(imageAdapter);

        clicklogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        addpic.setOnClickListener(v -> {
            String[] options = {"Chụp ảnh", "Chọn từ thư viện"};
            new AlertDialog.Builder(this)
                .setTitle("Thêm ảnh")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                        } else {
                            cameraImageUri = createImageUri();
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                            cameraLauncher.launch(cameraIntent);
                        }
                    } else {
                        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickIntent.setType("image/*");
                        pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        imageDocumentPickerLauncher.launch(pickIntent);
                    }
                })
                .show();
        });

        register.setOnClickListener(v -> {
            String usernameStr = username.getText().toString().trim();
            String storeOrBrand = stOrbr.getText().toString().trim();
            String emailStr = email.getText().toString().trim();
            String phoneStr = phone.getText().toString().trim();
            String passwordStr = password.getText().toString().trim();
            String introduceStr = introduce.getText().toString().trim();

            // Validate fields
            if (usernameStr.isEmpty()) {
                username.setError("Không được để trống tên người dùng");
                username.requestFocus();
                return;
            }
            if (storeOrBrand.isEmpty()) {
                stOrbr.setError("Không được để trống tên cửa hàng/thương hiệu");
                stOrbr.requestFocus();
                return;
            }
            if (emailStr.isEmpty()) {
                email.setError("Không được để trống email");
                email.requestFocus();
                return;
            }
            if (!emailStr.contains("@") || !emailStr.contains(".")) {
                email.setError("Email không hợp lệ");
                email.requestFocus();
                return;
            }
            if (phoneStr.isEmpty()) {
                phone.setError("Không được để trống số điện thoại");
                phone.requestFocus();
                return;
            }
            if (passwordStr.isEmpty()) {
                password.setError("Không được để trống mật khẩu");
                password.requestFocus();
                return;
            }
            if (passwordStr.length() < 6) {
                password.setError("Mật khẩu phải có ít nhất 6 ký tự");
                password.requestFocus();
                return;
            }
            if (introduceStr.isEmpty()) {
                introduce.setError("Không được để trống giới thiệu");
                introduce.requestFocus();
                return;
            }
            if (imageItems.size() < 3) {
                imageErrorText.setText(R.string.error_image_required);
                imageErrorText.setVisibility(View.VISIBLE);
                return;
            }

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userId = firebaseUser != null ? firebaseUser.getUid() : null;
                        if (userId == null) {
                            Toast.makeText(this, "Lỗi tạo tài khoản!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        // Prepare artisan info according to Artisan.java
                        Map<String, Object> artisan = new HashMap<>();
                        artisan.put("email", emailStr);
                        artisan.put("name", usernameStr);
                        artisan.put("role", "artisan");
                        artisan.put("avatar", ""); // Set default or empty
                        artisan.put("background", ""); // Set default or empty
                        artisan.put("phone", phoneStr);
                        artisan.put("introduce", introduceStr);
                        artisan.put("brand_name", storeOrBrand);
                        artisan.put("status", "Pending");
                        artisan.put("created_at", com.google.firebase.firestore.FieldValue.serverTimestamp());
                        artisan.put("updated_at", com.google.firebase.firestore.FieldValue.serverTimestamp());
                        // Upload images and save artisan info
                        uploadImagesAndSaveInfo(db, artisan, () -> {
                                frameRegister.setVisibility(View.GONE);
                                frameSuccess.setVisibility(View.VISIBLE);
                            },
                            () -> Toast.makeText(this, "Đăng ký thất bại khi upload ảnh/thông tin!", Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        Exception e = task.getException();
                        if (e != null && e.getClass().getSimpleName().equals("FirebaseAuthUserCollisionException")) {
                            email.setError("Email này đã được sử dụng. Vui lòng dùng email khác.");
                            email.requestFocus();
                        } else {
                            Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        });

        btnSuccessOK.setOnClickListener(v -> {
            // Lấy email vừa đăng ký
            String registeredEmail = email.getText().toString();
            Intent intent = new Intent(RegisterArtisan.this, LoginActivity.class);
            intent.putExtra("email", registeredEmail);
            startActivity(intent);
            finish();
        });

        // Handle back button
        findViewById(R.id.imageButton).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private String getUnusedType(String[] types, List<ImageItem> imageItems) {
        for (String type : types) {
            boolean used = false;
            for (ImageItem item : imageItems) {
                if (item.getType().equals(type)) {
                    used = true;
                    break;
                }
            }
            if (!used) return type;
        }
        return null;
    }
    private void uploadImagesAndSaveInfo(FirebaseFirestore db, Map<String, Object> artisan, Runnable onSuccess, Runnable onFailure) {
        new Thread(() -> {
            List<String> imageUrls = new ArrayList<>();
            int total = imageItems.size();
            int[] uploaded = {0};
            boolean[] failed = {false};
            Object lock = new Object();
            for (ImageItem item : imageItems) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(item.getUri());
                    String fileName = "image_" + System.currentTimeMillis() + ".jpg";
                    CloudinaryUploader.uploadImage(inputStream, fileName, new CloudinaryUploader.UploadCallback() {
                        @Override
                        public void onSuccess(String imageUrl) {
                            synchronized (lock) {
                                imageUrls.add(imageUrl);
                                uploaded[0]++;
                                if (uploaded[0] == total && !failed[0]) {
                                    artisan.put("images", imageUrls);
                                    runOnUiThread(() -> saveArtisanInfo(db, artisan, onSuccess, onFailure));
                                }
                            }
                        }
                        @Override
                        public void onFailure(Exception e) {
                            synchronized (lock) {
                                failed[0] = true;
                                runOnUiThread(onFailure);
                            }
                        }
                    });
                } catch (Exception e) {
                    synchronized (lock) {
                        failed[0] = true;
                        runOnUiThread(onFailure);
                    }
                }
            }
        }).start();
    }

    private void saveArtisanInfo(FirebaseFirestore db, Map<String, Object> artisan, Runnable onSuccess, Runnable onFailure) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : null;
        if (userId == null) {
            runOnUiThread(onFailure);
            return;
        }
        db.collection("users").document(userId).set(artisan)
            .addOnSuccessListener(aVoid -> runOnUiThread(onSuccess))
            .addOnFailureListener(e -> runOnUiThread(onFailure));
    }

    private void scanFileToGallery(Uri uri) {
        File file = new File(Objects.requireNonNull(uri.getPath()));
        MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null, null);
    }

    private Uri createImageUri() {
        String imageName = "IMG_" + System.currentTimeMillis() + ".jpg";
        File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageName);
        return androidx.core.content.FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", image);
    }


    private void launchCameraWithPermissionCheck() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                String fileName = "register_camera_" + System.currentTimeMillis() + ".jpg";
                File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
                File photoFile = new File(storageDir, fileName);
                cameraImageUri = androidx.core.content.FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            } catch (Exception e) {
                android.util.Log.e("RegisterArtisan", "Lỗi tạo file ảnh camera", e);
            }
            if (cameraImageUri != null) cameraLauncher.launch(takePictureIntent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCameraWithPermissionCheck();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền truy cập camera để sử dụng chức năng này", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
