package com.example.houserentalapp.model;

public class Review {
    private String id;
    private String propertyId;
    private String tenantId;
    private String tenantName;
    private String tenantImage;
    private float rating;     // 1.0 – 5.0
    private String comment;
    private String landlordResponse;
    private long createdAt;
    private long updatedAt;

    public Review() {
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public Review(String propertyId, String tenantId, String tenantName,
                  float rating, String comment) {
        this();
        this.propertyId = propertyId;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.rating = rating;
        this.comment = comment;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }
    public String getTenantImage() { return tenantImage; }
    public void setTenantImage(String tenantImage) { this.tenantImage = tenantImage; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getLandlordResponse() { return landlordResponse; }
    public void setLandlordResponse(String landlordResponse) { this.landlordResponse = landlordResponse; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
