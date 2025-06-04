package register_artisan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.provider.MediaStore;
import android.net.Uri;
import android.content.ActivityNotFoundException;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.animation.ValueAnimator;
import android.view.ViewGroup;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoicraftcorner.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

import register.ImageAdapter;

public class Register_Artisan extends AppCompatActivity {
    EditText username, stOrbr, email, phone, password, introduce;
    LinearLayout addpic;
    Button register;
    TextView clicklogin;
    private RecyclerView imageRecyclerView;
    private ImageAdapter imageAdapter;
    private final List<Uri> imageUris = new ArrayList<>();

    private static final int REQUEST_CAMERA_PERMISSION = 2001;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        imageUris.add(selectedImage);
                        imageAdapter.notifyItemInserted(imageUris.size() - 1);
                        updateRecyclerViewVisibility();
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
                                imageUris.add(tempUri);
                                imageAdapter.notifyItemInserted(imageUris.size() - 1);
                                updateRecyclerViewVisibility();
                            }
                        }
                    } else if (imageUri != null) {
                        imageUris.add(imageUri);
                        imageAdapter.notifyItemInserted(imageUris.size() - 1);
                        updateRecyclerViewVisibility();
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
        imageAdapter = new ImageAdapter(this, imageUris, new ImageAdapter.OnImageActionListener() {
            @Override
            public void onImageClick(Uri uri) {
                // Xem ảnh full screen (dùng intent hoặc dialog)
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "image/*");
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position) {
                imageUris.remove(position);
                imageAdapter.notifyItemRemoved(position);
                updateRecyclerViewVisibility();
            }
        });
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageRecyclerView.setAdapter(imageAdapter);
        updateRecyclerViewVisibility();

        clicklogin.setOnClickListener(v -> {
//            Intent intent = new Intent(this, "Thay vào đây".class);
//            startActivity(intent);
        });

        addpic.setOnClickListener(v -> {
            String[] options = {"Chọn ảnh từ thiết bị", "Chụp ảnh bằng camera"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Thêm ảnh")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            // Chọn ảnh từ thiết bị
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            imagePickerLauncher.launch(intent);
                        } else if (which == 1) {
                            // Chụp ảnh bằng camera
                            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                            } else {
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                try {
                                    cameraLauncher.launch(takePictureIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(this, "Không tìm thấy ứng dụng camera", Toast.LENGTH_SHORT).show();
                                }
                            }
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
                                if (!imageUris.isEmpty()) {
                                    uploadImagesAndSaveInfo(userId, storeOrBrand, phoneStr, introduceStr, db);
                                } else {
                                    saveArtisanInfoWithImagesSubcollection(db.collection("Users").document(userId), storeOrBrand, phoneStr, introduceStr, new ArrayList<>());
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show());
                    } else {
                        String msg = task.getException() != null ? task.getException().getMessage() : "";
                        if (msg != null && msg.contains("email address is already in use")) email.setError("Email đã được sử dụng");
                        else Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
        });
    }

    private void uploadImagesAndSaveInfo(String userId, String storeOrBrand, String phoneStr, String introduceStr, FirebaseFirestore db) {
        List<String> base64Images = new ArrayList<>();
        // Chỉ upload những ảnh đang có trong imageUris (hiển thị trên RecyclerView)
        if (imageUris.isEmpty()) {
            saveArtisanInfoWithImagesSubcollection(db.collection("Users").document(userId), storeOrBrand, phoneStr, introduceStr, base64Images);
            return;
        }
        for (Uri uri : imageUris) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                // Resize ảnh về max 800px và nén quality 70%
                Bitmap resized = resizeBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resized.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imageBytes = baos.toByteArray();
                String base64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                base64Images.add(base64);
            } catch (Exception e) {
                // Use robust logging instead of printStackTrace
                android.util.Log.e("Register_Artisan", "Error processing image", e);
            }
        }
        saveArtisanInfoWithImagesSubcollection(db.collection("Users").document(userId), storeOrBrand, phoneStr, introduceStr, base64Images);
    }

    // Resize bitmap về maxSize px (giữ tỉ lệ)
    private Bitmap resizeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float ratio = (float) width / height;
        if (ratio > 1) {
            width = 800;
            height = (int) (width / ratio);
        } else {
            height = 800;
            width = (int) (height * ratio);
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    private void saveArtisanInfoWithImagesSubcollection(com.google.firebase.firestore.DocumentReference documentReference, String storeOrBrand, String phoneStr, String introduceStr, List<String> base64Images) {
        Map<String, Object> artisanInfo = new HashMap<>();
        artisanInfo.put("storeOrBrand", storeOrBrand);
        artisanInfo.put("phone", phoneStr);
        artisanInfo.put("introduce", introduceStr);
        artisanInfo.put("verified", "pending");
        documentReference.collection("artisanInfo")
            .add(artisanInfo)
            .addOnSuccessListener(subDocRef -> {
                // Lưu từng ảnh vào subcollection Image (dạng base64 string)
                if (!base64Images.isEmpty()) {
                    for (String base64 : base64Images) {
                        Map<String, Object> img = new HashMap<>();
                        img.put("base64", base64);
                        subDocRef.collection("Image").add(img);
                    }
                }
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show());
    }

    private void animateRecyclerViewHeight(int targetHeight) {
        if (imageRecyclerView == null) return;
        int currentHeight = imageRecyclerView.getLayoutParams().height;
        if (currentHeight == targetHeight) return;
        ValueAnimator animator = ValueAnimator.ofInt(currentHeight, targetHeight);
        animator.setDuration(300); // 300ms for smooth animation
        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = imageRecyclerView.getLayoutParams();
            params.height = value;
            imageRecyclerView.setLayoutParams(params);
        });
        animator.start();
    }

    private void updateRecyclerViewVisibility() {
        if (imageRecyclerView != null) {
            if (imageUris.isEmpty()) {
                animateRecyclerViewHeight(0);
            } else {
                float scale = getResources().getDisplayMetrics().density;
                int heightPx = (int) (80 * scale + 0.5f); // 80dp to px
                animateRecyclerViewHeight(heightPx);
            }
        }
    }

    private Uri getImageUriFromBitmap(android.graphics.Bitmap bitmap) {
        java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = android.provider.MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "IMG_" + System.currentTimeMillis(), null);
        return path != null ? Uri.parse(path) : null;
    }
}
