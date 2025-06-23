package com.example.hanoicraftcorner.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Category {
    private String id;
    private String name;
    private String parentId;
    private String imageUrl; // I'll add a sample image URL field

    @ServerTimestamp
    private Date createdAt;

    public Category() {
        // Required for Firestore
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
} 