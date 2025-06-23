package com.example.hanoicraftcorner.main.artisan;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.hanoicraftcorner.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.View;

public class MainBoardArtisan extends AppCompatActivity {

    private String email;
    private MyProductsAdapter myProductsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        email = getIntent().getStringExtra("email");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_board_artisan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo RecyclerView và Adapter cho sản phẩm của tôi
        androidx.recyclerview.widget.RecyclerView recyclerMyProducts = findViewById(R.id.recycler_my_products);
        myProductsAdapter = new MyProductsAdapter(new MyProductsAdapter.OnProductActionListener() {
            @Override
            public void onEdit(com.google.firebase.firestore.DocumentSnapshot product) {
                startEditProductActivity(product);
            }
            @Override
            public void onDelete(com.google.firebase.firestore.DocumentSnapshot product) {
                showDeleteProductDialog(product);
            }
        });
        recyclerMyProducts.setAdapter(myProductsAdapter);
        recyclerMyProducts.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        // Đăng ký ActivityResultLauncher cho AddProduct
        ActivityResultLauncher<android.content.Intent> addProductLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Reload lại thông tin sản phẩm sau khi thêm sản phẩm
                    reloadProductInfo(email);
                }
            });
        // BottomNavigationView logic
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Mặc định: hiển thị frame_home, ẩn các frame khác
        showFrame(R.id.frame_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            int frameId;
            if (itemId == R.id.nav_home) {
                frameId = R.id.frame_home;
                loadHomeInfo();
            } else if (itemId == R.id.nav_heart) {
                frameId = R.id.frame_heart;
                loadHeartInfo();
            } else if (itemId == R.id.nav_wallet) {
                frameId = R.id.frame_wallet;
                loadWalletInfo();
            } else {
                frameId = R.id.frame_menu;
                loadMenuInfo();
            }
            showFrame(frameId);
            return true;
        });

        // Thêm sự kiện click cho nút Thêm sản ph��m
        findViewById(R.id.btn_add_product).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainBoardArtisan.this, AddProduct.class);
            intent.putExtra("email", email);
            addProductLauncher.launch(intent);
        });

        // Thêm sự kiện click cho nút Quản lý sản phẩm
        findViewById(R.id.btn_manage_products).setOnClickListener(v -> {
            showFrame(R.id.frame_heart);
            bottomNavigationView.setSelectedItemId(R.id.nav_heart);
            loadMyProducts(); // Load sản phẩm khi bấm nút quản lý
        });

        // Thêm sự kiện click cho artisan_info để mở ArtisanInfo và truyền email
        findViewById(R.id.artisan_info).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainBoardArtisan.this, ArtisanInfo.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });
    }

    private void startEditProductActivity(com.google.firebase.firestore.DocumentSnapshot product) {
        android.content.Intent intent = new android.content.Intent(MainBoardArtisan.this, AddProduct.class);
        intent.putExtra("email", email);
        intent.putExtra("productId", product.getId());
        intent.putExtra("productName", product.getString("Name"));
        intent.putExtra("description", product.getString("Description"));
        intent.putExtra("price", product.getString("Price"));
        intent.putExtra("category", product.getString("Category"));
        intent.putExtra("quantity", product.getString("Quantity"));
        intent.putExtra("imageUrl", product.getString("Image"));
        startActivity(intent);
    }

    private void showDeleteProductDialog(com.google.firebase.firestore.DocumentSnapshot product) {
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(MainBoardArtisan.this)
            .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?")
            .setCancelable(true)
            .create();
        android.widget.LinearLayout layout = new android.widget.LinearLayout(MainBoardArtisan.this);
        layout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        layout.setPadding(32, 16, 32, 16);
        layout.setGravity(android.view.Gravity.END);

        android.widget.Button btnDelete = new android.widget.Button(MainBoardArtisan.this);
        btnDelete.setText("Xóa");
        btnDelete.setTextColor(android.graphics.Color.WHITE);
        btnDelete.setBackgroundColor(android.graphics.Color.parseColor("#E53935")); // Red
        android.widget.LinearLayout.LayoutParams paramsDelete = new android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        paramsDelete.setMarginEnd(16);
        btnDelete.setLayoutParams(paramsDelete);

        android.widget.Button btnCancel = new android.widget.Button(MainBoardArtisan.this);
        btnCancel.setText("Hủy bỏ");
        btnCancel.setTextColor(android.graphics.Color.WHITE);
        btnCancel.setBackgroundColor(android.graphics.Color.parseColor("#757575")); // Gray
        android.widget.LinearLayout.LayoutParams paramsCancel = new android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        btnCancel.setLayoutParams(paramsCancel);

        layout.addView(btnDelete);
        layout.addView(btnCancel);

        dialog.setView(layout);
        dialog.setOnShowListener(d -> {
            btnDelete.setOnClickListener(v -> {
                dialog.dismiss();
                deleteProductWithImage(product);
            });
            btnCancel.setOnClickListener(v -> dialog.dismiss());
        });
        dialog.show();
    }

    // Hàm reloadProductInfo: chỉ dùng cho các thao tác thêm/xóa/sửa sản phẩm, cập nhật số lượng và trạng thái sản phẩm, không cập nhật sản phẩm nổi bật
    private void reloadProductInfo(String email) {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("users")
            .whereEqualTo("Email", email)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    com.google.firebase.firestore.DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                    userDoc.getReference().collection("products").get()
                        .addOnSuccessListener(productsSnapshot -> {
                            int productCount = productsSnapshot.size();
                            android.widget.TextView textStatus = findViewById(R.id.text_status);
                            android.widget.TextView textNoProducts = findViewById(R.id.text_no_products);
                            android.widget.LinearLayout linearFeatured1 = findViewById(R.id.linear_featured_1);
                            android.widget.LinearLayout linearFeatured2 = findViewById(R.id.linear_featured_2);
                            android.widget.LinearLayout linearManageProducts = findViewById(R.id.btn_manage_products);
                            if (productCount == 0) {
                                if (textNoProducts != null) textNoProducts.setVisibility(View.VISIBLE);
                                if (linearFeatured1 != null) linearFeatured1.setVisibility(View.GONE);
                                if (linearFeatured2 != null) linearFeatured2.setVisibility(View.GONE);
                                if (linearManageProducts != null) {
                                    linearManageProducts.setEnabled(false);
                                    linearManageProducts.setAlpha(0.5f);
                                }
                            } else {
                                if (textNoProducts != null) textNoProducts.setVisibility(View.GONE);
                                if (linearFeatured1 != null) linearFeatured1.setVisibility(View.VISIBLE);
                                if (linearFeatured2 != null) linearFeatured2.setVisibility(View.VISIBLE);
                                if (linearManageProducts != null) {
                                    linearManageProducts.setEnabled(true);
                                    linearManageProducts.setAlpha(1f);
                                }
                            }
                            if (textStatus != null) {
                                textStatus.setText(getString(R.string.product_count, productCount));
                                textStatus.setVisibility(View.VISIBLE);
                            }
                        })
                        .addOnFailureListener(e -> android.util.Log.e("MainBoardArtisan", "Lỗi truy vấn Products", e));
                }
            });
    }
    // Hàm load sản phẩm từ Firestore vào RecyclerView
    private void loadMyProducts() {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("users")
            .whereEqualTo("Email", email)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    com.google.firebase.firestore.DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                    userDoc.getReference().collection("products").get()
                        .addOnSuccessListener(productsSnapshot -> myProductsAdapter.setProducts(productsSnapshot.getDocuments()))
                        .addOnFailureListener(e -> android.util.Log.e("MainBoardArtisan", "Lỗi truy vấn Products", e));
                }
            });
    }

    // --- Cloudinary helper dùng chung cho mọi file ---
    public static void deleteCloudinaryImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return;
        com.example.hanoicraftcorner.utils.CloudinaryUploader.deleteImageByUrl(imageUrl, new com.example.hanoicraftcorner.utils.CloudinaryUploader.DeleteCallback() {
            @Override public void onSuccess() { android.util.Log.d("Cloudinary", "Xóa ảnh thành công"); }
            @Override public void onFailure(Exception e) { android.util.Log.e("Cloudinary", "Lỗi xóa ảnh", e); }
        });
    }

    // Xóa sản phẩm và ảnh trên Cloudinary nếu có
    private void deleteProductWithImage(com.google.firebase.firestore.DocumentSnapshot product) {
        deleteCloudinaryImage(product.getString("Image"));
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("Email", email).get().addOnSuccessListener(q -> {
            if (!q.isEmpty()) {
                com.google.firebase.firestore.DocumentSnapshot userDoc = q.getDocuments().get(0);
                userDoc.getReference().collection("products").document(product.getId())
                    .delete()
                    .addOnSuccessListener(unused -> loadMyProducts())
                    .addOnFailureListener(e -> android.widget.Toast.makeText(this, "Lỗi xóa sản phẩm!", android.widget.Toast.LENGTH_SHORT).show());
            }
        });
    }
    // Sửa lỗi không reload frame_menu khi ấn lại menu
    // Thay đổi showFrame để luôn gọi loadMenuInfo nếu frame_menu được chọn
    private void showFrame(int frameId) {
        int[] frameIds = {R.id.frame_home, R.id.frame_heart, R.id.frame_wallet, R.id.frame_menu};
        for (int id : frameIds) {
            findViewById(id).setVisibility(id == frameId ? View.VISIBLE : View.GONE);
        }
        if (frameId == R.id.frame_home) {
            reloadHomeFrame();
        } else if (frameId == R.id.frame_heart) {
            reloadHeartFrame();
        } else if (frameId == R.id.frame_wallet) {
            reloadWalletFrame();
        } else if (frameId == R.id.frame_menu) {
            reloadMenuFrame();
        }
    }

    // --- FRAME RELOAD METHODS ---
    private void reloadHomeFrame() {
        loadHomeInfo();
        // Add any additional logic for Home frame reload here
    }

    private void reloadHeartFrame() {
        loadMyProducts();
        reloadProductInfo(email);
        // Add any additional logic for Heart frame reload here
    }

    private void reloadWalletFrame() {
        loadWalletInfo();
        // Add any additional logic for Wallet frame reload here
    }

    private void reloadMenuFrame() {
        loadMenuInfo();
        // Add any additional logic for Menu frame reload here
    }

    private void loadMenuInfo() {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("users")
            .whereEqualTo("Email", email)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    com.google.firebase.firestore.DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                    // Cập nhật thông tin user
                    String username = userDoc.getString("Username");
                    String status = userDoc.getString("Status");
                    android.widget.TextView tv = findViewById(R.id.text_username);
                    android.widget.TextView textVerified = findViewById(R.id.text_verified);
                    if (tv != null && username != null) tv.setText(username);
                    if (textVerified != null && status != null) {
                        textVerified.setText(status.equalsIgnoreCase("Verified") ? R.string.verified_text : R.string.pending_verification_text);
                        android.view.ViewGroup parent = (android.view.ViewGroup) textVerified.getParent();
                        int idx = parent.indexOfChild(textVerified);
                        if (idx != -1 && idx + 1 < parent.getChildCount()) {
                            android.view.View dotView = parent.getChildAt(idx + 1);
                            dotView.setBackgroundResource(status.equalsIgnoreCase("Verified") ? R.drawable.green_dot_circle : R.drawable.yellow_dot_circle);
                        }
                    }
                    // Cập nhật sản phẩm nổi bật
                    userDoc.getReference().collection("products").get()
                        .addOnSuccessListener(productsSnapshot -> {
                            int productCount = productsSnapshot.size();
                            android.widget.TextView textStatus = findViewById(R.id.text_status);
                            android.widget.TextView textNoProducts = findViewById(R.id.text_no_products);
                            android.widget.LinearLayout linearFeatured1 = findViewById(R.id.linear_featured_1);
                            android.widget.LinearLayout linearFeatured2 = findViewById(R.id.linear_featured_2);
                            android.widget.LinearLayout linearManageProducts = findViewById(R.id.btn_manage_products);
                            android.widget.ImageView imageProduct1 = findViewById(R.id.image_product_1);
                            android.widget.TextView imageProductDetail1 = findViewById(R.id.image_product_detail_1);
                            android.widget.ImageView imageProduct2 = findViewById(R.id.image_product_2);
                            android.widget.TextView imageProductDetail2 = findViewById(R.id.image_product_detail_2);
                            if (productCount == 0) {
                                if (textNoProducts != null) textNoProducts.setVisibility(View.VISIBLE);
                                if (linearFeatured1 != null) linearFeatured1.setVisibility(View.GONE);
                                if (linearFeatured2 != null) linearFeatured2.setVisibility(View.GONE);
                                if (linearManageProducts != null) {
                                    linearManageProducts.setEnabled(false);
                                    linearManageProducts.setAlpha(0.5f);
                                }
                                if (imageProduct1 != null) imageProduct1.setImageResource(R.drawable.ic_launcher_foreground);
                                if (imageProductDetail1 != null) imageProductDetail1.setText("");
                                if (imageProduct2 != null) imageProduct2.setImageResource(R.drawable.ic_launcher_foreground);
                                if (imageProductDetail2 != null) imageProductDetail2.setText("");
                            } else {
                                if (textNoProducts != null) textNoProducts.setVisibility(View.GONE);
                                if (linearFeatured1 != null) linearFeatured1.setVisibility(View.VISIBLE);
                                if (linearFeatured2 != null) linearFeatured2.setVisibility(View.VISIBLE);
                                if (linearManageProducts != null) {
                                    linearManageProducts.setEnabled(true);
                                    linearManageProducts.setAlpha(1f);
                                }
                                // Lấy sản phẩm đầu tiên làm sản phẩm nổi bật 1
                                if (imageProduct1 != null && imageProductDetail1 != null && !productsSnapshot.isEmpty()) {
                                    com.google.firebase.firestore.DocumentSnapshot firstProduct = productsSnapshot.getDocuments().get(0);
                                    String imageUrl1 = firstProduct.getString("Image");
                                    String description1 = firstProduct.getString("Description");
                                    if (imageUrl1 != null && !imageUrl1.isEmpty()) {
                                        try {
                                            com.bumptech.glide.Glide.with(this).load(imageUrl1).into(imageProduct1);
                                        } catch (Exception e) {
                                            android.util.Log.e("MainBoardArtisan", "Lỗi load ảnh sản phẩm nổi bật 1", e);
                                        }
                                    } else {
                                        imageProduct1.setImageResource(R.drawable.ic_launcher_foreground);
                                    }
                                    imageProductDetail1.setText(description1 != null ? description1 : "");
                                }
                                // Lấy sản phẩm thứ hai làm sản phẩm nổi bật 2 (nếu có)
                                if (imageProduct2 != null && imageProductDetail2 != null && productsSnapshot.size() > 1) {
                                    com.google.firebase.firestore.DocumentSnapshot secondProduct = productsSnapshot.getDocuments().get(1);
                                    String imageUrl2 = secondProduct.getString("Image");
                                    String description2 = secondProduct.getString("Description");
                                    if (imageUrl2 != null && !imageUrl2.isEmpty()) {
                                        try {
                                            com.bumptech.glide.Glide.with(this).load(imageUrl2).into(imageProduct2);
                                        } catch (Exception e) {
                                            android.util.Log.e("MainBoardArtisan", "Lỗi load ảnh sản phẩm nổi bật 2", e);
                                        }
                                    } else {
                                        imageProduct2.setImageResource(R.drawable.ic_launcher_foreground);
                                    }
                                    imageProductDetail2.setText(description2 != null ? description2 : "");
                                } else if (imageProduct2 != null && imageProductDetail2 != null) {
                                    imageProduct2.setImageResource(R.drawable.ic_launcher_foreground);
                                    imageProductDetail2.setText("");
                                }
                            }
                            if (textStatus != null) {
                                textStatus.setText(getString(R.string.product_count, productCount));
                                textStatus.setVisibility(View.VISIBLE);
                            }
                        })
                        .addOnFailureListener(e -> android.util.Log.e("MainBoardArtisan", "Lỗi truy vấn Products", e));
                }
            });
    }

    private void loadHomeInfo() {
    }

    private void loadHeartInfo() {
        loadMyProducts();
    }

    private void loadWalletInfo() {
        // TODO: Implement wallet info loading if needed
    }
}
