package com.example.houserentalapp.model;

import java.util.ArrayList;
import java.util.List;

public class RentalApplication {
    private String id;
    private String propertyId;
    private String propertyTitle;
    private String propertyImage;
    private String tenantId;
    private String tenantName;
    private String landlordId;
    private String message;
    private String status;   // pending | reviewing | accepted | rejected | withdrawn
    private List<String> documents;
    private long appliedAt;
    private long updatedAt;

    public RentalApplication() {
        documents = new ArrayList<>();
        status = "pending";
        appliedAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public RentalApplication(String propertyId, String propertyTitle, String propertyImage,
                              String tenantId, String tenantName, String landlordId, String message) {
        this();
        this.propertyId = propertyId;
        this.propertyTitle = propertyTitle;
        this.propertyImage = propertyImage;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.landlordId = landlordId;
        this.message = message;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }
    public String getPropertyTitle() { return propertyTitle; }
    public void setPropertyTitle(String propertyTitle) { this.propertyTitle = propertyTitle; }
    public String getPropertyImage() { return propertyImage; }
    public void setPropertyImage(String propertyImage) { this.propertyImage = propertyImage; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }
    public String getLandlordId() { return landlordId; }
    public void setLandlordId(String landlordId) { this.landlordId = landlordId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<String> getDocuments() { return documents; }
    public void setDocuments(List<String> documents) { this.documents = documents; }
    public long getAppliedAt() { return appliedAt; }
    public void setAppliedAt(long appliedAt) { this.appliedAt = appliedAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
