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

    // Category sliding logic
    private java.util.List<String> allCategories = new java.util.ArrayList<>();
    private int categoryStartIndex = 0;
    private android.widget.Button btnCategory1, btnCategory2, btnCategory3;
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

        // Thêm sự kiện click cho nút Thêm sản phẩm
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

        // Category buttons and arrows
        btnCategory1 = findViewById(R.id.btn_category_1);
        btnCategory2 = findViewById(R.id.btn_category_2);
        btnCategory3 = findViewById(R.id.btn_category_3);
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

    private void loadFeaturedProducts() {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        java.util.List<java.util.Map<String, Object>> featuredList = new java.util.ArrayList<>();
        db.collection("users").get().addOnSuccessListener(usersSnap -> {
            java.util.List<com.google.firebase.firestore.DocumentSnapshot> users = usersSnap.getDocuments();
            if (users.isEmpty()) {
                featuredProductsAdapter.setProducts(featuredList);
                return;
            }
            final int[] counter = {0};
            for (com.google.firebase.firestore.DocumentSnapshot user : users) {
                user.getReference().collection("products").get().addOnSuccessListener(productsSnap -> {
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
                    counter[0]++;
                    if (counter[0] == users.size()) {
                        featuredProductsAdapter.setProducts(featuredList);
                    }
                }).addOnFailureListener(e -> {
                    counter[0]++;
                    if (counter[0] == users.size()) {
                        featuredProductsAdapter.setProducts(featuredList);
                    }
                });
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
            btnCategory1.setText("");
            btnCategory2.setText("");
            btnCategory3.setText("");
            btnCategoryLeft.setVisibility(View.GONE);
            btnCategoryRight.setVisibility(View.GONE);
            return;
        }
        btnCategory1.setText(allCategories.get(categoryStartIndex));
        btnCategory2.setText(allCategories.get(categoryStartIndex + 1));
        btnCategory3.setText(allCategories.get(categoryStartIndex + 2));
        btnCategoryLeft.setVisibility(categoryStartIndex == 0 ? View.GONE : View.VISIBLE);
        btnCategoryRight.setVisibility(categoryStartIndex + 3 >= allCategories.size() ? View.GONE : View.VISIBLE);
    }

    private void animateCategorySlide(int direction) {
        // direction: -1 = left, 1 = right
        int distance = btnCategory1.getWidth() + btnCategory2.getWidth() + btnCategory3.getWidth();
        float toX = direction * -distance;
        btnCategory1.animate().translationX(toX).setDuration(200).withEndAction(() -> btnCategory1.setTranslationX(0)).start();
        btnCategory2.animate().translationX(toX).setDuration(200).withEndAction(() -> btnCategory2.setTranslationX(0)).start();
        btnCategory3.animate().translationX(toX).setDuration(200).withEndAction(() -> btnCategory3.setTranslationX(0)).start();
    }
}
