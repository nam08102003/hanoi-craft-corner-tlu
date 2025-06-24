package com.example.hanoicraftcorner.main.mainboard_user;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.adapter.FavoriteAdapter;
import com.example.hanoicraftcorner.model.FavoriteItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainboardUser extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteAdapter adapter;
    private List<FavoriteItem> favoriteItemList;

    // =========================================================================================
    // QUAN TRỌNG: THAY THẾ CÁC URL SAU BẰNG URL ẢNH THỰC TẾ TỪ FIREBASE STORAGE CỦA BẠN
    // =========================================================================================
    private static final String IMAGE_URL_KHAY_MUT = "https://firebasestorage.googleapis.com/v0/b/your-project-id.appspot.com/o/images%2Fkhay_mut_5_ngan.jpg?alt=media";
    private static final String IMAGE_URL_TUONG_RAN = "https://firebasestorage.googleapis.com/v0/b/your-project-id.appspot.com/o/images%2Ftuong_ran_phuc_thai.jpg?alt=media";
    private static final String IMAGE_URL_BO_DO_AN = "https://firebasestorage.googleapis.com/v0/b/your-project-id.appspot.com/o/images%2Fbo_do_an_tu_quy.jpg?alt=media";
    private static final String IMAGE_URL_DEN_LONG = "https://firebasestorage.googleapis.com/v0/b/your-project-id.appspot.com/o/images%2Fden_long_hoi_an.jpg?alt=media";
    private static final String IMAGE_URL_TRANH_DONG_HO = "https://firebasestorage.googleapis.com/v0/b/your-project-id.appspot.com/o/images%2Ftranh_dong_ho_thuan_buom.jpg?alt=media";
    private static final String IMAGE_URL_GOM_BAT_TRANG = "https://firebasestorage.googleapis.com/v0/b/your-project-id.appspot.com/o/images%2Fgom_su_bat_trang_binh_hoa.jpg?alt=media";
    // Ví dụ: your-project-id là ID dự án Firebase của bạn.
    // images%2Fkhay_mut_5_ngan.jpg là đường dẫn đã mã hóa đến tệp ảnh.
    // Bạn có thể lấy URL này từ Firebase Console sau khi tải ảnh lên Storage.
    // =========================================================================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đảm bảo R.layout.activity_dashboard_user là layout chính xác cho Activity này.
        // Nó nên chứa RecyclerView với ID là recyclerViewFavorites và các view khác bạn dùng.
        setContentView(R.layout.activity_dashboard_user);

        // Khởi tạo các view
        recyclerView = findViewById(R.id.recyclerViewFavorites);
        ImageButton btnBack = findViewById(R.id.btnBack);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        // Cài đặt RecyclerView
        setupRecyclerView();

        // Tải dữ liệu (giả lập với URL)
        loadFavoriteData();

        // Xử lý sự kiện click
        btnBack.setOnClickListener(v -> {
            finish(); // Quay lại màn hình trước đó
        });

        // Đánh dấu mục đang được chọn trên BottomNav (ví dụ: mục Yêu thích)
        // Thay R.id.nav_favorites bằng ID của item menu "Yêu thích" trong bottom_nav_menu.xml của bạn
        // bottomNav.setSelectedItemId(R.id.nav_favorites);

        bottomNav.setOnItemSelectedListener(item -> {
            // int itemId = item.getItemId();
            // Xử lý chuyển màn hình dựa trên itemId
            // if (itemId == R.id.nav_home) { /* Chuyển đến Home */ return true; }
            // else if (itemId == R.id.nav_favorites) { /* Đang ở đây */ return true; }
            // else if (itemId == R.id.nav_profile) { /* Chuyển đến Profile */ return true; }
            return true; // Trả về true để hiển thị item là selected
        });
    }

    private void setupRecyclerView() {
        favoriteItemList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FavoriteAdapter(favoriteItemList, new FavoriteAdapter.OnItemClickListener() {
            @Override
            public void onRemoveClick(int position) {
                if (position >= 0 && position < favoriteItemList.size()) {
                    FavoriteItem itemToRemove = favoriteItemList.get(position);
                    // TODO: Logic xóa khỏi Firestore
                    // Ví dụ:
                    // FirebaseFirestore.getInstance().collection("users").document(userId)
                    // .collection("favorites").document(itemToRemove.getProductId()).delete()
                    // .addOnSuccessListener(aVoid -> {
                    // adapter.removeItem(position); // Xóa khỏi UI nếu thành công
                    // Toast.makeText(MainboardUser.this, "Đã xóa: " + itemToRemove.getName(), Toast.LENGTH_SHORT).show();
                    // })
                    // .addOnFailureListener(e -> Toast.makeText(MainboardUser.this, "Lỗi khi xóa", Toast.LENGTH_SHORT).show());

                    // Tạm thời xóa khỏi UI để test
                    adapter.removeItem(position);
                    Toast.makeText(MainboardUser.this, "Đã xóa (UI): " + itemToRemove.getName(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDetailsClick(FavoriteItem item) {
                // TODO: Logic chuyển sang màn hình chi tiết sản phẩm
                // Intent intent = new Intent(MainboardUser.this, ProductDetailActivity.class);
                // intent.putExtra("PRODUCT_ID", item.getProductId());
                // startActivity(intent);
                Toast.makeText(MainboardUser.this, "Xem chi tiết: " + item.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadFavoriteData() {
        if (favoriteItemList == null) {
            favoriteItemList = new ArrayList<>();
        }
        favoriteItemList.clear();

        // Dữ liệu giả lập. THAY THẾ URL BẰNG URL THẬT TỪ FIREBASE STORAGE.
        favoriteItemList.add(new FavoriteItem("prod_101", "Khay mứt 5 ngăn gỗ hương", 1, IMAGE_URL_KHAY_MUT));
        favoriteItemList.add(new FavoriteItem("prod_102", "Tượng rắn Phúc Thái bằng đồng", 2, IMAGE_URL_TUONG_RAN));
        favoriteItemList.add(new FavoriteItem("prod_103", "Bộ đồ ăn sứ Bát Tràng tứ quý", 1, IMAGE_URL_BO_DO_AN));
        favoriteItemList.add(new FavoriteItem("prod_104", "Đèn lồng Hội An vải lụa", 3, IMAGE_URL_DEN_LONG));
        favoriteItemList.add(new FavoriteItem("prod_105", "Tranh Đồng Hồ - Đám cưới chuột", 1, IMAGE_URL_TRANH_DONG_HO));
        favoriteItemList.add(new FavoriteItem("prod_106", "Bình hoa gốm Bát Tràng men rạn", 1, IMAGE_URL_GOM_BAT_TRANG));
        favoriteItemList.add(new FavoriteItem("prod_107", "Nón lá bài thơ Huế", 5, null)); // Không có ảnh
        favoriteItemList.add(new FavoriteItem("prod_108", "Móc khóa hình chùa Một Cột", 10, IMAGE_URL_KHAY_MUT));
        favoriteItemList.add(new FavoriteItem("prod_109", "Bộ ấm trà tử sa Nghi Hưng", 1, IMAGE_URL_BO_DO_AN));
        favoriteItemList.add(new FavoriteItem("prod_110", "Tượng Phật Di Lặc bằng gỗ", 1, "")); // URL rỗng
        favoriteItemList.add(new FavoriteItem("prod_111", "Tranh lụa Hà Đông thiếu nữ", 1, IMAGE_URL_DEN_LONG));
        favoriteItemList.add(new FavoriteItem("prod_112", "Lục bình sứ vẽ vàng kim", 2, IMAGE_URL_GOM_BAT_TRANG));
        favoriteItemList.add(new FavoriteItem("prod_113", "Guốc mộc truyền thống", 1, null));
        favoriteItemList.add(new FavoriteItem("prod_114", "Chuông gió vỏ sò trang trí", 4, IMAGE_URL_TUONG_RAN));
        favoriteItemList.add(new FavoriteItem("prod_115", "Khăn rằn Nam Bộ", 2, IMAGE_URL_TRANH_DONG_HO));

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            Log.e("MainboardUser", "Adapter is null in loadFavoriteData. RecyclerView might not update.");
        }
    }
}