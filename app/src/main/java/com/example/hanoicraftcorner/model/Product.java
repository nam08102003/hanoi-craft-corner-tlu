package com.example.hanoicraftcorner.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Product {
    private String name;
    private String description;
    private String category;
    private String price;
    private String quantity;
    private String status;
    private List<String> images;
    private String artisan;
    @ServerTimestamp()
    private Timestamp created_at;
    @ServerTimestamp()
    private Timestamp updated_at;

    public Product() {
        // Required empty constructor
    }

    public Product(String artisan, String category, Timestamp created_at, String description, List<String> images, String name, String price, String quantity, String status, Timestamp updated_at) {
        this.artisan = artisan;
        this.category = category;
        this.created_at = created_at;
        this.description = description;
        this.images = images;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
        this.updated_at = updated_at;
    }

    // Getter & Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public String getArtisan() { return artisan; }
    public void setArtisan(String artisan) { this.artisan = artisan; }

    public Timestamp getCreated_at() { return created_at; }
    public void setCreated_at(Timestamp created_at) { this.created_at = created_at; }

    public Timestamp getUpdated_at() { return updated_at; }
    public void setUpdated_at(Timestamp updated_at) { this.updated_at = updated_at; }
}
