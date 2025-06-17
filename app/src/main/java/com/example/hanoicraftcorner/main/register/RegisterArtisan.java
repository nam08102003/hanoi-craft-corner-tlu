package com.example.hanoicraftcorner.main.register;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.adapter.ImageAdapter;
import com.example.hanoicraftcorner.adapter.ImageItem;
import com.example.hanoicraftcorner.main.login.LoginActivity;
import com.example.hanoicraftcorner.main.MainBoardArtisan;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

public class RegisterArtisan extends AppCompatActivity {
    EditText username, stOrbr, email, phone, password, introduce;
    LinearLayout addpic;
    Button register;
    TextView clicklogin, imageErrorText;
    private RecyclerView imageRecyclerView;
    private ImageAdapter imageAdapter;
    private final List<ImageItem> imageItems = new ArrayList<>();

    private static final int REQUEST_CAMERA_PERMISSION = 2001;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    int currentCount = imageItems.size();
                    int maxSelectable = 3 - currentCount;
                    if (maxSelectable <= 0) {
                        Toast.makeText(this, "Chỉ được chọn tối đa 3 ảnh theo thứ tự: chứng chỉ, CCCD trước, CCCD sau", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String[] defaultTypes = {"certificate", "cccd_front", "cccd_back"};
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        if (count > maxSelectable) {
                            Toast.makeText(this, "Bạn chỉ có thể chọn thêm " + maxSelectable + " ảnh", Toast.LENGTH_SHORT).show();
                            count = maxSelectable;
                        }
                        for (int i = 0; i < count; i++) {
                            Uri selectedImage = data.getClipData().getItemAt(i).getUri();
                            if (selectedImage != null) {
                                String type = defaultTypes[currentCount + i];
                                imageItems.add(new ImageItem(selectedImage, type));
                                imageAdapter.notifyItemInserted(imageItems.size() - 1);
                            }
                        }
                    } else if (data.getData() != null) {
                        if (maxSelectable > 0) {
                            Uri selectedImage = data.getData();
                            if (selectedImage != null) {
                                String type = defaultTypes[currentCount];
                                imageItems.add(new ImageItem(selectedImage, type));
                                imageAdapter.notifyItemInserted(imageItems.size() - 1);
                            }
                        } else {
                            Toast.makeText(this, "Chỉ được chọn tối đa 3 ảnh theo thứ tự: chứng chỉ, CCCD trước, CCCD sau", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri == null && result.getData().getExtras() != null) {
                        android.graphics.Bitmap photo = (android.graphics.Bitmap) result.getData().getExtras().get("data");
                        if (photo != null) {
                            Uri tempUri = getImageUriFromBitmap(photo);
                            if (tempUri != null) {
                                // Thêm ảnh từ camera vào imageItems với nhãn tiếp theo còn thiếu
                                String[] defaultTypes = {"certificate", "cccd_front", "cccd_back"};
                                int currentCount = imageItems.size();
                                if (currentCount < 3) {
                                    String type = defaultTypes[currentCount];
                                    imageItems.add(new ImageItem(tempUri, type));
                                    imageAdapter.notifyItemInserted(imageItems.size() - 1);
                                } else {
                                    Toast.makeText(this, "Chỉ được chọn tối đa 3 ảnh theo thứ tự: chứng chỉ, CCCD trước, CCCD sau", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } else if (imageUri != null) {
                        // Thêm ảnh từ camera vào imageItems với nhãn tiếp theo còn thiếu
                        String[] defaultTypes = {"certificate", "cccd_front", "cccd_back"};
                        int currentCount = imageItems.size();
                        if (currentCount < 3) {
                            String type = defaultTypes[currentCount];
                            imageItems.add(new ImageItem(imageUri, type));
                            imageAdapter.notifyItemInserted(imageItems.size() - 1);
                        } else {
                            Toast.makeText(this, "Chỉ được chọn tối đa 3 ảnh theo thứ tự: chứng chỉ, CCCD trước, CCCD sau", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    // Sử dụng ActivityResultLauncher thay cho startActivityForResult (deprecated)
    private final ActivityResultLauncher<Intent> imageDocumentPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    int currentCount = imageItems.size();
                    int maxSelectable = 3 - currentCount;
                    String[] defaultTypes = {"certificate", "cccd_front", "cccd_back"};
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        if (count > maxSelectable) count = maxSelectable;
                        for (int i = 0; i < count; i++) {
                            if (currentCount + i >= 3) break;
                            Uri uri = data.getClipData().getItemAt(i).getUri();
                            grantUriPermission(uri);
                            String type = defaultTypes[currentCount + i];
                            imageItems.add(new ImageItem(uri, type));
                        }
                        imageAdapter.notifyDataSetChanged();
                    } else if (data.getData() != null) {
                        if (currentCount < 3) {
                            Uri uri = data.getData();
                            grantUriPermission(uri);
                            String type = defaultTypes[currentCount];
                            imageItems.add(new ImageItem(uri, type));
                            imageAdapter.notifyDataSetChanged();
                        }
                    }
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
        addpic = findViewById(R.id.addPic);
        register = findViewById(R.id.registerButtonArtisan);
        clicklogin = findViewById(R.id.ClickableText);
        imageRecyclerView = findViewById(R.id.imageRecyclerView);
        imageErrorText = findViewById(R.id.imageErrorText);

        imageErrorText.setVisibility(View.GONE);

        imageAdapter = new ImageAdapter(this, imageItems, new ImageAdapter.OnImageActionListener() {
            @Override
            public void onImageClick(int position, ImageItem item) {
                String[] labels = {"Chứng chỉ", "CCCD trước", "CCCD sau"};
                String[] types = {"certificate", "cccd_front", "cccd_back"};
                int checked = 0;
                for (int i = 0; i < types.length; i++) {
                    if (types[i].equals(item.getType())) checked = i;
                }
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
                                // Tìm loại còn lại chưa ai chọn
                                String unusedType = getUnusedType(types, imageItems);
                                if (unusedType == null) {
                                    unusedType = oldType;
                                }
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
            imageErrorText.setVisibility(View.GONE);
            openImageDocumentPicker();
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
                imageErrorText.setText("Bạn phải chọn đủ 3 ảnh: chứng chỉ, CCCD trước, CCCD sau");
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
                        Map<String, Object> user = new HashMap<>();
                        user.put("username", usernameStr);
                        user.put("email", emailStr);
                        user.put("profiletype", "Seller");
                        user.put("role", "artisan");
                        db.collection("Users")
                            .document(userId)
                            .set(user)
                            .addOnSuccessListener(aVoid -> {
                                // Chạy upload ảnh trên background thread để tránh block UI
                                new Thread(() -> {
                                    uploadImagesAndSaveInfo(userId, storeOrBrand, phoneStr, introduceStr, db);
                                    runOnUiThread(() -> {
                                        Intent intent = new Intent(RegisterArtisan.this, MainBoardArtisan.class);
                                        intent.putExtra("email", emailStr);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    });
                                }).start();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show());
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

        // Handle back button
        findViewById(R.id.imageButton).setOnClickListener(v -> onBackPressed());
    }

    private String getUnusedType(String[] types, List<ImageItem> imageItems) {
        for (String t : types) {
            boolean used = false;
            for (ImageItem item : imageItems) {
                if (item.getType().equals(t)) {
                    used = true;
                    break;
                }
            }
            if (!used) return t;
        }
        return null;
    }

    // Upload ảnh lên Cloudinary theo thứ tự và lưu vào CertiImage
    private void uploadImagesAndSaveInfo(String userId, String storeOrBrand, String phoneStr, String introduceStr, FirebaseFirestore db) {
        // Sử dụng mảng cố định cho loại ảnh
        String[] types = {"certificate", "cccd_front", "cccd_back"};
        List<ImageItem> sorted = new ArrayList<>(3);
        for (String t : types) {
            for (ImageItem item : imageItems) {
                if (item.getType().equals(t)) {
                    sorted.add(item);
                    break;
                }
            }
        }
        uploadImagesSequentially(userId, storeOrBrand, phoneStr, introduceStr, db, types, sorted);
    }

    // Đệ quy tuần tự upload từng ảnh
    private void uploadImagesSequentially(String userId, String storeOrBrand, String phoneStr, String introduceStr, FirebaseFirestore db, String[] types, List<ImageItem> sortedItems) {
        Map<String, Object> artisanInfo = new HashMap<>();
        artisanInfo.put("storeOrBrand", storeOrBrand);
        artisanInfo.put("phone", phoneStr);
        artisanInfo.put("introduce", introduceStr);
        artisanInfo.put("verified", "pending");
        db.collection("Users").document(userId).collection("artisanInfo")
            .add(artisanInfo)
            .addOnSuccessListener(subDocRef -> uploadImagesSequentiallyInternal(0, subDocRef, types, sortedItems))
            .addOnFailureListener(e -> runOnUiThread(() -> Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show()));
    }

    private void uploadImagesSequentiallyInternal(int index, com.google.firebase.firestore.DocumentReference subDocRef, String[] types, List<ImageItem> sortedItems) {
        if (index >= types.length) {
            runOnUiThread(() -> {
                Intent intent = new Intent(this, MainBoardArtisan.class);
                intent.putExtra("email", email.getText().toString().trim());
                startActivity(intent);
            });
            return;
        }
        ImageItem item = sortedItems.get(index);
        Uri uri = item.getUri();
        String type = item.getType();
        try {
            File tempFile = copyUriToTempFile(uri, type);
            Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
            Bitmap resized = resizeBitmap(bitmap);
            File file = File.createTempFile("upload_resized_" + type, ".jpg", getCacheDir());
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                resized.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            }
            com.example.hanoicraftcorner.utils.CloudinaryUploader.uploadImage(file, new com.example.hanoicraftcorner.utils.CloudinaryUploader.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    if (imageUrl == null || imageUrl.isEmpty()) {
                        runOnUiThread(() -> Toast.makeText(RegisterArtisan.this, "Lỗi upload ảnh: Link ảnh rỗng! Đăng ký thất bại.", Toast.LENGTH_LONG).show());
                        return; // Stop all actions, do not continue registration
                    }
                    Map<String, Object> img = new HashMap<>();
                    img.put("url", imageUrl);
                    img.put("type", type);
                    subDocRef.collection("CertiImage").add(img)
                        .addOnSuccessListener(docRef -> uploadImagesSequentiallyInternal(index + 1, subDocRef, types, sortedItems));
                }
                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> Toast.makeText(RegisterArtisan.this, "Lỗi upload ảnh: " + type, Toast.LENGTH_SHORT).show());
                }
            });
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(this, "Lỗi xử lý ảnh: " + type, Toast.LENGTH_SHORT).show());
        }
    }

    // Copy URI về file tạm trong cache để xử lý an toàn với mọi loại URI (kể cả Google Photos)
    private File copyUriToTempFile(Uri uri, String type) throws Exception {
        java.io.InputStream input = getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("upload_" + type, ".jpg", getCacheDir());
        java.io.OutputStream output = new java.io.FileOutputStream(tempFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        input.close();
        output.close();
        return tempFile;
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "IMG_" + System.currentTimeMillis(), null);
        if (path == null) return null;
        return Uri.parse(path);
    }

    // Resize bitmap về maxSize px (giữ tỉ lệ)
    private Bitmap resizeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth(), height = bitmap.getHeight(), maxSize = 800;
        float ratio = (float) width / height;
        if (ratio > 1) { width = maxSize; height = (int) (width / ratio); }
        else { height = maxSize; width = (int) (height * ratio); }
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    // Sử dụng ACTION_OPEN_DOCUMENT để chọn ảnh và xin quyền truy cập lâu dài
    private void openImageDocumentPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imageDocumentPickerLauncher.launch(intent);
    }

    // Xin quyền truy cập lâu dài cho URI
    private void grantUriPermission(Uri uri) {
        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
        try {
            getContentResolver().takePersistableUriPermission(uri, takeFlags);
        } catch (Exception ignored) {}
    }
}
