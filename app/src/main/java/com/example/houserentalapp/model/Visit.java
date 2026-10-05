package com.example.houserentalapp.model;

public class Visit {
    private String id;
    private String propertyId;
    private String propertyTitle;
    private String propertyImage;
    private String tenantId;
    private String tenantName;
    private String landlordId;
    private String requestedDate;   // "yyyy-MM-dd"
    private String requestedTime;   // "HH:mm"
    private String status;   // pending | confirmed | rejected | completed | cancelled
    private String notes;
    private String landlordNotes;
    private long createdAt;
    private long updatedAt;

    public Visit() {
        status = "pending";
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public Visit(String propertyId, String propertyTitle, String propertyImage,
                 String tenantId, String tenantName, String landlordId,
                 String requestedDate, String requestedTime, String notes) {
        this();
        this.propertyId = propertyId;
        this.propertyTitle = propertyTitle;
        this.propertyImage = propertyImage;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.landlordId = landlordId;
        this.requestedDate = requestedDate;
        this.requestedTime = requestedTime;
        this.notes = notes;
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
    public String getRequestedDate() { return requestedDate; }
    public void setRequestedDate(String requestedDate) { this.requestedDate = requestedDate; }
    public String getRequestedTime() { return requestedTime; }
    public void setRequestedTime(String requestedTime) { this.requestedTime = requestedTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getLandlordNotes() { return landlordNotes; }
    public void setLandlordNotes(String landlordNotes) { this.landlordNotes = landlordNotes; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
