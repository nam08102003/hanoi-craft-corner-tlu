package com.example.hanoicraftcorner;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProductDetail extends AppCompatActivity {

    private TextView tvTitle, tvPrice, tvDescription, tvSize,  tvStockLeft;
    private ImageView imgProduct;

    private TextView tvArtisan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        // Cài đặt Edge-to-Edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ view
        tvArtisan = findViewById(R.id.tvArtisan);
        tvTitle = findViewById(R.id.tvTitle);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        tvSize = findViewById(R.id.tvSize);
        tvStockLeft = findViewById(R.id.tvStockLeft);
        imgProduct = findViewById(R.id.imgProduct);


        // Nhận productId từ Intent
//        String productId = getIntent().getStringExtra("productId");
//        if (productId == null || productId.isEmpty()) {
//            // Nếu không có productId thì kết thúc
//            finish();
//            return;
//        }
//

        // Lấy dữ liệu từ Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference docRef = db.collection("products").document("cat_001");
//
//        docRef.get().addOnSuccessListener(documentSnapshot -> {
//            if (documentSnapshot.exists()) {
//                String title = documentSnapshot.getString("name");
//                String price = documentSnapshot.getString("price");
//                String description = documentSnapshot.getString("description");
//                String size = documentSnapshot.getString("category");
//                String imageUrl = documentSnapshot.getString("image");
//                String stock = documentSnapshot.getString("quantity");
//                String artisan = documentSnapshot.getString("artisan");

//
//                tvTitle.setText(title);
//                tvPrice.setText(price + " đ");
//                tvDescription.setText(description);
//                tvSize.setText("Danh mục: " + size);
//                tvStockLeft.setText(stock + " Sản phẩm còn lại");
//                tvArtisan.setText(artisan);

//                // Load ảnh
//                Glide.with(this).load(imageUrl).into(imgProduct);
//            }
//        }).addOnFailureListener(e -> {
//            // TODO: Thông báo lỗi hoặc hiển thị trạng thái không tải được
//        });
        db.collection("products")
                .whereEqualTo("category_id", "cat_013")
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                        String title = documentSnapshot.getString("name");
                        String price = documentSnapshot.getString("price");
                        String description = documentSnapshot.getString("description");
                        String size = documentSnapshot.getString("category");
                        String artisan = documentSnapshot.getString("artisan");
                        String imageUrl = documentSnapshot.getString("image");
                        Object stockObj = documentSnapshot.get("quantity");
                        Long stock = null;
                        if (stockObj instanceof Number) {
                            stock = ((Number) stockObj).longValue();
                        } else if (stockObj instanceof String) {
                            try {
                                stock = Long.parseLong((String) stockObj);
                            } catch (NumberFormatException e) {
                                stock = null; // or set a default value
                            }
                        }

                        tvArtisan.setText(artisan);
                        tvTitle.setText(title);
                        tvPrice.setText(price + " đ");
                        tvDescription.setText(description);
                        tvSize.setText("Danh mục: " + size);
                        if (stock != null) {
                            tvStockLeft.setText("Sản phẩm còn lại: "+ stock);
                        } else {
                            tvStockLeft.setText("Không rõ số lượng");
                        }

                        Glide.with(this).load(imageUrl).into(imgProduct);
                    } else {
                        tvTitle.setText("Không tìm thấy sản phẩm");
                    }
                }).addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "Lỗi tải dữ liệu từ Firestore", Toast.LENGTH_SHORT).show();
                });

    }
}
