package com.example.hanoicraftcorner.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Category {
    private String id;
    private String name;
    private String parentId;
    @ServerTimestamp
    private Date createdAt;
    private String updatedAt;
    private String imageUrl; // I'll add a sample image URL field

    public Category(String name, String parentId, Date createdAt, String updatedAt) {
        this.name = name;
        this.parentId = parentId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
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