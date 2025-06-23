package com.example.hanoicraftcorner.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String email, password, fullname, role, avatar, phone, introduce, brand_name, status;
    private List<String> images;
    private Timestamp created_at, updated_at;

    public User() {}

    public User(String avatar, String brand_name, Timestamp created_at, String fullname, String email, List<String> images, String introduce, String password, String phone, String role, String status, Timestamp updated_at) {
        this.avatar = avatar;
        this.brand_name = brand_name;
        this.created_at = created_at;
        this.fullname = fullname;
        this.email = email;
        this.images = images;
        this.introduce = introduce;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.updated_at = updated_at;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }
}
