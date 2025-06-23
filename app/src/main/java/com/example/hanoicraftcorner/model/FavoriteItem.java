package com.example.hanoicraftcorner.Model;

public class FavoriteItem {
    private String productId;
    private String name;
    private int quantity;
    private String imageUrl; // Thay đổi từ int imageResId sang String imageUrl

    // Constructor mặc định cần thiết cho Firestore nếu bạn đọc dữ liệu trực tiếp vào đối tượng này
    public FavoriteItem() {
        // Firestore cần constructor không tham số để deserialize
    }

    // Constructor để bạn tạo đối tượng trong code
    public FavoriteItem(String productId, String name, int quantity, String imageUrl) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.imageUrl = imageUrl; // Gán cho imageUrl
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Getter và Setter cho imageUrl
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}