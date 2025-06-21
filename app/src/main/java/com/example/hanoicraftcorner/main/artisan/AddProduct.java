package com.example.hanoicraftcorner.main.artisan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hanoicraftcorner.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class AddProduct extends AppCompatActivity {
    private ImageView productImageView;
    private Uri imageUri;
    private Uri cameraImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private FirebaseFirestore db;
    private String userEmail;
    private String editingProductId = null;
    private String editingImageUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.imageButton).setOnClickListener(v -> finish());
        productImageView = findViewById(R.id.productImageView);

        android.widget.TextView addPicText = findViewById(R.id.textView2);
        updateAddPicButtonText(addPicText, imageUri);
        findViewById(R.id.addPic).setOnClickListener(v -> new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(imageUri == null ? "Thêm ảnh sản phẩm" : "Sửa ảnh sản phẩm")
            .setItems(new CharSequence[]{"Chụp ảnh", "Chọn từ thư viện"}, (dialog, which) -> {
                if (which == 0) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                    } else {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraImageUri = createImageUri();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                        imagePickerLauncher.launch(takePictureIntent);
                    }
                } else if (which == 1) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    imagePickerLauncher.launch(pickPhoto);
                }
            })
            .show());
        // Update button text after picking image
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (cameraImageUri != null) {
                        scanFileToGallery(cameraImageUri);
                        imageUri = cameraImageUri;
                        productImageView.setImageURI(imageUri);
                        cameraImageUri = null;
                    } else if (data != null && data.getData() != null) {
                        imageUri = data.getData();
                        productImageView.setImageURI(imageUri);
                    }
                    updateAddPicButtonText(addPicText, imageUri);
                }
            }
        );

        db = FirebaseFirestore.getInstance();
        userEmail = getIntent().getStringExtra("email");
        // Check if editing
        editingProductId = getIntent().getStringExtra("productId");
        if (editingProductId != null) {
            // Hide title
            findViewById(R.id.text_add_product_title).setVisibility(android.view.View.GONE);
            // Change button text to "Lưu"
            ((android.widget.Button)findViewById(R.id.addProductButton)).setText("Lưu");
            // Load data into fields
            ((android.widget.EditText)findViewById(R.id.editTextProductName)).setText(getIntent().getStringExtra("productName"));
            ((android.widget.EditText)findViewById(R.id.editTextDescription)).setText(getIntent().getStringExtra("description"));
            ((android.widget.EditText)findViewById(R.id.editTextPrice)).setText(getIntent().getStringExtra("price"));
            ((android.widget.EditText)findViewById(R.id.editTextCategory)).setText(getIntent().getStringExtra("category"));
            ((android.widget.EditText)findViewById(R.id.editTextQuantity)).setText(getIntent().getStringExtra("quantity"));
            editingImageUrl = getIntent().getStringExtra("imageUrl");
            if (editingImageUrl != null && !editingImageUrl.isEmpty()) {
                com.bumptech.glide.Glide.with(this).load(editingImageUrl).into(productImageView);
            }
        }

        findViewById(R.id.addProductButton).setOnClickListener(v -> {
            if (userEmail == null || userEmail.isEmpty()) {
                android.util.Log.e("AddProduct", "Không tìm thấy email người dùng!");
                return;
            }
            EditText productNameEt = findViewById(R.id.editTextProductName);
            EditText descriptionEt = findViewById(R.id.editTextDescription);
            EditText priceEt = findViewById(R.id.editTextPrice);
            EditText categoryEt = findViewById(R.id.editTextCategory);
            EditText quantityEt = findViewById(R.id.editTextQuantity);

            String productName = productNameEt.getText().toString().trim();
            String description = descriptionEt.getText().toString().trim();
            String price = priceEt.getText().toString().trim();
            String category = categoryEt.getText().toString().trim();
            String quantity = quantityEt.getText().toString().trim();

            if (!validateFields(productNameEt, descriptionEt, priceEt, categoryEt, quantityEt,
                    productName, description, price, category, quantity)) {
                return;
            }

            // Bỏ thêm .000 khi lưu giá
            if (editingProductId != null) {
                // EDIT MODE: update Firestore document
                updateProduct(editingProductId, productName, description, price, category, quantity, imageUri, editingImageUrl);
            } else {
                // ADD MODE: upload image and add new product
                if (imageUri != null) {
                    try {
                        java.io.InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        String fileName = "product_" + System.currentTimeMillis() + ".jpg";
                        com.example.hanoicraftcorner.utils.CloudinaryUploader.uploadImage(inputStream, fileName, new com.example.hanoicraftcorner.utils.CloudinaryUploader.UploadCallback() {
                            @Override
                            public void onSuccess(String imageUrl) {
                                android.util.Log.d("AddProduct", "Cloudinary upload success, imageUrl=" + imageUrl);
                                saveProduct(productName, description, price, category, quantity, imageUrl);
                            }
                            @Override
                            public void onFailure(Exception e) {
                                android.util.Log.e("AddProduct", "Lỗi upload ảnh Cloudinary", e);
                            }
                        });
                    } catch (Exception e) {
                        android.util.Log.e("AddProduct", "Lỗi khi mở InputStream cho ảnh", e);
                    }
                } else {
                    saveProduct(productName, description, price, category, quantity, null);
                }
            }
        });
    }

    private boolean validateFields(EditText productNameEt, EditText descriptionEt, EditText priceEt,
                                   EditText categoryEt, EditText quantityEt,
                                   String productName, String description, String price, String category, String quantity) {
        if (productName.isEmpty()) {
            productNameEt.setError("Vui lòng nhập tên sản phẩm");
            productNameEt.requestFocus();
            return false;
        }
        if (description.isEmpty()) {
            descriptionEt.setError("Vui lòng nhập mô tả");
            descriptionEt.requestFocus();
            return false;
        }
        if (price.isEmpty()) {
            priceEt.setError("Vui lòng nhập giá bán");
            priceEt.requestFocus();
            return false;
        }
        if (category.isEmpty()) {
            categoryEt.setError("Vui lòng nhập danh mục");
            categoryEt.requestFocus();
            return false;
        }
        if (quantity.isEmpty()) {
            quantityEt.setError("Vui lòng nhập số lượng");
            quantityEt.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                java.io.File photoFile;
                try {
                    String fileName = "product_camera_" + System.currentTimeMillis() + ".jpg";
                    java.io.File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
                    photoFile = new java.io.File(storageDir, fileName);
                    cameraImageUri = androidx.core.content.FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                } catch (Exception e) {
                    android.util.Log.e("AddProduct", "Lỗi tạo file ảnh camera", e);
                }
                if (cameraImageUri != null) {
                    imagePickerLauncher.launch(takePictureIntent);
                }
            } else {
                android.util.Log.e("AddProduct", "Bạn cần cấp quyền camera để chụp ảnh");
            }
        }
    }

    // Save product to Firestore
    private void saveProduct(String productName, String description, String price, String category, String quantity, String imageUrl) {
        db.collection("users").whereEqualTo("Email", userEmail).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                DocumentReference userDoc = queryDocumentSnapshots.getDocuments().get(0).getReference();
                java.util.Map<String, Object> productInfo = new java.util.HashMap<>();
                productInfo.put("Name", productName);
                productInfo.put("Description", description);
                productInfo.put("Price", price);
                productInfo.put("Category", category);
                productInfo.put("Quantity", quantity);
                // Nếu imageUri khác null nhưng imageUrl null/rỗng thì KHÔNG thêm sản phẩm
                if (imageUri != null && (imageUrl == null || imageUrl.isEmpty())) {
                    android.util.Log.e("AddProduct", "Không lấy được URL ảnh từ Cloudinary! imageUrl=null, imageUri=" + imageUri);
                    return;
                }
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    productInfo.put("Image", imageUrl);
                }
                userDoc.collection("products")
                    .add(productInfo)
                    .addOnSuccessListener(documentReference -> {
                        Intent intent = new Intent(this, com.example.hanoicraftcorner.main.artisan.MainBoardArtisan.class);
                        intent.putExtra("email", userEmail);
                        setResult(RESULT_OK, intent);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> android.util.Log.e("AddProduct", "Lỗi khi thêm sản phẩm vào subcollection products!", e));
            } else {
                android.util.Log.e("AddProduct", "Không tìm thấy người dùng!");
            }
        }).addOnFailureListener(e -> android.util.Log.e("AddProduct", "Lỗi Firestore khi tìm user!", e));
    }

    // --- Cloudinary helper dùng chung ---
    private void deleteCloudinaryImage(String imageUrl) {
        MainBoardArtisan.deleteCloudinaryImage(imageUrl);
    }

    // Thêm hàm updateProduct
    private void updateProduct(String productId, String productName, String description, String price, String category, String quantity, Uri newImageUri, String oldImageUrl) {
        db.collection("users").whereEqualTo("Email", userEmail).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                DocumentReference userDoc = queryDocumentSnapshots.getDocuments().get(0).getReference();
                DocumentReference productRef = userDoc.collection("products").document(productId);
                java.util.Map<String, Object> productInfo = new java.util.HashMap<>();
                productInfo.put("Name", productName);
                productInfo.put("Description", description);
                productInfo.put("Price", price);
                productInfo.put("Category", category);
                productInfo.put("Quantity", quantity);
                if (newImageUri != null) {
                    // Nếu có ảnh cũ thì xóa trước khi upload ảnh mới
                    if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                        deleteCloudinaryImage(oldImageUrl);
                        uploadAndUpdate(productRef, productInfo, newImageUri);
                    } else {
                        uploadAndUpdate(productRef, productInfo, newImageUri);
                    }
                } else if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                    productInfo.put("Image", oldImageUrl);
                    productRef.update(productInfo)
                        .addOnSuccessListener(unused -> finish())
                        .addOnFailureListener(e -> android.util.Log.e("AddProduct", "Lỗi cập nhật sản phẩm!", e));
                } else {
                    productRef.update(productInfo)
                        .addOnSuccessListener(unused -> finish())
                        .addOnFailureListener(e -> android.util.Log.e("AddProduct", "Lỗi cập nhật sản phẩm!", e));
                }
            }
        }).addOnFailureListener(e -> android.util.Log.e("AddProduct", "Lỗi Firestore khi tìm user!", e));
    }

    private void uploadAndUpdate(DocumentReference productRef, java.util.Map<String, Object> productInfo, Uri newImageUri) {
        try {
            java.io.InputStream inputStream = getContentResolver().openInputStream(newImageUri);
            String fileName = "product_" + System.currentTimeMillis() + ".jpg";
            com.example.hanoicraftcorner.utils.CloudinaryUploader.uploadImage(inputStream, fileName, new com.example.hanoicraftcorner.utils.CloudinaryUploader.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    productInfo.put("Image", imageUrl);
                    productRef.update(productInfo)
                        .addOnSuccessListener(unused -> finish())
                        .addOnFailureListener(e -> android.util.Log.e("AddProduct", "Lỗi cập nhật sản phẩm!", e));
                }
                @Override
                public void onFailure(Exception e) {
                    android.util.Log.e("AddProduct", "Lỗi upload ảnh Cloudinary khi sửa", e);
                }
            });
        } catch (Exception e) {
            android.util.Log.e("AddProduct", "Lỗi khi mở InputStream cho ảnh sửa", e);
        }
    }

    // Helper to update addPic button text
    @SuppressLint("SetTextI18n")
    private void updateAddPicButtonText(android.widget.TextView addPicText, Uri imageUri) {
        if (imageUri == null) {
            addPicText.setText("Thêm ảnh");
        } else {
            addPicText.setText("Sửa ảnh");
        }
    }

    private Uri createImageUri() {
        String imageName = "IMG_" + System.currentTimeMillis() + ".jpg";
        java.io.File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        java.io.File image = new java.io.File(storageDir, imageName);
        return androidx.core.content.FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", image);
    }

    private void scanFileToGallery(Uri uri) {
        java.io.File file = new java.io.File(Objects.requireNonNull(uri.getPath()));
        android.media.MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null, null);
    }
}
