package com.example.houserentalapp.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    private String uid;
    private String name;
    private String email;
    private String phone;
    private String role;           // tenant | landlord | admin
    private String profileImage;
    private String bio;
    private String currency;       // user's preferred display currency
    private boolean isVerified;    // landlord verification by admin
    private boolean isActive;
    private boolean phoneVerified;
    private long createdAt;
    private long updatedAt;

    public User() { }

    public User(String uid, String name, String email, String phone, String role) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.isVerified = false;
        this.isActive = true;
        this.phoneVerified = false;
        this.currency = "INR";
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters & Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Legacy alias kept for compatibility with existing code
    @Exclude
    public String getname() { return name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    // Legacy alias
    @Exclude
    public String getImage() { return profileImage; }
    @Exclude
    public void setImage(String image) { this.profileImage = image; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getCurrency() { return currency != null ? currency : "INR"; }
    public void setCurrency(String currency) { this.currency = currency; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean isPhoneVerified() { return phoneVerified; }
    public void setPhoneVerified(boolean phoneVerified) { this.phoneVerified = phoneVerified; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
