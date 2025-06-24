package com.example.hanoicraftcorner.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Artisan {
    private String id;
    private String name;
    private String craft_type;
    private String brand_name;
    private String phone;
    private String location;
    private int status;
    private String introduce;
    private boolean hot;
    @ServerTimestamp
    private Date created_at;
    private String imageUrl;

    public Artisan() {
        // Required for Firestore
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCraft_type() { return craft_type; }
    public void setCraft_type(String craft_type) { this.craft_type = craft_type; }
    public String getBrand_name() { return brand_name; }
    public void setBrand_name(String brand_name) { this.brand_name = brand_name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getIntroduce() { return introduce; }
    public void setIntroduce(String introduce) { this.introduce = introduce; }
    public boolean isHot() { return hot; }
    public void setHot(boolean hot) { this.hot = hot; }
    public Date getCreated_at() { return created_at; }
    public void setCreated_at(Date created_at) { this.created_at = created_at; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
} 