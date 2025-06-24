package com.example.hanoicraftcorner.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.List;

public class Product {
    private String name;
    private String description;
    private String category; // reference to categories
    private String price;
    private String quantity;
    private String status;
    private List<String> images;
    private String user_id; // reference to users
    @ServerTimestamp
    private Date created_at;
    @ServerTimestamp
    private Date updated_at;

    public Product() {
        // Required for Firestore
    }

    public Product(String name, String description, String category, String price, String quantity, String status, List<String> images, String user_id) {
        this.name = name != null ? name : "";
        this.description = description != null ? description : "";
        this.category = category != null ? category : "";
        this.price = price != null ? price : "";
        this.quantity = quantity != null ? quantity : "";
        this.status = status != null ? status : "";
        this.images = images;
        this.user_id = user_id != null ? user_id : "";
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price != null ? price : "";
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity != null ? quantity : "";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status != null ? status : "";
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id != null ? user_id : "";
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}
