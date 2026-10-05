package com.example.houserentalapp.model;

import com.google.firebase.database.PropertyName;
import java.util.ArrayList;
import java.util.List;

public class Property {
    private String id;
    private String landlordId;
    private String landlordName;
    private String title;
    private String description;
    private String propertyType;
    private double price;
    private String currency;
    private double deposit;
    private String address;
    private String city;
    private String state;
    private String country;
    private double latitude;
    private double longitude;
    private int bedrooms;
    private int bathrooms;
    private double area;
    private String furnished;
    private boolean parkingAvailable;
    private boolean petFriendly;
    private List<String> amenities;
    private String availableFrom;
    private boolean isAvailable;
    private boolean isFeatured;
    private boolean isVerified;
    private String status;
    private String coverImage;
    private long viewCount;
    private long savedCount;
    private float averageRating;
    private int reviewCount;
    private long createdAt;
    private long updatedAt;

    public Property() {
        amenities = new ArrayList<>();
        isAvailable = true;
        isFeatured = false;
        isVerified = false;
        status = "active";
        currency = "INR";
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLandlordId() { return landlordId; }
    public void setLandlordId(String landlordId) { this.landlordId = landlordId; }
    public String getLandlordName() { return landlordName; }
    public void setLandlordName(String landlordName) { this.landlordName = landlordName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getFullLocation() {
        if (address == null || address.isEmpty()) {
            return city;
        }
        return address + ", " + city;
    }

    @PropertyName("isAvailable")
    public boolean isAvailable() { return isAvailable; }
    @PropertyName("isAvailable")
    public void setAvailable(boolean available) { isAvailable = available; }

    @PropertyName("isFeatured")
    public boolean isFeatured() { return isFeatured; }
    @PropertyName("isFeatured")
    public void setFeatured(boolean featured) { isFeatured = featured; }

    @PropertyName("isVerified")
    public boolean isVerified() { return isVerified; }
    @PropertyName("isVerified")
    public void setVerified(boolean verified) { isVerified = verified; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public int getBedrooms() { return bedrooms; }
    public void setBedrooms(int bedrooms) { this.bedrooms = bedrooms; }
    public int getBathrooms() { return bathrooms; }
    public void setBathrooms(int bathrooms) { this.bathrooms = bathrooms; }
    public double getArea() { return area; }
    public void setArea(double area) { this.area = area; }
    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }
    public String getFurnished() { return furnished; }
    public void setFurnished(String furnished) { this.furnished = furnished; }
    public float getAverageRating() { return averageRating; }
    public void setAverageRating(float averageRating) { this.averageRating = averageRating; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
