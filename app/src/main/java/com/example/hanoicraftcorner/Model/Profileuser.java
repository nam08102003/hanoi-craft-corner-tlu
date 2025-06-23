package com.example.hanoicraftcorner.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName; // QUAN TRỌNG
import com.google.firebase.firestore.ServerTimestamp; // Cho createdAt, updatedAt

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Profileuser implements Serializable {

    @Exclude
    private String id; // ID của document, không lưu trữ như một trường trong document

    // Các trường này PHẢI KHỚP với tên trường trên Firestore (phân biệt chữ hoa/thường)
    // Hoặc sử dụng @PropertyName nếu tên biến Java khác
    private String email;
    private String username;
    private String avatarUrl; // Đảm bảo trường này là "avatarUrl" trên Firestore

    // Các trường bạn có thể thêm/sửa sau này
    private String fullName;
    private String phoneNumber;
    private Date birthDate; // Firestore sẽ lưu dưới dạng Timestamp
    private String idCardNumber;
    private String gender;
    private String address;
    private String profileType; // Nếu bạn sử dụng trường này

    @ServerTimestamp // Firestore sẽ tự động điền khi tạo
    private Date createdAt;
    @ServerTimestamp // Firestore sẽ tự động điền khi tạo/cập nhật nếu bạn set trong .update()
    private Date updatedAt;


    public Profileuser() {
        // Constructor rỗng cần thiết cho Firestore
    }

    // --- Getters ---
    @Exclude
    public String getId() { return id; }

    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getAvatarUrl() { return avatarUrl; } // Getter cho avatarUrl

    public String getFullName() {
        // Nếu fullName rỗng hoặc null, trả về username làm dự phòng
        return (fullName != null && !fullName.trim().isEmpty()) ? fullName : username;
    }
    public String getPhoneNumber() { return phoneNumber; }
    public Date getBirthDate() { return birthDate; }
    public String getIdCardNumber() { return idCardNumber; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public String getProfileType() { return profileType; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }


    // --- Setters ---
    public void setId(String id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setUsername(String username) { this.username = username; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; } // Setter cho avatarUrl
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }
    public void setIdCardNumber(String idCardNumber) { this.idCardNumber = idCardNumber; }
    public void setGender(String gender) { this.gender = gender; }
    public void setAddress(String address) { this.address = address; }
    public void setProfileType(String profileType) { this.profileType = profileType; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }


    // Phương thức này dùng để cập nhật dữ liệu lên Firestore
    // Đảm bảo các key trong map KHỚP với tên trường trên Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        // Không nên cho phép người dùng tự sửa email, username, createdAt qua màn hình này
        // map.put("email", email);
        // map.put("username", username);

        // Các trường người dùng có thể sửa
        map.put("fullName", fullName); // Sẽ tạo/cập nhật trường "fullName"
        map.put("phoneNumber", phoneNumber);
        map.put("birthDate", birthDate); // Firestore sẽ lưu là Timestamp
        map.put("idCardNumber", idCardNumber);
        map.put("gender", gender);
        map.put("address", address);
        map.put("avatarUrl", avatarUrl); // Cho phép cập nhật avatarUrl
        map.put("profileType", profileType); // Nếu bạn dùng

        // updatedAt nên được cập nhật bằng FieldValue.serverTimestamp() khi gọi .update()
        // map.put("updatedAt", updatedAt); // Không nên đặt cứng ở đây

        return map;
    }
}