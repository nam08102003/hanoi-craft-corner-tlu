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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.View;

public class MainBoardArtisan extends AppCompatActivity {

    private String email;
    private MyProductsAdapter myProductsAdapter;
    private FeaturedProductsAdapter featuredProductsAdapter;
    private ActivityResultLauncher<android.content.Intent> addProductLauncher;

    // Category sliding logic
    private final java.util.List<String> allCategories = new java.util.ArrayList<>();
    private int categoryStartIndex = 0;
    private android.widget.Button[] btnCategories;
    private android.widget.ImageButton btnCategoryLeft, btnCategoryRight;

    @SuppressLint("NotifyDataSetChanged")
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

        // Thêm sự kiện click cho nút Thêm sản phẩm
        findViewById(R.id.btn_add_product).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainBoardArtisan.this, AddProduct.class);
            intent.putExtra("email", email);
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
            intent.putExtra("email", email);
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
    }

    private void startEditProductActivity(com.google.firebase.firestore.DocumentSnapshot product) {
        android.content.Intent intent = new android.content.Intent(MainBoardArtisan.this, AddProduct.class);
        intent.putExtra("isEdit", true);
        intent.putExtra("productId", product.getId());
        intent.putExtra("Name", product.getString("Name"));
        intent.putExtra("Quantity", product.getString("Quantity"));
        intent.putExtra("Price", product.getString("Price"));
        intent.putExtra("Image", product.getString("Image"));
        intent.putExtra("Description", product.getString("Description"));
        intent.putExtra("Category", product.getString("Category"));
        intent.putExtra("email", email);
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
    private void reloadProductInfo(String email) {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("products")
            .whereEqualTo("email", email)
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
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener(productsSnapshot -> myProductsAdapter.setProducts(productsSnapshot.getDocuments()))
            .addOnFailureListener(e -> android.util.Log.e("MainBoardArtisan", "Lỗi truy vấn Products", e));
    }

    private void loadFeaturedProducts() {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        java.util.List<java.util.Map<String, Object>> featuredList = new java.util.ArrayList<>();
        db.collection("products")
            .get()
            .addOnSuccessListener(productsSnap -> {
                for (com.google.firebase.firestore.DocumentSnapshot product : productsSnap.getDocuments()) {
                    String img = product.getString("Image");
                    String name = product.getString("Name");
                    String price = product.getString("Price");
                    if (img != null && name != null && price != null) {
                        java.util.Map<String, Object> map = new java.util.HashMap<>();
                        map.put("Image", img);
                        map.put("Name", name);
                        map.put("Price", price);
                        featuredList.add(map);
                    }
                }
                featuredProductsAdapter.setProducts(featuredList);
            });
    }

    private void loadFeaturedArtisan() {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("users")
            .whereEqualTo("Role", "artisan")
            .limit(1)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    com.google.firebase.firestore.DocumentSnapshot artisan = queryDocumentSnapshots.getDocuments().get(0);
                    String name = artisan.getString("Username");
                    String intro = artisan.getString("Introduce");
                    String backgroundUrl = artisan.getString("Background");
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
        db.collection("products").document(product.getId())
            .delete()
            .addOnSuccessListener(unused -> loadMyProducts())
            .addOnFailureListener(e -> android.widget.Toast.makeText(this, "Lỗi xóa sản phẩm!", android.widget.Toast.LENGTH_SHORT).show());
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
                    String avatarUrl = userDoc.getString("Avatar");
                    android.util.Log.d("MainBoardArtisan", "avatarUrl from Firestore: " + avatarUrl);
                    android.widget.TextView tv = findViewById(R.id.text_username);
                    android.widget.TextView helloUsername = findViewById(R.id.text_greeting);
                    android.widget.TextView textVerified = findViewById(R.id.text_verified);
                    android.widget.ImageView imageAvatar = findViewById(R.id.image_avatar);
                    if (tv != null && username != null) tv.setText(username);
                    if (helloUsername != null && username != null) helloUsername.setText(getString(R.string.hello_username, username));
                    if (textVerified != null && status != null) {
                        textVerified.setText(status.equalsIgnoreCase("Verified") ? R.string.verified_text : R.string.pending_verification_text);
                        android.view.ViewGroup parent = (android.view.ViewGroup) textVerified.getParent();
                        int idx = parent.indexOfChild(textVerified);
                        if (idx != -1 && idx + 1 < parent.getChildCount()) {
                            android.view.View dotView = parent.getChildAt(idx + 1);
                            dotView.setBackgroundResource(status.equalsIgnoreCase("Verified") ? R.drawable.green_dot_circle : R.drawable.yellow_dot_circle);
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
                    // Cập nhật sản phẩm nổi bật
                    com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("products")
                        .whereEqualTo("email", email)
                        .get()
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
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("users")
            .whereEqualTo("Email", email)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    com.google.firebase.firestore.DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                    String username = userDoc.getString("Username");
                    String avatarUrl = userDoc.getString("Avatar");
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
        for (int i = 0; i < btnCategories.length; i++) {
            btnCategories[i].setText(allCategories.get(categoryStartIndex + i));
        }
        selectCategoryButton(selectedCategoryIndex); // Always update highlight after changing text
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
        java.util.List<java.util.Map<String, Object>> featuredList = new java.util.ArrayList<>();
        db.collection("products")
            .whereEqualTo("Category", category)
            .get()
            .addOnSuccessListener(productsSnap -> {
                for (com.google.firebase.firestore.DocumentSnapshot product : productsSnap.getDocuments()) {
                    String img = product.getString("Image");
                    String name = product.getString("Name");
                    String price = product.getString("Price");
                    if (img != null && name != null && price != null) {
                        java.util.Map<String, Object> map = new java.util.HashMap<>();
                        map.put("Image", img);
                        map.put("Name", name);
                        map.put("Price", price);
                        featuredList.add(map);
                    }
                }
                featuredProductsAdapter.setProducts(featuredList);
            });
    }

    // Highlight selected category button with background and text color
    private int selectedCategoryIndex = 0;
    private void selectCategoryButton(int selectedIndex) {
        selectedCategoryIndex = selectedIndex;
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
