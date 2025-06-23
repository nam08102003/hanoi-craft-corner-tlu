package com.example.hanoicraftcorner.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Experience {
    private String id;
    private String title;
    private String imageUrl;
    private String contentUrl; // Link to the full article
    @ServerTimestamp
    private Date createdAt;

    public Experience() {
        // Required for Firestore
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getContentUrl() { return contentUrl; }
    public void setContentUrl(String contentUrl) { this.contentUrl = contentUrl; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
} 