package com.example.hanoicraftcorner.main.artisan;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.View;

public class MainBoardArtisan extends AppCompatActivity {
    private MyProductsAdapter myProductsAdapter;
    private FeaturedProductsAdapter featuredProductsAdapter;
    private ActivityResultLauncher<android.content.Intent> addProductLauncher;

    // Category sliding logic
    private final java.util.List<String> allCategories = new java.util.ArrayList<>();
    private int categoryStartIndex = 0;
    private android.widget.Button[] btnCategories;
    private android.widget.ImageButton btnCategoryLeft, btnCategoryRight;

    private String userId;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getStringExtra("user_id");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_board_artisan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnCategories = new android.widget.Button[] {
            findViewById(R.id.btn_category_1),
            findViewById(R.id.btn_category_2),
            findViewById(R.id.btn_category_3)
        };

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

        // Setup Featured Products RecyclerView
        androidx.recyclerview.widget.RecyclerView recyclerFeaturedProducts = findViewById(R.id.recycler_featured_products);
        featuredProductsAdapter = new FeaturedProductsAdapter();
        recyclerFeaturedProducts.setAdapter(featuredProductsAdapter);
        recyclerFeaturedProducts.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        loadFeaturedProducts();

        // Đăng ký ActivityResultLauncher cho AddProduct
        addProductLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Reload lại thông tin sản phẩm sau khi thêm sản phẩm
                    reloadProductInfo(userId);
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

