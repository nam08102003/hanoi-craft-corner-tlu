package com.example.hanoicraftcorner.mainboard_artisan;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hanoicraftcorner.R;

public class MainBoardArtisan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_board_artisan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lấy email từ intent, nếu rỗng thì gán noreply@gmail.com
        String email = getIntent().getStringExtra("email");
        if (email == null || email.isEmpty()) {
            email = "noreply@gmail.com";
        }

        // Lấy ảnh sản phẩm đầu tiên của user từ Firestore và decode vào image_product_1
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("Users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    com.google.firebase.firestore.DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                    userDoc.getReference().collection("artisanInfo").get().addOnSuccessListener(artisanInfoSnaps -> {
                        if (!artisanInfoSnaps.isEmpty()) {
                            com.google.firebase.firestore.DocumentSnapshot artisanInfoDoc = artisanInfoSnaps.getDocuments().get(0);
                            artisanInfoDoc.getReference().collection("Image").get().addOnSuccessListener(imageSnaps -> {
                                if (!imageSnaps.isEmpty()) {
                                    com.google.firebase.firestore.DocumentSnapshot imageDoc = imageSnaps.getDocuments().get(0);
                                    String base64 = imageDoc.getString("base64");
                                    if (base64 != null && !base64.isEmpty()) {
                                        try {
                                            byte[] decodedString = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
                                            android.graphics.Bitmap decodedBitmap = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                            int productWidth = (int) (60 * getResources().getDisplayMetrics().density);
                                            int productHeight = (int) (90 * getResources().getDisplayMetrics().density);
                                            android.graphics.Bitmap productBitmap = android.graphics.Bitmap.createScaledBitmap(decodedBitmap, productWidth, productHeight, true);
                                            android.widget.ImageView productImage1 = findViewById(R.id.image_product_1);
                                            if (productImage1 != null) {
                                                productImage1.setImageBitmap(productBitmap);
                                            }
                                        } catch (Exception e) {
                                            // Nếu lỗi decode thì không set ảnh
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            });
    }
}