        // Thêm sự kiện click cho nút Thêm sản phẩm
        findViewById(R.id.btn_add_product).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainBoardArtisan.this, AddProduct.class);
            intent.putExtra("user_id", userId);
            intent.putStringArrayListExtra("categories", new java.util.ArrayList<>(allCategories));
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
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        // Setup ad ViewPager2 from Firestore (only load Image field)
        androidx.viewpager2.widget.ViewPager2 viewPagerAds = findViewById(R.id.viewpager_ads);
        java.util.List<String> adImages = new java.util.ArrayList<>();
        AdPagerAdapter adPagerAdapter = new AdPagerAdapter(this, adImages);
        viewPagerAds.setAdapter(adPagerAdapter);
        viewPagerAds.setOffscreenPageLimit(1);
        viewPagerAds.setOrientation(androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL);
        // Load ads from Firestore
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("ads").get().addOnSuccessListener(queryDocumentSnapshots -> {
            adImages.clear();
            for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                String imageUrl = doc.getString("Image");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    adImages.add(imageUrl);
                }
            }
            adPagerAdapter.notifyDataSetChanged();
        });

        btnCategoryLeft = findViewById(R.id.btn_category_left);
        btnCategoryRight = findViewById(R.id.btn_category_right);
        updateCategoryButtons();

        btnCategoryRight.setOnClickListener(v -> {
            if (categoryStartIndex + 3 < allCategories.size()) {
                animateCategorySlide(-1); // Slide left
                categoryStartIndex++;
                updateCategoryButtons();
            }
        });
        btnCategoryLeft.setOnClickListener(v -> {
            if (categoryStartIndex > 0) {
                animateCategorySlide(1); // Slide right
                categoryStartIndex--;
                updateCategoryButtons();
            }
        });

        // Gán sự kiện click cho các nút category để lọc sản phẩm nổi bật
        for (int i = 0; i < btnCategories.length; i++) {
            final int idx = i;
            btnCategories[i].setOnClickListener(v -> {
                selectCategoryButton(idx);
                String category = btnCategories[idx].getText().toString();
                if (!category.isEmpty()) loadFeaturedProductsByCategory(category);
            });
        }
        // Set default selected
        selectCategoryButton(0);

        // Load categories from Firestore
        db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("categories").get().addOnSuccessListener(queryDocumentSnapshots -> {
            allCategories.clear();
            for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                String name = doc.getString("name");
                if (name != null && !name.isEmpty()) {
                    allCategories.add(name);
                }
            }
            // Ensure at least 3 categories for UI
            while (allCategories.size() < 3) allCategories.add("");
            categoryStartIndex = 0;
            updateCategoryButtons();
        });

        loadFeaturedArtisan();
        loadMyFeaturedProduct();

        // Thêm sự kiện click cho nút Thêm sản phẩm trong frame_menu
        findViewById(R.id.btn_add_product).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainBoardArtisan.this, AddProduct.class);
            intent.putExtra("user_id", userId);
            intent.putStringArrayListExtra("categories", new java.util.ArrayList<>(allCategories));
            addProductLauncher.launch(intent);
        });
    }

    private void startEditProductActivity(com.google.firebase.firestore.DocumentSnapshot product) {
        android.content.Intent intent = new android.content.Intent(MainBoardArtisan.this, AddProduct.class);
        intent.putExtra("isEdit", true);
        intent.putExtra("product_id", product.getId());
        intent.putExtra("user_id", userId);
        intent.putExtra("name", product.getString("name"));
        intent.putExtra("quantity", product.getString("quantity"));
        intent.putExtra("price", product.getString("price"));
        intent.putExtra("image", product.getString("image"));
        intent.putExtra("description", product.getString("description"));
        intent.putExtra("category", product.getString("category"));
        intent.putStringArrayListExtra("categories", new java.util.ArrayList<>(allCategories));
        addProductLauncher.launch(intent);
    }

    private void showDeleteProductDialog(com.google.firebase.firestore.DocumentSnapshot product) {
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(MainBoardArtisan.this)
            .setMessage("Bạn có chắc chắn muốn xóa sản phẩm?")
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
        btnCancel.setText("Hủy");
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
    private void reloadProductInfo(String userId) {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("products")
            .whereEqualTo("user_id", userId)
            .get()
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
    // Hàm load sản phẩm từ Firestore vào RecyclerView
    private void loadMyProducts() {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("products")
            .whereEqualTo("user_id", userId)
            .get()
            .addOnSuccessListener(productsSnapshot -> myProductsAdapter.setProducts(productsSnapshot.getDocuments()))
            .addOnFailureListener(e -> android.util.Log.e("MainBoardArtisan", "Lỗi truy vấn Products", e));
    }

    private void loadFeaturedProducts() {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        java.util.List<Product> featuredList = new java.util.ArrayList<>();
        db.collection("products")
            .get()
            .addOnSuccessListener(productsSnap -> {
                for (com.google.firebase.firestore.DocumentSnapshot productDoc : productsSnap.getDocuments()) {
                    Product product = productDoc.toObject(Product.class);
                    if (product != null) {
                        featuredList.add(product);
                    }
                }
                // You may need to update your FeaturedProductsAdapter to accept List<Product>
                featuredProductsAdapter.setProducts(featuredList);
            });
    }

    private void loadFeaturedArtisan() {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("users")
            .whereEqualTo("hot", true)
            .limit(1)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    com.google.firebase.firestore.DocumentSnapshot artisan = queryDocumentSnapshots.getDocuments().get(0);
                    String name = artisan.getString("name");
                    String intro = artisan.getString("introduce");
                    String backgroundUrl = artisan.getString("background");
                    android.widget.TextView tvName = findViewById(R.id.tv_artisan_name);
                    android.widget.TextView tvIntro = findViewById(R.id.tv_artisan_intro);
                    android.widget.ImageView imgCover = findViewById(R.id.img_artisan_cover);
                    if (tvName != null && name != null) tvName.setText(name);
                    if (tvIntro != null && intro != null) tvIntro.setText(intro);
                    if (imgCover != null && backgroundUrl != null && !backgroundUrl.isEmpty()) {
                        try {
                            com.bumptech.glide.Glide.with(this).load(backgroundUrl).centerCrop().into(imgCover);
                        } catch (Exception e) {
                            imgCover.setImageResource(R.drawable.ic_baseline_person_24);
                        }
                    } else if (imgCover != null) {
                        imgCover.setImageResource(R.drawable.ic_baseline_person_24);
                    }
                }
            });
    }

    // Hiển thị sản phẩm nổi bật của tôi vào linear_featured_1 và linear_featured_2 (nếu có)
    private void loadMyFeaturedProduct() {
        String currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null ? com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid() : userId;
        com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("products")
            .whereEqualTo("user_id", currentUserId)
            .limit(2)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                android.util.Log.d("FeaturedProduct", "Query size: " + queryDocumentSnapshots.size());
                if (!queryDocumentSnapshots.isEmpty()) {
                    // Sản phẩm 1
                    com.example.hanoicraftcorner.model.Product product1 = queryDocumentSnapshots.getDocuments().get(0).toObject(com.example.hanoicraftcorner.model.Product.class);
                    android.util.Log.d("FeaturedProduct", "Product 1: " + (product1 != null ? product1.getName() + ", images: " + product1.getImages() + ", desc: " + product1.getDescription() : "null"));
                    if (product1 != null) {
                        android.widget.ImageView imageView1 = findViewById(R.id.image_product_1);
                        android.widget.TextView detailView1 = findViewById(R.id.image_product_detail_1);
                        String imageUrl1 = (product1.getImages() != null && !product1.getImages().isEmpty()) ? product1.getImages().get(0) : null;
                        if (imageView1 != null) {
                            if (imageUrl1 != null && !imageUrl1.isEmpty()) {
                                com.bumptech.glide.Glide.with(this).load(imageUrl1).placeholder(R.drawable.ic_launcher_foreground).into(imageView1);
                            } else {
                                imageView1.setImageResource(R.drawable.ic_launcher_foreground);
                            }
                        }
                        if (detailView1 != null) {
                            detailView1.setText(product1.getDescription() != null ? product1.getDescription() : "");
                        }
                    }
                    // Sản phẩm 2 (nếu có)
                    if (queryDocumentSnapshots.size() > 1) {
                        com.example.hanoicraftcorner.model.Product product2 = queryDocumentSnapshots.getDocuments().get(1).toObject(com.example.hanoicraftcorner.model.Product.class);
                        android.util.Log.d("FeaturedProduct", "Product 2: " + (product2 != null ? product2.getName() + ", images: " + product2.getImages() + ", desc: " + product2.getDescription() : "null"));
                        if (product2 != null) {
                            android.widget.ImageView imageView2 = findViewById(R.id.image_product_2);
                            android.widget.TextView detailView2 = findViewById(R.id.image_product_detail_2);
                            String imageUrl2 = (product2.getImages() != null && !product2.getImages().isEmpty()) ? product2.getImages().get(0) : null;
                            if (imageView2 != null) {
                                if (imageUrl2 != null && !imageUrl2.isEmpty()) {
                                    com.bumptech.glide.Glide.with(this).load(imageUrl2).placeholder(R.drawable.ic_launcher_foreground).into(imageView2);
                                } else {
                                    imageView2.setImageResource(R.drawable.ic_launcher_foreground);
                                }
                            }
                            if (detailView2 != null) {
                                detailView2.setText(product2.getDescription() != null ? product2.getDescription() : "");
                            }
                        }
                    }
                } else {
                    android.util.Log.d("FeaturedProduct", "No products found for user: " + currentUserId);

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
        // Lấy danh sách ảnh từ trường images (kiểu List<String>), kiểm tra an toàn kiểu dữ liệu
        java.util.List<String> images = null;
        Object imagesObj = product.get("images");
        if (imagesObj instanceof java.util.List<?>) {
            images = new java.util.ArrayList<>();
            for (Object o : (java.util.List<?>) imagesObj) {
                if (o instanceof String) images.add((String) o);
            }
        }
        if (images != null && !images.isEmpty()) {
            for (String imageUrl : images) {
                deleteCloudinaryImage(imageUrl);
            }
        }
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("products").document(product.getId())
            .delete()
            .addOnSuccessListener(unused -> loadMyProducts())
            .addOnFailureListener(e -> android.widget.Toast.makeText(this, "Lỗi xóa sản phẩm!", android.widget.Toast.LENGTH_SHORT).show());
    }
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
        reloadProductInfo(userId);
        // Add any additional logic for Heart frame reload here
    }

    private void reloadWalletFrame() {
        loadWalletInfo();
        // Add any additional logic for Wallet frame reload here
    }

    private void reloadMenuFrame() {
        loadMenuInfo();
        // Không gọi lại cập nhật featured ở đây
    }

    private void loadMenuInfo() {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("name");
                    String status = documentSnapshot.getString("status");
                    String avatarUrl = documentSnapshot.getString("avatar");
                    android.util.Log.d("MainBoardArtisan", "avatarUrl from Firestore: " + avatarUrl);
                    android.widget.TextView tv = findViewById(R.id.text_username);
                    android.widget.TextView helloUsername = findViewById(R.id.text_greeting);
                    android.widget.TextView textVerified = findViewById(R.id.text_verified);
                    android.widget.ImageView imageAvatar = findViewById(R.id.image_avatar);
                    if (tv != null && username != null) tv.setText(username);
                    if (helloUsername != null && username != null) helloUsername.setText(getString(R.string.hello_username, username));
                    if (textVerified != null && status != null) {
                        textVerified.setText(status.equalsIgnoreCase("verified") ? R.string.verified_text : R.string.pending_verification_text);
                        android.view.ViewGroup parent = (android.view.ViewGroup) textVerified.getParent();
                        int idx = parent.indexOfChild(textVerified);
                        if (idx != -1 && idx + 1 < parent.getChildCount()) {
                            android.view.View dotView = parent.getChildAt(idx + 1);
                            dotView.setBackgroundResource(status.equalsIgnoreCase("verified") ? R.drawable.green_dot_circle : R.drawable.yellow_dot_circle);
                        }
                    }
                    // Load avatar bằng Glide
                    if (avatarUrl != null && !avatarUrl.isEmpty() && imageAvatar != null) {
                        try {
                            com.bumptech.glide.Glide.with(this).load(avatarUrl).circleCrop().into(imageAvatar);
                        } catch (Exception e) {
                            android.util.Log.e("MainBoardArtisan", "Glide error loading avatar", e);
                            imageAvatar.setImageResource(R.drawable.ic_baseline_person_24);
                        }
                    } else if (imageAvatar != null) {
                        android.util.Log.w("MainBoardArtisan", "avatarUrl is null or empty, using default avatar");
                        imageAvatar.setImageResource(R.drawable.ic_baseline_person_24);
                    }
                    // Không reset/cập nhật sản phẩm nổi bật ở đây nữa
                }
            });
    }

    private void loadHomeInfo() {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("name");
                    String avatarUrl = documentSnapshot.getString("avatar");
                    android.widget.TextView helloUsername = findViewById(R.id.text_hello_username);
                    android.widget.ImageView imageAvatarHome = findViewById(R.id.image_avatar_home);
                    if (helloUsername != null && username != null) {
                        helloUsername.setText(getString(R.string.hello_username, username));
                    }
                    if (avatarUrl != null && !avatarUrl.isEmpty() && imageAvatarHome != null) {
                        try {
                            com.bumptech.glide.Glide.with(this)
                                .load(avatarUrl)
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .error(R.drawable.ic_baseline_person_24)
                                .circleCrop()
                                .into(imageAvatarHome);
                        } catch (Exception e) {
                            android.util.Log.e("MainBoardArtisan", "Glide error", e);
                        }
                    }
                    // Add click listener to avatar to open menu frame
                    if (imageAvatarHome != null) {
                        imageAvatarHome.setOnClickListener(v -> {
                            showFrame(R.id.frame_menu);
                            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
                            bottomNavigationView.setSelectedItemId(R.id.nav_menu);
                        });
                    }
                }
            });
    }

    private void loadHeartInfo() {
        loadMyProducts();
    }

    private void loadWalletInfo() {
        // TODO: Implement wallet info loading if needed
    }

    private void updateCategoryButtons() {
        if (allCategories == null || allCategories.size() < 3) {
            for (android.widget.Button btnCategory : btnCategories) {
                btnCategory.setText("");
            }
            btnCategoryLeft.setVisibility(View.GONE);
            btnCategoryRight.setVisibility(View.GONE);
            return;
        }
        for (android.widget.Button btnCategory : btnCategories) {
            btnCategory.setText(allCategories.get(categoryStartIndex + java.util.Arrays.asList(btnCategories).indexOf(btnCategory)));
        }
        if (selectedCategoryIndex >= 0 && selectedCategoryIndex < btnCategories.length) {
            selectCategoryButton(selectedCategoryIndex);
        } else {
            // Deactivate all buttons if no valid selection
            for (android.widget.Button btnCategory : btnCategories) {
                btnCategory.setBackgroundResource(android.R.color.transparent);
                btnCategory.setTextColor(getColor(R.color.primary));
            }
            // Hiện tất cả sản phẩm nếu không có nút nào được chọn
            loadFeaturedProducts();
        }
        btnCategoryLeft.setVisibility(categoryStartIndex == 0 ? View.GONE : View.VISIBLE);
        btnCategoryRight.setVisibility(categoryStartIndex + 3 >= allCategories.size() ? View.GONE : View.VISIBLE);
    }

    private void animateCategorySlide(int direction) {
        // direction: -1 = left, 1 = right
        int distance = btnCategories[0].getWidth() + btnCategories[1].getWidth() + btnCategories[2].getWidth();
        float toX = direction * -distance;
        for (android.widget.Button btnCategory : btnCategories) {
            btnCategory.animate().translationX(toX).setDuration(200).withEndAction(() -> btnCategory.setTranslationX(0)).start();
        }
    }

    // Hàm lọc sản phẩm nổi bật theo category
    private void loadFeaturedProductsByCategory(String category) {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        java.util.List<Product> featuredList = new java.util.ArrayList<>();
        db.collection("products")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener(productsSnap -> {
                for (com.google.firebase.firestore.DocumentSnapshot productDoc : productsSnap.getDocuments()) {
                    Product product = productDoc.toObject(Product.class);
                    if (product != null) {
                        featuredList.add(product);
                    }
                }
                featuredProductsAdapter.setProducts(featuredList);
            });
    }

    // Highlight selected category button with background and text color
    private int selectedCategoryIndex = 0;
    private void selectCategoryButton(int selectedIndex) {
        // Nếu nút đã active thì deactive nó, ngược lại thì active
        if (this.selectedCategoryIndex == selectedIndex) {
            btnCategories[selectedIndex].setBackgroundResource(android.R.color.transparent);
            btnCategories[selectedIndex].setTextColor(getColor(R.color.primary));
            this.selectedCategoryIndex = -1;
        } else {
            this.selectedCategoryIndex = selectedIndex;
            for (int i = 0; i < btnCategories.length; i++) {
                if (i == selectedIndex) {
                    btnCategories[i].setBackgroundResource(R.drawable.category_button_background);
                    btnCategories[i].setTextColor(getColor(android.R.color.white));
                } else {
                    btnCategories[i].setBackgroundResource(android.R.color.transparent);
                    btnCategories[i].setTextColor(getColor(R.color.primary));
                }
            }
        }
    }
}
